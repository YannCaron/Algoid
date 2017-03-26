/*
3d sample code.
Written by Yann Caron at 27-05-2012
*/

// the point 3d object
set pt3d = object () {
	set x;	set y;	set z;
	set px = 0; set py = 0; set FOCAL = 1000;

	// rotate on X axis
	set rotateX = function (a){

		// calc rotation
		set ca = math.cos(a);
		set sa = math.sin(a);

		// rotate on X
		set ty = y * ca - z * sa;
		set tz = y * sa + z * ca;

		// apply
		y = ty;
		z = tz;

	};

	// rotate on Y axis
	set rotateY = function (a){

		// calc rotation
		set ca = math.cos(a);
		set sa = math.sin(a);

		// rotate on Y
		set tx = x * ca + z * sa;
		set tz = x * -sa + z * ca;

		// apply
		x = tx;
		z = tz;

	};

	set transformPersp = function () {
		set pers = FOCAL / (FOCAL - z);
		px = x * pers;
		py = y * pers;
		return pers;
	};

};

// the face object
set face = object () {
	set pt1;
	set pt2;
	set pt3;
	set pt4;

	// render line to viewport
	set render = function () {
		set f1 = pt1.transformPersp();
		set f2 = pt2.transformPersp();
		pt3.transformPersp();
		pt4.transformPersp();

		algo.goTo(pt1.px, pt1.py);
		algo.disc(20 * f1);
		algo.lineTo(pt2.px, pt2.py);
		algo.disc(20 * f2);
		algo.lineTo(pt3.px, pt3.py);
		algo.lineTo(pt4.px, pt4.py);

	};
};

// the cube points
set pts = array {
	pt3d.clone (-100, -100, 100),
	pt3d.clone (100, -100, 100),
	pt3d.clone (100, 100, 100),
	pt3d.clone (-100, 100, 100),
	pt3d.clone (-100, -100, -100),
	pt3d.clone (100, -100, -100),
	pt3d.clone (100, 100, -100),
	pt3d.clone (-100, 100, -100)
};

// the cube faces
set faces = array {
	face.clone(pts[0], pts[1], pts[2], pts[3]),
	face.clone(pts[0], pts[4], pts[5], pts[1]),
	face.clone(pts[0], pts[3], pts[7], pts[4]),
	face.clone(pts[1], pts[5], pts[6], pts[2]),
	face.clone(pts[2], pts[3], pts[7], pts[6]),
	face.clone(pts[7], pts[6], pts[5], pts[4]),
};

set a = -2;
set b = 0.5;
set ax = 0;
set ay = 0;

// count the number of element to draw
set count = function () {
	set i;
	i += faces.length () * 5;
	return i;
};

// render loop
set render = function () {
	pts.each(function (item) {
		item.rotateX(-b);
		item.rotateY(a);
	});

	faces.each (function (item) {
		item.render();
	});
};

set anchor = function (x, y) {
	ax = x;
	ay = y;
};

// rotate on touch event
set rotate = function (x, y) {
	a = (x - ax) % 20;
	b = (y - ay) % 20;

	render();

	ax = x;
	ay = y;
};

// program
ui.showAlgo();
algo.hide();
algo.setStack(count());

util.pulse(render, 40);
algo.onClick(anchor);
algo.onDrag (rotate);
