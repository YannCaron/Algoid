set  messageHeader = object(){
	set MsgId;
	set MsgTo;
	set MsgFrom;
	set MsgData;
}

set messageData = object(){
	set MsgType;
	set MsgParam;
	set MsgValue;
}

set wheel = object() {
	set wheel;
	set velocity;
	set cm;
}

set servo = object() {
	set servo;
	set angle;
}

set distance = object() {
	set sonar;
	set angle;
	set event;
	set event_lower;
	set event_higher;
}

text.clear();

//----------------------------------------------
// Function moveBuggy
//----------------------------------------------
set moveBuggy = function(leftWheelSpeed, rightWheelSpeed, leftRunTime, rightRunTime){
	set command = messageHeader.clone(math.random (10000), "buggy", "Algoid");
	set msgData = messageData.clone("command", "2wd");
								msgData.MsgValue = array () {
									wheel.clone("right", rightWheelSpeed, rightRunTime),
									wheel.clone("left", leftWheelSpeed, leftRunTime)
	};	
	command.MsgData = msgData;
//	util.log (command.toJSon())
	
	mqtt.publish("Command",  command.toJSon				());
	mqtt.expect("Event", (topic,  messageReceived) {
	util.warn("is message expected ? " .. messageReceived .. " " .. ("algoid" == "algoid"));
//	return (msg=="go");
})
	}
// End moveBuggy-------------------------------


//----------------------------------------------
// Function getDistance
//----------------------------------------------
set getDistance = function(){
	set request = messageHeader.clone(math.random (10000), "buggy", "Algoid");
	set msgData = messageData.clone("request", "distance");
								msgData.MsgValue = array () {
									distance.clone(0,0,"off",20, 180)
	};	
	request.MsgData = msgData;
//	util.log (command.toJSon())
	
	mqtt.publish("Command",  request.toJSon());

/*
	mqtt.expect("Response", (topic,  messageReceived) {
		util.log (messageReceived)
		*	set myMessage = util.fromJson(messageReceived);
		set myAckResult = myMessage.MsgData.MsgType
		util.warn("Ack result: " .. myAckResult .. " is valid ? " .. (myAckResult=="ack"));
		
//	return (1);
})
*/
	}
// End getGistance-------------------------------


//----------------------------------------------
// Function SetServo
//----------------------------------------------
set setServo = function(pos){
	set request = messageHeader.clone(math.random (10000), "buggy", "Algoid");
	set msgData = messageData.clone("command", "servo");
								msgData.MsgValue = array () {
								servo.clone(1,pos)
	};	
	request.MsgData = msgData;
//	util.log (command.toJSon())
	
	mqtt.publish("Command",  request.toJSon());

	return(1);
	}
// End SetServo-------------------------------

//----------------------------------------------
// MAIN
//----------------------------------------------
set mqtt = network.mqtt.clone("tcp://192.168.3.1:1883", "algoid");

set DIST;

mqtt.subscribe("Event", (topic,  messageReceived) {
		set myMessage = util.fromJson(messageReceived);
		set myDistance = myMessage.MsgData.MsgValue[0].cm
//		util.warn("My distance " .. myDistance .. " " .. ("algoid" == "algoid"));
//				text.output(myDistance .. "cm");
				DIST = myDistance;
})



// Avance 3.6m
moveBuggy (-15,-15, 60, 60);
util.wait(500);
/*
//Rotation droite 90degré
moveBuggy (50,-50, 27, 27);
util.wait(500);
*/
/*
setServo(10);
util.wait(1000);
setServo(100);
*/

//set ret = text.inputNumber ("ajskad");
//text.output("monchoix: " .. ret);

/*
while(1){
	getDistance();
	text.output(DIST .. "cm");
	//algo.go(10);
	util.wait(250);
}
*/
//mqtt.publish("MONTEST",  messageHeader.toJSon());
