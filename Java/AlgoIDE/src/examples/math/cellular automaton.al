/*
Conway's Game of Life
Written by CyaNn 04/02/2013

rules :
- a cell with 3 alive neighbours become alive
- a cell with 2 alive neighbours does not change
- in other cases the cell disapears

*/
set SIZE = 20;
set SCALE = 10;
set cells = array {};
set render = array {};

set cell = object () {
	set c = 0;
	set r = 0;
	set alive = true;
	set next = true;

	set eachNeighbours = function (f) {
		set cfrom = math.max (c-1, 0);
		set cto = math.min (c+1, SIZE-1);
		set rfrom = math.max (r-1, 0);
		set rto = math.min (r+1, SIZE-1);

		for (set row=rfrom; row<=rto; row++) {
			for (set col=cfrom; col<=cto; col++) {
				if (!(col == c && row == r)) {
					f(cells[row][col]);
				}
			}
		}
	};

	set evalNext = function () {
		set count = 0;
		eachNeighbours (function (item) {
			if (item.alive) {
				count++;
			}
		});

		if (count == 3) {
			next = true;
		} else if (count == 2) {
			next = alive;
		} else {
			next = false;
		}
	};

	set apply = function () {
		alive = next;
	};

	set clone = function (c, r) {
		set o = new this;
		set o.c = c;
		set o.r = r;
		set o.alive = math.random(2);
		return o;
	};
};

set initialize = function () {

	SIZE.loopFor (function (r) {
		set row = array {};
		set rrow = array {};
		SIZE.loopFor (function (c) {
			row.add(cell.clone(c, r));
			rrow.add(-1);
		});

		cells.add(row);
		render.add(rrow);
	});
};

initialize();

set evalNext = function () {
	cells.eachItem(function (item) {
		item.evalNext();
	});
	cells.eachItem(function (item) {
		item.apply();
	});
};

set draw = function () {
	cells.each(function (line, row) {
		line.each (function (item, col) {
			if (item.alive) {
				render[row][col] = 3;
			} else {
				render[row][col] = -1;
			}
		});
	});

	set stamp = algo.stamp.clone(render, SCALE);
	stamp.draw();
	stamp.delete();
};

// initialize
ui.showAlgo();
algo.hide();
algo.setStack (1);
draw ();

util.pulse (function () {
	evalNext();
	draw ();
}, 200);
