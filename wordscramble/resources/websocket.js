
var	G_WEBSOCKET;
var	G_USER_NAME;
var G_SESSION_ID;
	

function connect() {
    var host = document.location.host;
    var pathname = document.location.pathname;
	var relativePath = "srv";
	
	G_USER_NAME = document.getElementById("iName").value;
	G_PTR_FRAME = document.getElementById("frmMain").contentWindow;
	
	//log.innerHTML += "host=" + host + ", pathname=" + pathname + "<br/>";
	
	if(!G_CONNECTED){
		//create a new WS object
		G_WEBSOCKET = new WebSocket(((window.location.protocol === "https:") ? "wss://" : "ws://") + host  + pathname + relativePath);
			
		G_WEBSOCKET.onmessage = function(event) {
			console.log("onmessage called, string=" + event.data);
						   
			var message = JSON.parse(event.data);
			consumeMessage( message );
		};
		
		G_WEBSOCKET.onerror = function(event) {
			document.getElementById( 'wsLog' ).innerHtml="<p class='ERR'>Connection error.</p>";
			setConnected(false);
		};
		
		G_WEBSOCKET.onclose = function(event){
			setConnected(false);
		};
	} else {
		//disconnect
		disconnect();
	}
	
}

function consumeMessage(inMsg){
	//TBD, input: JSON-parsed message structure
	console.log("consumeMessage called -> type=" + inMsg.type);
	
	switch(inMsg.type){
		case "JOIN":
			//push the new User to the iFrame's Game object...
			var playerName = inMsg.content;
			var playerSessionID = inMsg.sessionID;
			G_PTR_FRAME.playerConnected(playerSessionID, playerName);
			break;
			
		case "CONN_CONFIRM":
			//push the new User to the iFrame's Game object...
			G_SESSION_ID = inMsg.sessionID;
			var playerSessionID = G_SESSION_ID;
			G_PTR_FRAME.selfConnected(G_SESSION_ID, G_USER_NAME);
			//G_LOGGER.log("Session ID=" + playerSessionID);
			var message = {
				type: "JOIN",
				sender: G_USER_NAME,
				sessionID: playerSessionID,
				content: G_USER_NAME
			};
			G_WEBSOCKET.send(JSON.stringify(message));
			console.log("JOIN message sent, G_USER_NAME=" + G_USER_NAME);
			//inform UI from successful connection
			setConnected(true);
			break;
			
		case "SCRAMBLE":
			//dispatch scramble to iFrame
			G_PTR_FRAME.showScramble(inMsg.content);
			break;
			
		case "ANS_CONFIRM":
			//send result of answer evaluation to iFrame
			G_PTR_FRAME.answerConfirmed(inMsg.sender, inMsg.content);
			break;
			
		case "SCORE":
			G_PTR_FRAME.setScore(inMsg.sessionID, inMsg.sender, inMsg.content);
			break;
			
		case "QUIT":
			G_PTR_FRAME.userQuit(inMsg.sender);
			break;
			
		default: console.log("undefined message");
	}
}

function sendAnswer(inTxt){
	var message = {
		type: "ANSWER",
		sender: G_USER_NAME,
		sessionID: G_SESSION_ID,
		content: inTxt
	};
	G_WEBSOCKET.send(JSON.stringify(message));
	G_PTR_FRAME.showParentLog('answer sent');
}

function disconnect(){
	G_WEBSOCKET.close();
	setConnected(false);
}