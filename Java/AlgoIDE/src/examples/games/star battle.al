/*
Stat battle, shoot em up.
Created by Yann Caron at 22-05-13 17:54
*/

// init algo
//ui.fullScreen ();
algo.hide ();

// variables
set mw = algo.getWidth () / 2;
set mh = algo.getHeight () / 2;
set starNb = 10;
set velX = 0;

// objects
// score
set score = object () {
	set value = 0;
	set draw = function () {
		algo.goTo (mw - 125, -mh + 25);
		algo.setTextSize (20);
		algo.text ("score " .. value);
	}
}

// star
set star = object () {
	// stamp
	set starSTs = array {
		algo.stamp.clone (array {{7}}, 5),
		algo.stamp.clone (array {{16, 7, 16}, {7, 7, 7}, {16, 7, 16} }, 5),
		algo.stamp.clone (array {{7, 16, 7}, {16, 7, 16}, {7, 16, 7} }, 5),
		algo.stamp.clone (array {{16, 16, 7, 16, 16}, {16, 7, 7, 7, 16}, {7, 7, 7, 7, 7}, {16, 7, 7, 7, 16}, {16, 16, 7, 16, 16}}, 5)
	};
	set x = 0;
	set y = 0;
	set speedY = 0;
	set id = 0;

	// executed each frame
	set run = function () {
		x += velX;
		y += speedY;
		algo.goTo (x, y);
		starSTs [id].draw ();
	};

	set init = function (y) {
		x = math.random (algo.getWidth ()) - mw;
		y = y;
	}

	// constructor
	set clone = function (y) {
		set o = new this;
		o.id = math.random (starSTs.length ());
		o.init (y);
		o.speedY = math.random (10) + 10;
		return o;
	}
};

// ship
set ship = object () {

	set x = 0;
	set y = mh - 100;
	set stamp = algo.stamp.clone (
		array {
			{-1, -1, -1, 10, -1, -1, -1},
			{-1, -1, -1, 10, -1, -1, -1},
			{-1, -1, 02, 10, 02, -1, -1},
			{10, -1, 02, 10, 02, -1, 10},
			{10, 02, 02, 10, 02, 02, 10},
			{10, 02, 10, -1, 10, 02, 10},
			{10, 02, 02, -1, 02, 02, 10},
			{10, -1, -1, -1, -1, -1, 10},
		}, 7
	);

	// executed each frame
	set run = function () {
		algo.goTo (x, y);
		stamp.draw ();
	}
};

// alien
set alien = object () {

	set x = 0;
	set y = -mh + 50;
	set velX = 0;
	set velY = 0;

	set stamp = algo.stamp.clone (
		array {
			{13, -1, -1, -1, -1, -1, 13},
			{13, 05, 05, -1, 05, 05, 13},
			{-1, -1, 05, 13, 05, -1, -1},
			{13, 05, 13, -1, 13, 05, 13},
			{13, 05, 05, 13, 05, 05, 13},
			{13, -1, 05, 13, 05, -1, 13},
			{-1, -1, -1, 13, -1, -1, -1},
			{-1, -1, -1, 13, -1, -1, -1}
		}, 7
	);

	// constructor
	set clone = function () {
		set o = new this;
		o.x = math.random (algo.getWidth ()) - mw;
		return o;
	};

	// executed each frame
	set run = function () {
		x += velX;
		y += velY;

		if (!x.between (-mw, mw)) {
			velX = -velX;
			x+= velX;
		}
		if (!y.between (-mh, 0)) {
			velY = -velY;
			y += velY;
		}

		algo.goTo (x, y);
		stamp.draw ();

		if (math.random (10) == 0) {
			velX = math.random (30)-15;
			velY = math.random (30)-15;

			// shoot
			bullets.add (bulletDown.clone (x, y));
		}
	}
};

// bullets up
set bulletUp = object () {
	set stamp = algo.stamp.clone (
		array {
			{-1, 03, -1},
			{03, 11, 03},
			{03, 11, 03},
			{03, 11, 03},
			{-1, 03, -1}
		}, 5); // bullet up

	set x;
	set y = mh -75;
	set velY = -50;

	// constructor
	set clone = function (x) {
		set o = new this;
		o.x = x;
		return o;
	}

	// executed each frame
	set run = function () {
		y += velY;
		algo.goTo (x, y);
		stamp.draw ();
	}

	// test collision
	set isCollide = function () {
		aliens.each (function (item, i) {
			// calculate
			if (math.diagonal (x - item.x, y - item.y) < 30) {
				aliens.remove (i);
				score.value += 25;

				// new aliens
				if (aliens.length () == 0) {
					newAliens();
				}
				
				return true;
			}
		});
		
		return false;
	}
}

// bullets down
set bulletDown = object () {
	set stamp = algo.stamp.clone (
		array {
			{-1, -1, 04, -1, -1},
			{-1, 04, 12, 04, -1},
			{04, 12, -1, 12, 04},
			{-1, 04, 12, 04, -1},
			{-1, -1, 04, -1, -1},
		}, 5);

	set x;
	set y;
	set velY = 25;

	// constructor
	set clone = function (x, y) {
		set o = new this;
		o.x = x;
		o.y = y;
		return o;
	}

	// executed each frame
	set run = function () {
		y += velY;
		algo.goTo (x, y);
		stamp.draw ();
	}

	// test collision
	set isCollide = function () {
		if (math.diagonal (x - ship.x, y - ship.y) < 15) {
			gameOver ();
			return true;
		}
		return false;
	}

};

// main
set nbAlien = 1;
set stars = array {};
set bullets = array {};
set aliens = array {};

// init level
set init = function () {
	loop (starNb) {
		set y = math.random (algo.getHeight ()) - mh;
		stars.add (star.clone (y));
	}
};

// executed each frame
set run = function () {
	algo.autoClear();

	// anim stars
	stars.each (function (item) {
	item.run ();
	if (item.y >= mh) {
		item.init (-mh);
	}
	});

	// anim aliens
	aliens.each (function (item, i) {
		item.run ();
	});

	// anim bullets
	bullets.each (function (item, i) {
		item.run ();
		if (item.isCollide ()) {
			bullets.remove (i);
		}

		if (!item.y.between (-mh, mh)) {
			bullets.remove (i);
		}

	});

	ship.run ();
	score.draw ();

};

set initAliens = function () {
	nbAlien.loopFor (function () {
		aliens.add (alien.clone ());
	});
}

set newAliens = function () {
	nbAlien++;
	initAliens ();
	score.value += 100;
};

set gameOver = function () {
	util.clearTasks();
	algo.setColor (12);
	algo.setBgColor (4);
	score.draw();
	algo.goTo (0, 0);
	algo.setTextSize (30);
	algo.text ("Game Over");
};

// main programm
init ();
initAliens ();

// events
algo.onClick (function (x, y) {
	set o = bulletUp.clone (ship.x);
	bullets.add (o);
});

util.pulse (run, 40);

algo.onMove (function (x, y) {
	velX = x / 50;
	ship.x = x;
});
