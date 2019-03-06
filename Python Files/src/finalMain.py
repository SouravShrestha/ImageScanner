from __future__ import division
from __future__ import print_function
import os
import cv2
import warnings
import sys
import warnings
import os
import shutil
import argparse
import cv2
import editdistance
from DataLoader import DataLoader, Batch
from Model import Model, DecoderType
from SamplePreprocessor import preprocess
from WordSegmentation import wordSegmentation, prepareImg

class FilePaths:
	fnCharList = '../model/charList.txt'
	fnAccuracy = '../model/accuracy.txt'
	fnTrain = '../data/'
	fnInfer = '../data/y.jpg'
	fnCorpus = '../data/corpus.txt'

def mainD():
	imgFiles = os.listdir('../data/MainInputToSplit/')
	# shutil.rmtree('../data/SplitOutputs')
	# os.makedirs('../data/SplitOutputs')
	for (i,f) in enumerate(imgFiles):
		# print('Segmenting words of sample %s'%f)
		img = prepareImg(cv2.imread('../data/MainInputToSplit/%s'%f), 50)
		res = wordSegmentation(img, kernelSize=25, sigma=11, theta=7, minArea=100)
		# print('Segmented into %d words'%len(res))
		for (j, w) in enumerate(res):
			(wordBox, wordImg) = w
			(x, y, w, h) = wordBox
			cv2.imwrite('../data/SplitOutputs/%d.png'%j, wordImg)
			cv2.rectangle(img,(x,y),(x+w,y+h),0,1)
	return toText()

def train(model, loader):
	"train NN"
	epoch = 0 # number of training epochs since start
	bestCharErrorRate = float('inf') # best valdiation character error rate
	noImprovementSince = 0 # number of epochs no improvement of character error rate occured
	earlyStopping = 5 # stop training after this number of epochs without improvement
	while True:
		epoch += 1
		print('Train NN')
		loader.trainSet()
		while loader.hasNext():
			iterInfo = loader.getIteratorInfo()
			batch = loader.getNext()
			loss = model.trainBatch(batch)
		charErrorRate = validate(model, loader)
		if charErrorRate < bestCharErrorRate:
			bestCharErrorRate = charErrorRate
			noImprovementSince = 0
			model.save()
			open(FilePaths.fnAccuracy, 'w').write('Validation character error rate of saved model: %f%%' % (charErrorRate*100.0))
		else:
			noImprovementSince += 1
		if noImprovementSince >= earlyStopping:
			break

def validate(model, loader):
	loader.validationSet()
	numCharErr = 0
	numCharTotal = 0
	numWordOK = 0
	numWordTotal = 0
	while loader.hasNext():
		iterInfo = loader.getIteratorInfo()
		batch = loader.getNext()
		(recognized, _) = model.inferBatch(batch)
		for i in range(len(recognized)):
			numWordOK += 1 if batch.gtTexts[i] == recognized[i] else 0
			numWordTotal += 1
			dist = editdistance.eval(recognized[i], batch.gtTexts[i])
			numCharErr += dist
			numCharTotal += len(batch.gtTexts[i])
	charErrorRate = numCharErr / numCharTotal
	wordAccuracy = numWordOK / numWordTotal
	return charErrorRate

def infer(model, fnImg):
	img = preprocess(cv2.imread(fnImg, cv2.IMREAD_GRAYSCALE), Model.imgSize)
	batch = Batch(None, [img])
	(recognized, probability) = model.inferBatch(batch, True)
	return recognized[0]

def toText():
	parser = argparse.ArgumentParser()
    
	parser.add_argument("--train", help="train the NN", action="store_true")
	parser.add_argument("--validate", help="validate the NN", action="store_true")
	parser.add_argument("--beamsearch", help="use beam search instead of best path decoding", action="store_true")
	parser.add_argument("--wordbeamsearch", help="use word beam search instead of best path decoding", action="store_true")
	args = parser.parse_args()

	decoderType = DecoderType.BestPath
	if args.beamsearch:
		decoderType = DecoderType.BeamSearch
	elif args.wordbeamsearch:
		decoderType = DecoderType.WordBeamSearch

	# train or validate on IAM dataset	
	if args.train or args.validate:
		# load training data, create TF model
		loader = DataLoader(FilePaths.fnTrain, Model.batchSize, Model.imgSize, Model.maxTextLen)

		# save characters of model for inference mode
		open(FilePaths.fnCharList, 'w').write(str().join(loader.charList))
		
		# save words contained in dataset into file
		open(FilePaths.fnCorpus, 'w').write(str(' ').join(loader.trainWords + loader.validationWords))

		# execute training or validation
		if args.train:
			model = Model(loader.charList, decoderType)
			train(model, loader)
		elif args.validate:
			model = Model(loader.charList, decoderType, mustRestore=True)
			validate(model, loader)

	# infer text on test image
	else:
		model = Model(open(FilePaths.fnCharList).read(), decoderType, mustRestore=True)
		imgFiles = os.listdir('../data/SplitOutputs')
		answer =""
		for (i,f) in enumerate(imgFiles):
			answer = answer + infer(model, '../data/SplitOutputs/%s'%f) + " "
			os.remove('../data/SplitOutputs/%s'%f)
		return answer
	return "Hey"

if __name__ == '__main__':
	mainD()