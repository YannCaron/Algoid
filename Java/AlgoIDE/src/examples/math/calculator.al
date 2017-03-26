/*
Calculator sample code.
Written by Yann Caron at 14-05-2012
*/

ui.showAlgo();

set key = object () {
	set radius = 60;
	set x = 0;
	set y = 0;
	set value = "0";

	set init = function (x, y, value) {
		x = x;
		y = y;
		value = value;
	};

	set draw = function () {
		algo.goTo(x, y);
		algo.text(value);
		algo.circle(radius);
	};

	set isOn = function (tx, ty) {
		set diag = math.diagonal(tx - x, ty - y);
		return (diag < radius);
	};

	set action = function (tx, ty, f) {
		if (isOn (tx, ty)) {
			f();
			display.draw();
			algo.goTo(x, y);
		}
	};

};

set display = object () {

	set init = false;
	set value = "0";

	set append = function (v) {
		if (value == 0) {
			value = v;
		} else {
			value ..= v;
		}
	};

	set draw = function() {
		algo.goTo (0, -150);

		algo.removeLast();
		algo.text(value);
	};

	set initDraw = function() {
		algo.goTo (0, -150);
		algo.rect (460, 60);
		algo.text(value);
	};


};

set keys;
set x = 0; set y = 0;

set keyTexts = array {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/", "="};

/* initialize keys */
for (set i = 0; i < keyTexts.length(); i++) {
	set k = new key;
	keys[i] = k;
	k.init((x * 100) - 200 , (y * 100) - 50, keyTexts[i]);
	k.draw();

	x++;
	if (x > 4) {
		x = 0;
		y++;
	}
}

display.initDraw();

/* event */
set testOn = function (x, y) {
	for (set i = 0; i < keys.length(); i++) {
		set k = keys[i];

		if (i < keys.length() - 1) {
			k.action(x, y, function () {
				display.append(k.value);
			});
		} else {
			/* special key = */
			k.action(x, y, function () {
				set current = display.value;
				display.value = 0;

				set code = "set result = " .. current .. ";";
				util.eval(code);

				if (current != result) {
					display.value = result;
				}
			});
		}
	}

};

algo.onClick(testOn);