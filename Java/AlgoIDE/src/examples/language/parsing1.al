/*
CSV parsing example.
Demonstrate the power of functional programming combined to cascade
Written by Yann Caron at 19-02-2013
*/

set csv;
csv   = "data 1.1; data 1.2; data 1.3; data 1.4\n";
csv ..= "data 2.1; data 2.2; data 2.3; data 2.4\n";
csv ..= "data 3.1; data 3.2; data 3.3; data 3.4\n";
csv ..= "data 4.1; data 4.2; data 4.3; data 4.4\n";

set values = csv.split ("\n").each (function (item) {
	return item.split (";").each(function (item) {
		return item.trim();
	});
});

ui.clearLog();
ui.showLog();

values.eachItem(function (item) {
	util.log ("parsed data : [" .. item .. "]");
});
