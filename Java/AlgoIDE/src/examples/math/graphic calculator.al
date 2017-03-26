/*
Graphic Calculator sample code.
Written by Yann Caron at 14-05-2012
*/

set right = algo.getWidth() / 2;
set left = - right;
set bottom = algo.getHeight() / 2;
set top = - bottom;

set interval = 10;
set scale = 50;

set getFormula = function () {
	text.output("ex: x * x + 1");
	return text.inputText("f(x) = ");
};

set init = function () {
	algo.goTo (left, 0);
	algo.lineTo (right, 0);
	algo.goTo (0, bottom);
	algo.lineTo (0, top);

	for (set i = 0; i<=right; i += scale) {
		algo.goTo (i, 0);
		algo.lineTo (i, -interval);
		algo.goTo (-i, 0);
		algo.lineTo (-i, -interval);
	}

	for (set i = 0; i<=bottom; i += scale) {
		algo.goTo (0, i);
		algo.lineTo (interval, i);
		algo.goTo (0, -i);
		algo.lineTo (interval, -i);
	}

	algo.goTo (left * scale, 0);
};

set draw = function (formula) {
	set x; set y;

	set execf = "y = " .. formula .. ";";

	for (set pointer = left; pointer <= right; pointer += interval) {
		x = pointer / scale;
		util.eval(execf);
		algo.lineTo(x * scale, -y * scale);
	}
};

ui.showAlgo();

set formula = getFormula ();
init();
draw(formula);
