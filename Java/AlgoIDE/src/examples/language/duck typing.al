/*
Duck typing example.
Demonstrate the power (and risks likewise) of dynamic typed languages.
Written by Yann Caron at 19-02-2013
*/

set duck = object () {
	set quack = function () {
		util.log ("Quaaaaaack! Quaaaaaack!");
	};
};

set cow = object () {
	set quack = function () {
		util.log ("Hey! I am not a duck, I am a cow, so I say moooooo!");
	};
};

ui.clearLog();
ui.showLog();

set animals = array {new duck, new cow};

animals.each (function (item) {
	item.quack();
});