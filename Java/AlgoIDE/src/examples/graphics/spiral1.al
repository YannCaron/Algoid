/*
spiral sample code.
Written by Yann Caron at 14-06-2012
*/

set spiral = function (segments, initLength, increment, angle) {
	set length = initLength;

	for (set i = 0; i < segments; i++) {
		algo.go(length);
		algo.turnLeft(angle);
		
		length += increment;
	}
};

ui.showAlgo();
spiral(50, 10, 10, 121);