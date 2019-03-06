from flask import Flask,jsonify
import finalMain as fm

app = Flask(__name__)

@app.route("/")
def home():
    return fm.mainD()

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)