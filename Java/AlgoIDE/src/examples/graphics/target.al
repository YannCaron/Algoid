/*
Target sample code.
Written by Yann Caron at 14-05-2012
*/

set anim = 0;

set draw = function (x, y) {
	algo.clear();

	set diag = math.diagonal(x, y);
	set aim = math.aim(x, y);

	set s1 = diag;
	set s2 = s1 / 2;
	anim += 5;

	algo.lineTo(x, y);
	algo.circle (diag);
	algo.circle (anim % s1);
	algo.circle ((anim - s2) % s1);
	algo.goTo(0, 0);
	algo.circle (diag * 2);
	algo.rotateTo (aim);

};

ui.showAlgo();
algo.onDrag(draw);