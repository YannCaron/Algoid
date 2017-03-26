/*
kochfractal sample code.
Written by Yann Caron at 11-12-2012
*/

set kochfractal = function (it, size) {
	if (it == 0) {
		algo.go (size);
	} else  {
		it --; // decrement
		size /= 3; // the third
		kochfractal (it, size); // recursion 1

		algo.turnRight (60);
		kochfractal (it, size); // recursion 2

		algo.turnLeft (120);
		kochfractal (it, size); // recursion 3

		algo.turnRight (60);
		kochfractal (it, size); // recursion 4
	}
};

// main
ui.showAlgo();
algo.goTo(70, 100);
algo.hide ();

for (set a; a<3; a++) {
	kochfractal (3, 250); // initial parameters
	algo.turnLeft (120);
}
