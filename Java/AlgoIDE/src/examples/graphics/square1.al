/*
Square1 sample code.
Written by Yann Caron at 14-05-2012
*/

set square = function (size) {
	for (set i; i < 4; i++) {
		algo.go (size);
		algo.turnLeft (90);
	}
};

ui.showAlgo();
square(100);