/*
Square2 sample code.
Variation of square 1 with rotation and loop.
Written by Yann Caron at 14-05-2012
*/

set square = function (size) {
	for (set i; i < 4; i++) {
		algo.go (size);
		algo.turnLeft (90);
	}
};

ui.showAlgo();

for (set i; i < 12; i++) {
	algo.turnLeft(30);
	square(100);
}