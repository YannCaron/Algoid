/*
Space invaders inspire from Tomohiro Nishikado 1978 game
Written by Yann Caron at 23-05-2012
*/

// constant
set COLUMNS = 5;
set ROWS = 3;
set GRID = 75;
set TOTAL = COLUMNS * ROWS;
set CENTER_X = - COLUMNS * GRID / 2;
set CENTER_Y = - GRID * 5;

// declaring stamps
set invader1 = algo.stamp.clone (array {
	{-1, -1, 10, -1, -1, -1, -1, -1, 10, -1, -1,},
	{-1, -1, -1, 10, -1, -1, -1, 10, -1, -1, -1,},
	{-1, -1, 10, 10, 10, 10, 10, 10, 10, -1, -1,},
	{-1, 10, 10, 12, 10, 10, 10, 12, 10, 10, -1,},
	{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,},
	{10, -1, 10, 10, 10, 10, 10, 10, 10, -1, 10,},
	{10, -1, 10, -1, -1, -1, -1, -1, 10, -1, 10,},
	{-1, -1, -1, 10, 10, -1, 10, 10, -1, -1, -1}}, 5); // invader 1

set invader2 = algo.stamp.clone (array {
	{-1, -1, 10, -1, -1, -1, -1, -1, 10, -1, -1,},
	{10, -1, -1, 10, -1, -1, -1, 10, -1, -1, 10,},
	{10, -1, 10, 10, 10, 10, 10, 10, 10, -1, 10,},
	{10, 10, 10, 12, 10, 10, 10, 12, 10, 10, 10,},
	{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,},
	{-1, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1,},
	{-1, -1, 10, -1, -1, -1, -1, -1, 10, -1, -1,},
	{-1, 10, -1, -1, -1, -1, -1, -1, -1, 10, -1}}, 5); // invader 1 anim

set gun1 = algo.stamp.clone (array {
	{-1, -1, -1, -1, -1, 11, -1, -1, -1, -1, -1,},
	{-1, -1, -1, -1, 11, 11, 11, -1, -1, -1, -1,},
	{-1, -1, -1, -1, 11, 11, 11, -1, -1, -1, -1,},
	{-1, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1,},
	{11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,},
	{11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11}}, 5); // gun

set bulletUp = algo.stamp.clone (array {{11}, {11}, {11}, {11}}, 5); // bullet up
set bulletDown = algo.stamp.clone (array {{12}, {12}, {12}, {12}}, 5); // bullet down

// declaring
set invader = object () {
	set anim = 0;
	set gridX = 0;	set gridY = 0;
	set x = 0;	set y = 0;
	set sprite = array {invader1, invader2};

	set init = function (id) {
		gridX = id % COLUMNS;
		gridY = math.ceil(id / COLUMNS) - 1;
		gridX *= GRID;
		gridY *= GRID;
	};

	set applyPath = function (path) {
		x = CENTER_X + gridX + (path.x * GRID);
		y = CENTER_Y + gridY + (path.y * GRID);
	};

	set hit = function (bX, bY) {
		if (math.diagonal(x - bX, y - bY) < 35) {
			return true;
		}
		return false;
	};

	set draw = function () {
		anim = (anim + 1) % 2;
		algo.goTo(x, y);
		sprite[anim].draw();
	};

};

set gun = object () {
	set x = 0;
	set GUN_Y = 4 * GRID;
	set sprite = gun1;

	set draw = function () {
		algo.goTo(x, GUN_Y);
		sprite.draw();
	};
};

set bullet = object () {
	set sprite;
	set way;
	set x;
	set y;

	set init = function (up, x, y) {
		x = x;
		y = y;
		if (up) {
			sprite = bulletUp;
			way = -20;
		} else {
			sprite = bulletDown;
			way = 20;
		}
	};

	set isIn = function (objs) {
		set l = objs.length() - 1;
		return y.between (objs[0].y, objs[l].y);
	};

	set hit = function (objects) {
		set h = false;
		if (isIn (objects)) {
			objects.each (function (item, i) {
				if (item.hit(x, y)) {
					h = true;
					objects.remove(i);
				}
			});
		}
		if (y < -algo.getHeight() / 2) {
			h = true;
		}
		return h;
	};

	set draw = function () {
		y += way;
		algo.goTo(x, y);
		sprite.draw();
	};
};

set path = object () {
	set W = 3;
	set H = 5;
	set step = 1;
	set x = -2;
	set y = 0;
	set end = false;

	set next = function () {
		x += step;
		if (x >= W || x <= -W + 1) {
			step *= -1; // invert step
			x += step;
			y++;
		}

		if (y >= H) {
			end = true;
		}
	};

};

set loops = 0;
set invaders = array {};
set bullets = array {};

TOTAL.loopFor (function (i) {
	invaders[i] = new invader;
	invaders[i].init(i+1);
});

// run
set runGame = function () {
	algo.autoClear();
	gun.draw();
	bullets.each (function (item, i) {
		item.draw();
		set b = item.hit(invaders);
		if (b) {
			bullets.remove(i);
		}
	});

	if (loops == 0) {
		path.next();
		invaders.each (function (item) {
			item.applyPath(path);
		});
	}
	loops = (loops + 1) % 5;

	invaders.each (function (item) {
		item.draw();
	});

};

set action = function (x, y) {
	gun.x = x;
	set b = new bullet;
	b.init(true, x, gun.GUN_Y);
	bullets[bullets.length()] = b;
};

// initialize
ui.showAlgo();
algo.hide();

// events
algo.onClick(action);
util.pulse(runGame, 100);
