
var	G_WEBSOCKET;
var	G_USER_NAME;
var G_SESSION_ID;


function connect() {
    var host = document.location.host;
    var pathname = document.location.pathname;
	var relativePath = "srv";
	
	G_USER_NAME = document.getElementById("iName").value;
	G_PTR_FRAME = null; document.getElementById("frmMain").contentWindow;
	
	console.log("connect() called");
	
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
		case "JOIN_SPACE":
			//push the new User to the iFrame's Game object...
			//enqueing is needed as there is no guarantee that G_PTR_FRAME already exists when the first message arrives
			console.log('JOIN_SPACE processing started...');
			var check = function(){
				if(G_PTR_FRAME != null){
					// run when condition is met
					//pattern: {"player":{"name":"masik","sessionId":"3","score":0},"type":"JOIN_SPACE","sender":"SRV","sessionID":"3","content":"masik"}
					var playerName = inMsg.player.name;
					var playerSessionID = inMsg.player.sessionId;
					var playerScore = inMsg.player.score;
					G_PTR_FRAME.playerConnected(playerSessionID, playerName, playerScore);
					console.log('JOIN_SPACE processing ended...');
				}
				else {
					setTimeout(check, 3); // check again after a while
				}
			}
			check();
			break;
			
		case "CONN_CONFIRM":
			//save session ID
			G_SESSION_ID = inMsg.sessionID;
			//inform Server about our name			
			var message = {
				type: "JOIN_SERVER",
				sender: G_USER_NAME,
				sessionID: G_SESSION_ID,
				content: G_USER_NAME
			};
			G_WEBSOCKET.send(JSON.stringify(message));
			//modify UI
			setConnected(true);
			//redraw Rooms section
			refreshRooms();
			break;
			
		case "SCRAMBLE":
			//dispatch scramble to iFrame
			console.log("G_PTR_FRAME="+G_PTR_FRAME);
			//TAKEN FROM https://stackoverflow.com/questions/8896327/jquery-wait-delay-1-second-without-executing-code
			var check = function(){
				if(G_PTR_FRAME != null){
					// run when condition is met
					G_PTR_FRAME.showScramble(inMsg.content);
				}
				else {
					setTimeout(check, 3); // check again in a second
				}
			}
			check();
			break;
			
		case "ANS_CONFIRM":
			//send result of answer evaluation to iFrame
			G_PTR_FRAME.answerConfirmed(inMsg.sender, inMsg.content);
			break;
			
		case "SCORE":
			G_PTR_FRAME.setScore(inMsg.sessionID, inMsg.sender, inMsg.content);
			break;
			
		case "QUIT_SPACE":
			G_PTR_FRAME.userQuit(inMsg.sender);
			break;
			
		case "GAME_SPACE_ENUM":
			//pl. string={"count":1,"list":[{"id":1,"title":"fish, crisp","players":0}],"type":"GAME_SPACE_ENUM","sender":"2","sessionID":"2","content":""}
			var lst = inMsg.list;//list of {id:number,title:string,players:number} tuplets
			//console.log("GAME_SPACE_ENUM :: count=" + inMsg.count + ", list.len=" + lst.length);
			G_GAMESPACES = new Array();
			for(var i=0; i<lst.length; i++){
				G_GAMESPACES.push( {id: lst[i].id, title: lst[i].title, players: lst[i].players} );
				//console.log("GS ENUM id="+lst[i].id+", title="+lst[i].title+",players="+lst[i].players);
			}
			refreshRooms();
			break;
			
		case "GAME_SPACE_PLAYER_ENUM":
			//pl. {"count":1,"list":[{"name":"masik","sessionId":"1","score":0}],"type":"GAME_SPACE_PLAYER_ENUM","sender":"SRV","sessionID":"0","content":""}
			if( G_GAMESPACE_JOIN_STATUS==2 ){
					var check = function(){
					if(G_PTR_FRAME != null){
						var lst = inMsg.list;
						for(var i=0; i<lst.length; i++){
							var plName = lst[i].name;
							var plSession = lst[i].sessionId;
							G_PTR_FRAME.playerConnected(plSession, plName);
							console.log("GAME_SPACE_PLAYER_ENUM -> add sessionID="+plSession+", plName=" + plName);
						}//next i
					}
					else {
						setTimeout(check, 3); // check again in a second
					}
				}
				check();
			} else console.log("GAME_SPACE_PLAYER_ENUM :: G_GAMESPACE_JOIN_STATUS="+G_GAMESPACE_JOIN_STATUS+" --> nothing done");
			break;
			
		case "CREATE_SPACE_CONFIRM":
			G_ACT_ROOM_ID = inMsg.content;
			G_GAMESPACE_JOIN_STATUS = 2;
			refreshRooms();
			loadGameToFrame();
			break;
		
		case "CREATE_SPACE_REJECT":
			G_ACT_ROOM_ID = null;
			G_GAMESPACE_JOIN_STATUS = 0;
			refreshRooms();
			break;
			
		case "JOIN_SPACE_CONFIRM":
			G_ACT_ROOM_ID = inMsg.content;
			G_GAMESPACE_JOIN_STATUS = 2;
			refreshRooms();
			loadGameToFrame();
			break;
		
		case "JOIN_SPACE_REJECT":
			G_ACT_ROOM_ID = null;
			G_GAMESPACE_JOIN_STATUS = 0;
			refreshRooms();
			break;
		
		case "CLOCK":
			var check = function(){
				if(G_PTR_FRAME != null){
					// run when condition is met
					G_PTR_FRAME.setClock(inMsg.content);
				}
				else {
					setTimeout(check, 3); // check again in a second
				}
			}
			check();
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

function sendCreateRoomParams(inKeyWords){
	var txt = inKeyWords[0];
	for(var i=1; i<inKeyWords.length; i++){
		txt += (',' + inKeyWords[i]);
	}
	var message = {
		type: "CREATE_SPACE",
		sender: G_USER_NAME,
		sessionID: G_SESSION_ID,
		content: txt
	};
	G_WEBSOCKET.send(JSON.stringify(message));
	console.log("CREATE_SPACE message sent with txt=" + txt);
}

function sendJoinRoom(inGameSpaceID){
	var message = {
		type: "JOIN_SPACE",
		sender: G_USER_NAME,
		sessionID: G_SESSION_ID,
		content: inGameSpaceID
	};
	G_WEBSOCKET.send(JSON.stringify(message));
}

function sendLeaveRoom(){
	var message = {
		type: "QUIT_SPACE",
		sender: G_USER_NAME,
		sessionID: G_SESSION_ID,
		content: ""
	};
	G_WEBSOCKET.send(JSON.stringify(message));
}

function disconnect(){
	unloadGameFromFrame();
	G_WEBSOCKET.close();
	setConnected(false);
}