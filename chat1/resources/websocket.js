var ws;
var userName;

function init(){
	setConnected(false);
}

function connect() {
    var host = document.location.host;
    var pathname = document.location.pathname;
	var relativePath = "srv";
	
	//log.innerHTML += "host=" + host + ", pathname=" + pathname + "<br/>";
	
    ws = new WebSocket(((window.location.protocol === "https:") ? "wss://" : "ws://") + host  + pathname + relativePath);

    ws.onmessage = function(event) {
		var log = document.getElementById("log");
					   
		var message = JSON.parse(event.data);
		switch(message.type){
			case "msgSelfName":
				setSelfName(message.content);
				setConnected(true);
				break;
			case "msgConnectedUsers":
				setConnectedUsers(message.stringArray);
				break;
			case "msgSimple":
				var msgFrom = message.from;
				var msgContent = message.content;
				if( msgContent.substr(1,8)=='{"type":' ){
					//seems like a bug somewhere
					msgContent = JSON.parse(message.content).content;//who knows why is that needed again...?
				}
				try{
					//seems like a bug somewhere
					msgContent = JSON.parse(message.content).content;//who knows why is that needed again...?					
				} catch(err) {
					//do nothing
				}
				log.innerHTML += getMessageHtml(msgFrom==userName,msgFrom,msgContent);
				break;
			default:
				/* msgSimple, msgRebuke all fall in this category as there is nothing else to do than simply show the message */
				log.innerHTML += getMessageHtml(false,message.from,message.content);
		}
    };
	
	ws.onclose = function(event){ 
		var log = document.getElementById("log");
		log.innerHTML += getMessageHtml(false,"Server","Disconnected");
	};
	
}

document.addEventListener('keydown', function(event) {
	if (event.target.id == 'messageToSend' && event.code == 'Enter' && !(event.ctrlKey || event.metaKey)) {
		event.preventDefault();
		sendMessage();
	}
});

function disconnect(){
	var message = {
		type: "msgClose",
		from: userName,
		to: "Server",
		content: 'userName'
	};
	ws.send(JSON.stringify(message));
	ws.close();
	setConnected(false);
	setSelfName("");
	document.getElementById("divConnectedUsers").innerHTML = "";
}

function setSelfName(inName){
	userName = inName;
	document.getElementById("divConnectedAs").innerHTML = "<p>"+userName+"</p>";
}

function setConnectedUsers(inArr){
	var str = "<ul>";
	for(x in inArr){
		str += ("<li>"+inArr[x]+"</li>");
	}
	str += "</ul>";
	document.getElementById("divConnectedUsers").innerHTML = str;
}
 
function setConnected(inConn){
	//TRUE:  disable [Connect] and enable [Disconnect]
	//FALSE: opposite
	document.getElementById("btnConnect").disabled = inConn;
	document.getElementById("btnDisconnect").disabled = (!inConn);
	document.getElementById("btnSend").disabled = (!inConn);
}

function getMessageHtml(inIsOwnText,inMsgSender,inMsgText){
	var msgclass='';
	if(inIsOwnText){
		msgclass='spUserSelf';
	} else if(inMsgSender=='Server') {
		msgclass='spUserServer';
	} else {
		msgclass='spUserPartner';		
	}
	var d = new Date();
	var t = d.getHours()+":"+d.getMinutes();
	var ret  = '<div class="'+msgclass+'">';
		ret +=   '<p><span class="spSender">'+inMsgSender+':</span>&nbsp;';
		ret +=   '<span class="spMsgText">'+inMsgText+'</span><br/>';
		ret +=   '<span class="'+(inIsOwnText ? 'time-right' : 'time-left')+'">'+t+'</span></p>';
	    ret += '</div>';
	return ret;
}

function sendMessage(){
	var txt = document.getElementById("messageToSend").value;
	//alert("txt="+txt);
	var message = {
		type: "msgSimple",
		from: userName,
		to: "Server",
		content: txt
	};
	ws.send(JSON.stringify(message));
	document.getElementById("messageToSend").value = "";
}
