/*
Hello you sample code.
Written by Yann Caron at 14-05-2012
*/

set firstName = text.inputText ("What's your first name ?");
set lastName = text.inputText ("What's your last name ?");
set str = "hello " .. firstName .. " " .. lastName .. " !";
text.output (str);

set drawText = function (x, y) {
	algo.setColor (math.random(16));
	algo.disc (10);
	algo.lineTo (x, y);
	algo.text (str);
};

drawText (0, 0);

algo.onClick(drawText);