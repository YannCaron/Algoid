/*
Demo sample code.
Written by Yann Caron at 14-10-2012
To execute it touch run menu.
To remove step-by-step mode
please touch menu execution step-by-step

enjoy this demo
*/

set poly = function (size, n) {
	loop (n) {
		algo.go (size);
		algo.turnLeft (360 / n);
	}
};

ui.showAlgo();

loop(20) {
	poly (70, 10);
	algo.turnLeft (360 / 20);
}
