/*
Square3 sample code.
Variation of square2 with forward.
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
	algo.go(30);
	square(100);
}