/*
Aspect programming sample code.
Written by Yann Caron at 19-02-2013
*/

set superO = object () {
	set superMeth = function (s) {
		util.log ("Execute SUPER method with param : " .. s);
	};
};

set o = object (superO) {
	set meth = function (s) {
		util.log ("Execute method with param : " .. s);
	};
};

set logger = function (s, decored) {
	util.log ("Before decored execution");
	decored(s);
	util.log ("After decored execution");
};

ui.clearLog();
ui.showLog();

set myO = new o;

myO.setAttribute("meth", myO.meth.decorate(logger));
myO.meth("Hi I am algoid !");

util.log ("--------");

myO.superMeth = myO.superMeth.decorate(logger);
myO.superMeth("Hi I am algoid !");
