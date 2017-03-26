/*
Breakout clone.
Written by Yann Caron at 13-05-2013

enjoy this mini game
*/
// ---- stamps
set brickBlueST = algo.stamp.clone (array {
	{-1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1},
	{ 1,  1,  9,  9,  9,  9,  9,  9,  9,  9,  9,  1},
	{ 1,  9,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1},
	{ 1,  9,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1},
	{-1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, -1}}, 7);

set brickRedST = algo.stamp.clone (array {
	{-1,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4, -1},
	{ 4,  4, 12, 12, 12, 12, 12, 12, 12, 12, 12,  4},
	{ 4, 12,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4},
	{ 4, 12,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4},
	{-1,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4, -1}}, 7);

set brickMagentaST = algo.stamp.clone (array {
	{-1,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5, -1},
	{ 5,  5, 13, 13, 13, 13, 13, 13, 13, 13, 13,  5},
	{ 5, 13,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5},
	{ 5, 13,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5},
	{-1,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5, -1}}, 7);

set ballST = algo.stamp.clone (array {
	{-1, -1,  3,  3, -1, -1},
	{-1,  3, 11, 11,  3, -1},
	{ 3, 11,  3,  3,  3,  3},
	{ 3, 11,  3,  3,  3,  3},
	{-1,  3,  3,  3,  3, -1},
	{-1, -1,  3,  3, -1, -1}}, 5);

set racketST = algo.stamp.clone (array {
	{-1,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3, -1},
	{ 3, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,  3},
	{ 3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3},
	{-1,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3, -1}}, 7);


// ---- objects
// stage object
set stage = object() {
	set w = 470;
	set h = 720;

	set top = - h / 2;
	set left = - w / 2;
	set right = w / 2;
	set bottom = h / 2;

	set draw = function () {
		algo.goTo(0, 0);
		algo.rect(w, h);
	};

	set collide = function (ball) {
		if (ball.x + ball.hw >= right || ball.x - ball.hw <= left ) {
			ball.velX = -ball.velX;
		} elseif (ball.y - ball.hh <= top) {
			ball.velY = -ball.velY;
		} elseif (ball.y + ball.hh >= bottom) {
			// you loose
		}
	};

};

// brick object
set brick = object () {
	set stamp;
	set x;
	set y;
	set remain;
	set hw;
	set hh;

	// constructor
	set clone = function(x, y, stamp) {
		set o = new this;
		o.x = x;
		o.y = y;
		o.stamp = stamp;
		o.hw = stamp.width / 2;
		o.hh = stamp.height / 2;

		// eval brick live left
		if (stamp == brickRedST) {
			o.remain = 4;
		} elseif (stamp == brickMagentaST) {
			o.remain = 2;
		} else {
			o.remain = 1;
		}

		return o;
	};

	// methods
	set draw = function () {
		algo.goTo (x, y);
		stamp.draw();
	};

	set collide = function (ball) {
		if (ball.y + ball.hh >= y - hh && ball.y - ball.hh <= y + hh && ball.x + ball.hw > x - hw && ball.x - ball.hw < x + hw) {
			if (ball.y <= y - hh && ball.y >= y + hh) {
				ball.velX = -ball.velX;
			} else {
				ball.velY =- ball.velY;
			}
			remain --;
			if (remain == 2) {
				stamp = brickMagentaST;
			} elseif (remain == 1) {
				stamp = brickBlueST;
			} elseif (remain == 0) {
				return true;
			}
		}
		return false;
	};

};

set ball = object() {
	set x;
	set y;
	set stamp = ballST;
	set velX = 10;
	set velY = -10;
	set hw = stamp.width / 2;
	set hh = stamp.height / 2;

	// methods
	set draw = function () {
		algo.goTo(x, y);
		stamp.draw();
	};

	set anim = function () {
		x += velX;
		y += velY;

		// manage collisions
		stage.collide (ball);
		racket.collide (ball);

		if (y - hw <= collideThreshold) {
			bricks.each(function (item, i) {
				if (item.collide (ball)) {
					bricks.remove (i);
				}
			});
		}
	};
};

set racket = object() {
	set x;
	set y;
	set stamp = racketST;
	set hw = stamp.width / 2;
	set hh = stamp.height / 2;

	// methods
	set draw = function () {
		algo.goTo(x, y);
		stamp.draw();
	};

	set collide = function (ball) {
		if (ball.y + ball.hh >= y - hh && ball.y - ball.hh <= y + hh && ball.x + ball.hw > x - hw && ball.x - ball.hw < x + hw) {
			ball.velY =- ball.velY;
			ball.velX = (ball.x - x) / 5;
		}
	};
};

// sprites level
set brickStamps = array {
	 {brickBlueST, brickBlueST, brickBlueST, brickBlueST, brickBlueST},
	 {brickBlueST, brickMagentaST, brickBlueST, brickMagentaST, brickBlueST},
	 {brickMagentaST, brickRedST, brickMagentaST, brickRedST, brickMagentaST},
	 {brickBlueST, brickMagentaST, brickRedST, brickMagentaST, brickBlueST},
	 {brickBlueST, brickBlueST, brickMagentaST, brickBlueST, brickBlueST},
	 {brickBlueST, brickBlueST, brickBlueST, brickBlueST, brickBlueST}
};

// bricks array
set bricks = array {};
set collideThreshold = stage.top;

// ---- functions
set drawLevel = function () {
	stage.draw();

	bricks.each(function (item) {
		item.draw();
	});

	ball.draw();
	racket.draw();
};

set run = function () {
	algo.autoClear();
	ball.anim();
	drawLevel();
};

// create bricks array from brickStamps
set initBricks = function () {
	brickStamps.each(function (rowItem, itemY) {

		// calculate center on x
		set ix = (rowItem.length() - 1) * (rowItem[0].width + 7) / 2;
		// set y initial position
		set iy = -300;

		rowItem.each (function (item, itemX) {
			// calculate positions
			set x = itemX * (item.width + 7) - ix;
			set y = itemY * (item.height + 7) + iy;
			bricks.add(brick.clone (x, y, item));
			collideThreshold = math.max (collideThreshold, y + item.height / 2);
		});
	});
}

set init = function () {
	// init
	ui.showAlgo();
	algo.hide();
	algo.clear();

	// init ball
	ball.x = 0;
	ball.y = stage.bottom - 100;

	// init racket
	racket.x = 0;
	racket.y = stage.bottom - 70;

	// draw bricks
	initBricks();
};

// main program
init ();
util.pulse (run, 30);

set racketClearance = stage.right - racket.hw;

algo.onMove (function (x, y) {
	if (x < -racketClearance) {
		racket.x = -racketClearance;
	} else if (x > racketClearance) {
		racket.x = racketClearance;
	} else {
		racket.x = x;
	}
});
