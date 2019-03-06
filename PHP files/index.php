<?php

	$image = $_POST["IMG"];
	$decodeImage = base64_decode("$image");
	$path = "D:\Major Project\HandToText\data\MainInputToSplit";
	file_put_contents("$path/new.jpg",$decodeImage);

?>