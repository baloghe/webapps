/** 
	Class for Player
*/
class Player{
	/*
	var sessionID;
	var name;
	var score;
	var goodAnswers
	*/
	constructor(inSessionID, inName){
		this.sessionID = inSessionID;
		this.name = inName;
		this.score = 0;
		this.goodAnswers = new Array();
	}
	
	setScore( inScore ){
		this.score = inScore;
	}
	
	incScore(){this.score++;}
	
	getScore(){return this.score;}
	
	getSessionID(){return this.sessionID;}
	
	getName(){return this.name;}
	
	addGoodAnswer(inTxt){this.goodAnswers.unshift(inTxt);}
	
	getGoodAnswers(){return this.goodAnswers;}
}