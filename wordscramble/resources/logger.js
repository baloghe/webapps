/** 
	Class for logging
*/
class Logger{
	/*
	var targetDiv;
	var numToShow;
	array arrLog;
	*/
	constructor(inTargetDiv, inNumToShow){
		this.targetDiv = document.getElementById( inTargetDiv );
		//this.targetDiv = inTargetDiv;
		this.numToShow = inNumToShow;
		this.arrLog = new Array();
	}
	
	log( inTxt ){
		console.log("Logger :: inTxt=" + inTxt);
		this.arrLog.unshift( inTxt );
		this.refreshDiv();
	}
	
	refreshDiv(){
		var html = "";
		for(var i=0; i<this.arrLog.length && i < this.numToShow; i++){
			html += ( this.arrLog[i] + '<br/>' );
		}
		//document.getElementById( 'frmMain' ).contentWindow.document.getElementById( this.targetDiv ).innerHTML = html;
		this.targetDiv.innerHTML = html;
	}
}