/*
Column size delimited parsing example.
Demonstrate the power of functional programming combined to cascade
Written by Yann Caron at 19-02-2013
*/

set csv;
csv   = "data 1.1data 1.2data 1.3data 1.4\n";
csv ..= "data 2.1data 2.2data 2.3data 2.4\n";
csv ..= "data 3.1data 3.2data 3.3data 3.4\n";
csv ..= "data 4.1data 4.2data 4.3data 4.4\n";

set columns = array {8, 16, 24, 32};

set values = csv.split ("\n").each (function (item) {
	return item.splitAt (columns);
});

ui.clearLog();
ui.showLog();

values.eachItem(function (item) {
	util.log ("parsed data : [" .. item .. "]");
});