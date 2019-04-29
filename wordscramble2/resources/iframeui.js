var	G_LOGGER;
var	G_GAME;
var G_PLAYER;

function init(){
	G_LOGGER = new Logger('divReflex', 3);
	G_GAME = new Game();
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
	Listens to [Send] button and redirects the content of the answer
	1) to the Game object in order to have an immediate judgement
	2) to the Webserver
*/
function sendAnswer(){
	var ans=document.getElementById("answerToSend").value;
	//validate answer against scramble, i.e. whether it is a reshuffled version of the scramble
	if( G_GAME.validateAnswer(ans) ){
		parent.evaluateAnswer(ans);
		//delete input field
		document.getElementById("answerToSend").value = "";
	} else {
		G_LOGGER.log('invalid answer!');
	}
}

/**
	get notice of new players
*/
function selfConnected(inSessionID, inSelfName){
	G_PLAYER = new Player(inSessionID, inSelfName);
	refreshOwnScore();
}

function playerConnected(inSessionID, inPlayerName, inScore){
	//forward the information to Game
	G_GAME.addOpponent(inSessionID, inPlayerName, inScore);
	console.log('  playerConnected::addOpponent() executed')
	
	//rebuild opponent DIV
	rebuildOpponentDiv();
	console.log('  playerConnected::rebuildOpponentDiv() executed')
	
	//show log
	G_LOGGER.log(inPlayerName + ' joined.');
}

function rebuildOpponentDiv(inLastChgSessionID){
	//TBD
	var html = "Opponents:<br/>";
	for (var [key, value] of G_GAME.mapSessionPlayer.entries()) {
		var scoreClass = (key==inLastChgSessionID ? 'opponentScoreLast' : 'opponentScore');
		html += ("<span class='opponentName'>"+value.getName() + "</span><span class='"+scoreClass+"'> (" + value.getScore() + ")</span>  -- " + key + "<br/>");
		console.log('   rebuildOpponentDiv :: '+value.getName()+'->'+value.getScore());
	}
	document.getElementById("divOpponents").innerHTML = html;
}

function showScramble(inTxt){
	G_LOGGER.log('new scramble');
	G_GAME.setScramble(inTxt);
	document.getElementById("divPuzzle").innerHTML = "<p class='scramble'>" + inTxt + "</p>";
}

function answerConfirmed(inAnswer, inTxt){
	if( inTxt=="YES" ){
		G_LOGGER.log('GOOD answer!');
		G_PLAYER.incScore();
		G_PLAYER.addGoodAnswer(inAnswer);
		refreshOwnScore();
		refreshOwnResults();
	} else {
		G_LOGGER.log('wrong answer!');
	}
}

function setScore(inTargetSessionID, inUserSessionID, inUserScore){
	console.log("setScore, inTargetSessionID=" + inTargetSessionID + ", inUserSessionID="+inUserSessionID+", inUserScore="+inUserScore);
	if(G_PLAYER.getSessionID()==inUserSessionID){
		//own score received from Server
		G_PLAYER.setScore(inUserScore);
		refreshOwnScore();
		console.log("refreshOwnScore() called");
	} else {
		//someone else's score received from Server
		G_GAME.setOpponentScore(inUserSessionID, inUserScore);
		rebuildOpponentDiv(inUserSessionID);
		console.log("rebuildOpponentDiv() called");
	}
}

function refreshOwnScore(){
	document.getElementById("divOwnScore").innerHTML = "Score: " + G_PLAYER.getScore();
}

function refreshOwnResults(){
	var html = "<span class='resultsHeader'>Good solutions so far:</span><br/>"
	var arr = G_PLAYER.getGoodAnswers();
	for(var i=0; i<arr.length;i++){
		html += (arr[i] + "<br/>");
	}
	document.getElementById("divResults").innerHTML = html;
}

function userQuit(inSessionID){
	G_GAME.setOpponentScore(inSessionID, 0);
	G_GAME.removePlayer(inSessionID);
	rebuildOpponentDiv(inSessionID);
}

function showParentLog(inTxt){
	G_LOGGER.log( inTxt );
}

function setTitle(inTxt){
	document.getElementById("divTitle").innerHTML = "<h4>"+inTxt+"</h4>";
}

function setClock(inValue){
	document.getElementById("divClock").innerHTML = "<span class='clock'>"+inValue+"</span>";
}

function leaveRoom(){
	console.log("iFrame: [Leave room] clicked... dispatch to parent");
	parent.leaveRoom();
}