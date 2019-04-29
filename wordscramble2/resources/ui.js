var	G_LOGGER,
	G_PTR_FRAME,
	G_CONNECTED,
	G_GAMESPACES,
	G_CNT_MAX_GAMESPACES,
	G_CNT_MAX_USER_PER_GAMESPACE,
	G_ACT_ROOM_ID,
	G_GAMESPACE_JOIN_STATUS     //0 :: not joined, 1 :: join request sent waiting for reply, 2: joined
	;

function init(){
	setConnected(false);//@websocket.js
	G_LOGGER = new Logger('wsLog',3);
	G_CONNECTED = false;
	G_GAMESPACES = new Array();//list of {id:number,title:string,players:number} tuplets
	G_CNT_MAX_GAMESPACES = 3;
	G_CNT_MAX_USER_PER_GAMESPACE = 5;
	G_ACT_ROOM_ID = null;
	G_GAMESPACE_JOIN_STATUS = 0;
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

/**
	Redraw GameSpaces
*/
function refreshRooms(){
	if( G_CONNECTED && G_GAMESPACE_JOIN_STATUS==2 ){
		document.getElementById("divGameSpaces").innerHTML = "<h3>Enjoy the game!<h3>";
		return;
	}
	var html = "<form action='game.html' target='frmMain' method='get' id='frmGSEnum'>";
	if( G_GAMESPACES != undefined && G_GAMESPACES.length > 0 ){
		for(const {id,title,players} of G_GAMESPACES){
			html += ('<input id="btnJoinRoom'+id
			        +'" type="submit" onclick="joinRoom('+id+');" value="Join to room no. '+id
					+' with keywords('+title+'), players: '+players+'" form="frmGSEnum" '
					+(G_CONNECTED && G_GAMESPACE_JOIN_STATUS==0 ? '' : 'disabled')+'/>')
					;
			html += '<br/>';
		}
	}//endif
	if(G_GAMESPACES.length < G_CNT_MAX_GAMESPACES){
		//add 5 input fields + [New room] button for a potential new space
		html += '<p>Create a new room by providing keywords for the theme!</p>'
		for(var i=0; i<5; i++){
			html += ('<label for="iKW'+i+'" form="frmGSEnum">Keyword:</label><input type="text" name="iKW'+i+'" id="iKW'+i+'" pattern="[A-Za-z]+" form="frmGSEnum"'
			        +(G_CONNECTED && G_GAMESPACE_JOIN_STATUS==0 ? '' : 'disabled')
			        +'/><br/>')
					;
		}
		html += ('<input id="btnNewRoom" type="submit" onclick="createRoom();" value="New room" form="frmGSEnum"'
		        +(G_CONNECTED && G_GAMESPACE_JOIN_STATUS==0 ? '' : 'disabled')
				+'/>')
				;
	}//endif
	html += '</form>';
	document.getElementById("divGameSpaces").innerHTML = html;
}

function joinRoom(inRoomID){
	G_ACT_ROOM_ID = inRoomID;
	//dispatch to websocket
	sendJoinRoom(inRoomID);
	G_GAMESPACE_JOIN_STATUS = 1;
	G_ACT_ROOM_ID = inRoomID;
}

function leaveRoom(){
	G_GAMESPACE_JOIN_STATUS = 0;
	G_ACT_ROOM_ID = null;
	unloadGameFromFrame();
	sendLeaveRoom();
}

function createRoom(){
	//send room params to Server via CREATE_SPACE message provided there is at least one non-empty keyword
	var keyWords = new Array();
	var elements = document.getElementById("frmGSEnum").elements;
	for (var i = 0, element; element = elements[i++];) {
		if (element.type === "text" && element.value != ""){
			keyWords.push(element.value);
		}
	}//next element
	if(keyWords.length > 0){
		//send room params via websocket
		sendCreateRoomParams(keyWords);
		G_GAMESPACE_JOIN_STATUS = 1;
	}
}

function afterFrameLoaded(){
	  G_PTR_FRAME = document.getElementById("frmMain").contentWindow;
	  G_PTR_FRAME.setTitle(getRoomTitle());
	  G_PTR_FRAME.selfConnected(G_SESSION_ID, G_USER_NAME);
}

function loadGameToFrame(){
	var tmpframe = document.getElementById("frmMain");
	tmpframe.addEventListener("load", afterFrameLoaded);
	tmpframe.src = "game.html";	
}

function unloadGameFromFrame(){
	G_PTR_FRAME = null;
	var tmpframe = document.getElementById("frmMain");
	tmpframe.removeEventListener("load", afterFrameLoaded);
	tmpframe.src = "chooseRoom.html";
}

function getRoomTitle(){
	if(G_ACT_ROOM_ID==null || G_GAMESPACE_JOIN_STATUS != 2) return null;
	for(const {id,title,players} of G_GAMESPACES){
		if(id==G_ACT_ROOM_ID){
			return "Room no. " + G_ACT_ROOM_ID + " with keywords {" + title + "}";
		}
	}//next space
	return null;
}
