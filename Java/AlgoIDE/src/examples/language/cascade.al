/*
Cascade example.
Demonstrate the different ways to write program in AL :
expression and functional one.
Written by Yann Caron at 24-02-2013
*/

ui.clearLog();
ui.showLog();

// the following expression
set exp = 1 + 2 - 4 / 2;
util.log ("exp = " .. exp);

// can be written
set cas = (1).addition(2).substract((4).divide(2));
util.log ("cas = " .. cas);

util.log ("--------");

// the following expression
if (exp == cas) {
	util.log ("It's working");
}

// can be written
exp.equals(cas).ifTrue(function () {
	util.log ("It's working too");
});

util.log ("--------");

// the following expression
for (set i = 0; i<5; i++) {
	util.log ("For loop iteration " .. i);
}

// can be written
(5).loopFor (function (i) {
	util.log ("loopFor iteration " .. i);
});
