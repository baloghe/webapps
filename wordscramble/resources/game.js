/** 
	Class for game
*/
class Game{
	/*
	var mapSessionPlayer;
	var actualScramble;
	var ownScore;
	*/
	constructor(){
		this.mapSessionPlayer = new Map();
		this.ownScore = 0;
	}
	
	addOpponent(inSessionID, inPlayerName){
		//session ID exists already?
		if( !this.mapSessionPlayer.has(inSessionID) ){
			var player = new Player( inSessionID, inPlayerName );
			this.mapSessionPlayer.set( inSessionID, player );
		}
	}
	
	setScramble(inTxt){
		this.actualScramble = inTxt;
	}
	
	setOpponentScore(inSessionID, inScore){
		var player = this.mapSessionPlayer.get(inSessionID);
		player.setScore(inScore);
	}
	
	validateAnswer(inTxt){
		var scr = this.actualScramble.split('');
		var ans = inTxt.split('');
		
		if( scr.length != ans.length ){
			return false;
		} else {
			var mpScr = this.stringToMap( this.actualScramble );
			var mpAns = this.stringToMap( inTxt );
			if( mpScr==null || mpAns==null || mpScr.size != mpAns.size ){
				return false;
			} else {
				for(const [key, scrValue] of mpScr.entries()){
					if( !mpAns.has(key) ){
						return false;
					} else {
						var ansValue = mpAns.get(key);
						return (scrValue==ansValue);
					}//key check on mpAns
				}//nex entry in mpScr
			}//map sizes equal
		}//answer lengths equal
	}
	
	stringToMap(inTxt){
		var ret = new Map();
		var txt = inTxt.split('');
		for(var i=0; i<txt.length; i++){
			if(ret.has(txt[i])){
				var k = ret.get(txt[i]);
				k++;
				ret.set( txt[i], k );
			} else {
				ret.set( txt[i], 0 );
			}
		}//next char
		return ret;
	}
	
	removePlayer(inSessionID){
		if(this.mapSessionPlayer.has(inSessionID)){
			this.mapSessionPlayer.delete(inSessionID);
		}
	}
}
