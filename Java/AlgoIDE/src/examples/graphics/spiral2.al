/*
spiral sample code.
Written by Yann Caron at 14-06-2012
*/

set spiral = function (segments, length, initAngle, increment) {
	set angle = initAngle;

	for (set i = 0; i < segments; i++) {
		algo.go(length);
		algo.turnLeft(angle);
		
		angle += increment;
	}
};

ui.showAlgo();
spiral(90, 50, 2, 20);