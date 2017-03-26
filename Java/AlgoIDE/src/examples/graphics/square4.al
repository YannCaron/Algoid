/*
Square4 sample code.
Variation of square3 with event.
Written by Yann Caron at 14-05-2012
*/

set square = function (size) {
	for (set i; i < 4; i++) {
		algo.go (size);
		algo.turnLeft (90);
	}
};

set init = function (x, y) {
	algo.clear();
	algo.goTo(0, 0);
};

set draw = function (x, y) {
	set dist = math.sqrt (math.dbl (x), math.dbl (y));
	algo.turnLeft(30);
	algo.go(dist);
	square(100);
};

ui.showAlgo();
algo.onClick(init);
algo.onDrag(draw);

