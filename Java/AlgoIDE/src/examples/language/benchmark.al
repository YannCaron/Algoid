/*
AL Benchmarks CyaNn 14-04-2013
IMPORTANT : Disable the 'debug' and 'step by step' execution mode to have real performances.
*/

set j = 0;
set benchmark = function (name, func) {

	set start = al.clock();
	
	// execute benchmark
	func ();
	
	set time = ((al.clock() - start) * 1000).toDecimal(); // in millisecond
	util.log ("AL " .. name .. " running time : " .. time .. " ms");
	

};

ui.clearLog();
ui.showLog();


benchmark ("For loop", function () {
	for (set i = 0; i < 100000; i++) {
		j += 1;
	}
});

benchmark ("Nested loops", function () {
	for (set i = 0; i < 50000; i++) {
		for (set k = 0; k < 2; k++) {
			j += 1;
		}
	}
});

benchmark ("Loop", function () {
	loop (100000) {
		j += 1;
	}
});

benchmark ("Functional loop", function () {
	(100000).loopFor (function (i) {
		j += 1;
	});
});

benchmark ("If", function () {
	for (set i = 0; i < 100000; i++) {
		if (i % 2 == 0) {
			j += 1;
		} else {
			j -= 1;
		}
	}
});

benchmark ("While", function () {
	j = 0;
	while (j < 100000) {
		j += 1;
	}
});

benchmark ("Until", function () {
	j = 0;
	do {
		j += 1;
	} until (j > 100000)
});

benchmark ("Call function", function () {
	set add = function (a, b) {
		return a + b;
	};
	for (set i = 0; i < 100000; i++) {
		j += add (1, 2);
	}	
});

benchmark ("Call method", function () {
	set o = object () {
		set add = function (a, b) {
			return a + b;
		};
	};
	for (set i = 0; i < 100000; i++) {
		j += o.add (1, 2);
	}	
});

benchmark ("Magic method", function () {
	set s = "Hi, I am Algoid !";
	for (set i = 0; i < 100000; i++) {
		j += s.length();
	}	
});

benchmark ("Array access", function () {
	set a = array {1, 2, 3, 4};
	for (set i = 0; i < 100000; i++) {
		j += a[1];
	}	
});

benchmark ("Associative array access", function () {
	set a = array {"a" : 0, "b" : 1, "c" : 2, "d" : 3};
	for (set i = 0; i < 100000; i++) {
		j += a["c"];
	}	
});

benchmark ("Eval", function () {
	set i = 0;
	j = 0;
	while (i < 1000) {
		j++;
		util.eval("i = " .. j);
	}
});
