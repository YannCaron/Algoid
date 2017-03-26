/*
Filter-Map-Reduce sample code.
Written by Yann Caron at 26-04-2013
*/

set dog = object {
	set name; set genre; set age;
};

set dogs = array {
	dog.clone("effy", 0, 5),
	dog.clone("wolf", 0, 7),
	dog.clone("lili", 1, 7),
	dog.clone("poupette", 1, 10),
	dog.clone("rouquette", 1, 11),
	dog.clone("rouky", 0, 8),
	dog.clone("athos", 0, 3)
};

set average = function (gender) {
	set ages;
	dogs.filter (function (item) {
		return item.genre == gender;
	}).each(function (item) {
		ages.add(item.age);
	});
	set total = ages.join (al.combine.sum);
	return total / ages.length();
}

ui.showLog();

util.log ("The average of male dog ages is " .. average(0));
util.log ("The average of female dog ages is " .. average(1));
