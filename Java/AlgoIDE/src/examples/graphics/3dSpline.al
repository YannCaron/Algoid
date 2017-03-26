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
	set pts;

	// render line to viewport
	set render = function () {
		set curveArray = array{};

		pts.each (function (item) {
			item.transformPersp();
			curveArray.add(item.px);
			curveArray.add(item.py);
		});

		algo.setColor(7);
		algo.setAlpha(0.25);
		algo.curvedPoly(curveArray);

		algo.setAlpha(1);
		algo.curve(curveArray);

		algo.setColor(3);
		algo.setAlpha(0.25);
		algo.path(curveArray);

	};

};

// the cube points
set pts = array {
	pt3d.clone (0, -200, 0),
	pt3d.clone (50, -100, 0),
	pt3d.clone (250, 0, 0),
	pt3d.clone (50, 100, 0),
	pt3d.clone (0, 200, 0),

	pt3d.clone (35, -100, 35),
	pt3d.clone (176, 0, 176),
	pt3d.clone (35, 100, 35),

	pt3d.clone (0, -100, 50),
	pt3d.clone (0, 0, 250),
	pt3d.clone (0, 100, 50),

	pt3d.clone (-35, -100, 35),
	pt3d.clone (-176, 0, 176),
	pt3d.clone (-35, 100, 35),

	pt3d.clone (-50, -100, 0),
	pt3d.clone (-250, 0, 0),
	pt3d.clone (-50, 100, 0),

	pt3d.clone (-35, -100, -35),
	pt3d.clone (-176, 0, -176),
	pt3d.clone (-35, 100, -35),

	pt3d.clone (0, -100, -50),
	pt3d.clone (0, 0, -250),
	pt3d.clone (0, 100, -50),

	pt3d.clone (35, -100, -35),
	pt3d.clone (176, 0, -176),
	pt3d.clone (35, 100, -35),

};

// the cube faces
set faces = array {
	face.clone(array {pts[0], pts[1], pts[2], pts[3], pts[4]}),
	face.clone(array {pts[0], pts[5], pts[6], pts[7], pts[4]}),
	face.clone(array {pts[0], pts[8], pts[9], pts[10], pts[4]}),
	face.clone(array {pts[0], pts[11], pts[12], pts[13], pts[4]}),
	face.clone(array {pts[0], pts[14], pts[15], pts[16], pts[4]}),
	face.clone(array {pts[0], pts[17], pts[18], pts[19], pts[4]}),
	face.clone(array {pts[0], pts[20], pts[21], pts[22], pts[4]}),
	face.clone(array {pts[0], pts[23], pts[24], pts[25], pts[4]})
};

set a = -2;
set b = 0.5;
set ax = 0;
set ay = 0;

// count the number of element to draw
set count = function () {
	set i;
	i += faces.length () * 3;
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
