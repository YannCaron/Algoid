/*
binary tree sample code.
Written by Yann Caron at 11-12-2012
*/
set tree = function (it, size) {
	if (it==0) {
		algo.go (size);
		algo.turnLeft (180);
		algo.go (size);
	} else  {
		it --; // decrement
		size /= 2; // half
		algo.go (size);

		algo.turnLeft (45);
		tree (it, size); // recursion
		
		algo.turnLeft (90);
		tree (it, size); // recursion
		
		algo.turnLeft (45);
		algo.go (size);
	}
};

// main
ui.showAlgo();
algo.go (-75);
algo.hide ();
tree (5, 200); // initial parameters
