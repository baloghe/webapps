var	G_LOGGER,
	G_PTR_FRAME,
	G_CONNECTED
	;

function init(){
	setConnected(false);//@websocket.js
	G_LOGGER = new Logger('wsLog',3);
	G_CONNECTED = false;
}

/**
	Key event listener for ENTER key
*/
document.addEventListener('keydown', function(event) {
	if (event.target.id == 'answerToSend' && event.code == 'Enter' && !(event.ctrlKey || event.metaKey)) {
		event.preventDefault();
		sendAnswer();
	}
});

/**
	Connection-dependent UI settings
*/
function setConnected(inConn){
	//TRUE:  disable [Connect] and enable [Disconnect]
	//FALSE: opposite
	G_CONNECTED = inConn;
	document.getElementById("btnConnect").value = (inConn ? "Disconnect" : "Start game");
	document.getElementById("iName").disabled = (inConn);
}


/**
	Listen to iFrame answer sent in
*/
function evaluateAnswer(inAnswer){
	//use a function defined in websocket.js
	sendAnswer(inAnswer);
}
