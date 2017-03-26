/*
Functional programming sample code.
Written by Yann Caron at 19-02-2013
*/

set a = array {
	function (size) {algo.disc(size);},
	function (size) {algo.platter(size, size * 2);},
	function (size) {algo.circle(size);},
	function (size) {algo.oval(size, size * 2);},
	function (size) {algo.rect(size, size);},
	function (size) {algo.rect(size, size * 2);},
	function (size) {algo.plane(size, size);},
	function (size) {algo.plane(size, size * 2);},
};

set randomFunction = function () {
	set r = math.random(a.length(a));
	return a[r];
};

ui.showAlgo();

algo.onClick (function (x, y) {
	algo.goTo(x, y);
	algo.setColor (math.random(16));
	set size = math.random(50);
	randomFunction()(size);
});
