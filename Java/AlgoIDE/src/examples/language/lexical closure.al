/*
Lexical closure example.
Demonstrate the power of functional programming combined with MOP.

AL starting assumption
- data, functions, objects and arrays are expressions
- expressions are objects
- so functions and objects are respectively meta-functions and meta-objects

Written by Yann Caron at 19-02-2013
*/

set add = function (a, b) {
	return a + b;
};

set pow2 = function (a) {
	return math.dbl(a);
};

set bound = function (min, max, f) {
	set n = f();
	if (n < min) {
		n = min;
	}
	if (n > max) {
		n = max;
	}
	return n;
};

ui.clearLog();
ui.showLog();

// prepare parameters without calling function
add.setParameter("a", 7).setParameter("b", 8);
util.log ("0 < 7 + 8 < 10 = " .. bound (0, 10, add));
util.log ("0 < 7 + 8 < 100 = " .. bound (0, 100, add));

pow2.setParameter("a", 7);
util.log ("0 < 7 + 8 < 10 = " .. bound (0, 10, pow2));
util.log ("0 < 7 + 8 < 100 = " .. bound (0, 100, pow2));
