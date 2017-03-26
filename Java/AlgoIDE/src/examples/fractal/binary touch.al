/*
binary tree touch sample code.
Written by Yann Caron at 14-04-2013
*/
set branches = 5;

set tree = function (it, size, angle, factor) {
	if (it==0) { algo.go (size);
		algo.turnLeft (180);
		algo.go (size);
	} else {
		it --; // decrement
		size /= factor; // factor
		algo.go (size);

		algo.turnLeft (angle);
		tree (it, size, angle, factor ); // recursion

		algo.turnLeft (180 - (angle*2));
		tree (it, size, angle, factor ); // recursion

		algo.turnLeft (angle);
		algo.go (size);
	}
};

ui.showAlgo();
algo.hide ();
algo.setStack((math.pow(2, branches + 1) - 2) * 2);

set draw = function (x, y) { // main
	set a = -math.aim (x, y);
	set f = 1.7;
	set size = math.diagonal (x, y);
	algo.goTo (0, 75);
	tree (branches, size, a, f); // initial parameters
	algo.rotateTo (0);
};

draw (75, -150);
algo.onDrag (draw);
