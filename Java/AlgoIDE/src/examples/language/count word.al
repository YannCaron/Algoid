/*
Count word parsing example.
Demonstrate the power of functional programming combined to cascade
Written by Yann Caron at 19-02-2013
*/
ui.clearLog();
ui.showLog();
 
set text;
text   = "data 1.1; data 1.2; data 1.3\n";
text ..= "data 2.1; data 2.2; data 2.3; data 2.4\n";
text ..= "data 3.1; data 3.2;\n";
text ..= "data 4.1;\n";

set count = text.split ("\n").each (function (item) {
	return item.split (" ").each(function (item) {
		if (item == "data") {
			return 1;
		}
		return 0;
	}).join (al.combine.sum);
}).join (al.combine.sum);

util.log ("data word counted : [" .. count .. "]");
