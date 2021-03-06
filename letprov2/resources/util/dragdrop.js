var G_DDLISTS = null;
var G_COUNTER = 0;

if (!DragDropList) var DragDropList = function(inHtmlId, inReflexHtmlId){

	this.htmlID = inHtmlId;	
	this.reflexHtmlID = inReflexHtmlId;
	//console.log("DragDropList for htmlID="+this.htmlID+" created");

	this.initialize = function(){

		this.letterCnt = 0;

		//generate inLetterMap
		var inLetterMap = new Map();
		for(var i=1; i<=6; i++){
			var letter = document.getElementById("flipCard-front-fc"+i).innerHTML;
			var tmpcnt = 1;
			if(inLetterMap.has(letter)){
				tmpcnt = inLetterMap.get(letter)+1;
			}
			inLetterMap.set(letter,tmpcnt);
			//console.log("   generate inLetterMap :: "+i+" -> "+letter+" -> count: "+tmpcnt);
		}

		//copy letter list
		this.letterUpperBound = new Map(); //enabled upper bound for letters
		this.letterCount = new Map();      //actual count of letters
		for(const [key, elem] of inLetterMap){
			this.letterUpperBound.set(key, elem);
			this.letterCount.set(key, 0);
			//console.log("DragDropList :: letter=" + key + " -> " + elem);
		}

		//register itself
		G_DDLISTS.set(this.htmlID, this);

		//if the (physical) list has any letters, throw them away
		var domLst = document.getElementById(this.htmlID);
		if(domLst.childNodes.length>0){
		var dellist = new Array();
			for(var nd of domLst.childNodes){
				if(nd.nodeType==1){
					dellist.push(nd);
				}
			}
			for(var nd of dellist){
				domLst.removeChild(nd);
			}
		}
		updateReflex(inReflexHtmlId, this.getWord());
		domLst.innerHTML = "Drag letters here!";

		//console.log("DragDropList for htmlID="+this.htmlID+" initialized");
	}

	this.isLetterEnabled = function(inLetter){
		if(this.letterUpperBound.has(inLetter)){
			var actCnt = this.letterCount.get(inLetter);
			var upperBound = this.letterUpperBound.get(inLetter);
			//console.log("isLetterEnabled :: "+inLetter+" actCnt="+actCnt+", upperBound="+upperBound);
			if(actCnt < upperBound) return true; else return false;
		} else {
			console.log("DragDropList.isLetterEnabled for htmlID="+this.htmlID+" :: "+inLetter+" has no upper bound");
			return false;
		}
	}

	this.getWord = function(){
		var domLst = document.getElementById(this.htmlID);
		if(domLst.childNodes.length==0){
			return "";
		} else {
			var ret = "";
			for(var nd of domLst.childNodes){
				if(nd.nodeType==1){
					if( nd.hasAttribute('data-DDType') ){
						if(nd.getAttribute('data-DDType') == 'elem'){
							ret += nd.innerHTML;
						}
					}
				}//element node
			}//next nd
		}
		return ret;
	}

	this.addLetter = function(inLetter){
		//add letter to the end
		var elem = createDiv(inLetter);
		var domLst = document.getElementById(this.htmlID);
		if( this.letterCnt==0 ){
			domLst.innerHTML = "";
		}

		this.letterCnt++;
		var lcnt = this.letterCount.get(inLetter);
		this.letterCount.set(inLetter,lcnt+1);

		domLst.appendChild(elem);
		updateReflex(inReflexHtmlId, this.getWord());
	}

	this.addLetterBefore = function(inLetter, inBeforeObj){
		//add letter to the end
		var elem = createDiv(inLetter);
		var domLst = document.getElementById(this.htmlID);

		this.letterCnt++;
		var lcnt = this.letterCount.get(inLetter);
		this.letterCount.set(inLetter,lcnt+1);

		domLst.insertBefore(elem, inBeforeObj);
		updateReflex(inReflexHtmlId, this.getWord());
	}

	this.refresh = function(){
		var domLst = document.getElementById(this.htmlID);

		this.letterCnt=0;
		this.letterCount = new Map();
		for(const[key,elem] of this.letterUpperBound){
			this.letterCount.set(key,0);
		}

		for(var nd of domLst.childNodes){
			if(nd.nodeType==1){
				if( nd.hasAttribute('data-DDType') ){
					if(nd.getAttribute('data-DDType') == 'elem'){
						this.letterCnt++;
						var letter = nd.innerHTML;
						var actcnt = 1;
						if(this.letterCount.has(letter)){
							actcnt = this.letterCount.get(letter)+1;
						}
						this.letterCount.set(letter,actcnt);
						//console.log("refresh "+letter+" -> "+actcnt);
					}
				}
			}//element node
		}//next nd

		updateReflex(inReflexHtmlId, this.getWord());
		if(this.letterCnt==0){
			domLst.innerHTML = "Drag letters here!";
		}
	}
}//DragDropList

function updateReflex(inHtmlId, inWord){
	var elem = document.getElementById(inHtmlId);
	elem.innerHTML = inWord;
}

function dragStartFromProvider(inEvent){
	inEvent.stopPropagation();
	var targetHtmlId = inEvent.target.id;
	var targetLetter = inEvent.target.innerText;
	//console.log('handleDrag :: target=' + inEvent.target.id + " -> targetLetter=" + targetLetter);


	//add info as JSON object
	var dragInfo={
		letter: targetLetter,
		sourceDDEID: null,
		sourceType: "provider",
		sourceListID: null
	};
	var jstr = JSON.stringify(dragInfo);

	inEvent.dataTransfer.setData('text/plain', jstr);

	/*
	//https://stackoverflow.com/questions/20539196/creating-svg-elements-dynamically-with-javascript-inside-html
	var circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
	circle.setAttribute('cx', '25');
	circle.setAttribute('cy', '25');
	circle.setAttribute('r', '25');
	circle.setAttribute('style', 'fill:purple');
	var dragIcon = document.createElementNS("http://www.w3.org/2000/svg", "svg");
	dragIcon.setAttribute('width', '50');
	dragIcon.setAttribute('height', '50');
	dragIcon.appendChild(circle);
	*/
	var dragIcon = getSVG(targetLetter);

	var canvas = document.createElement("canvas");
	var bbox = dragIcon.getBBox();
	canvas.width = bbox.width;
	canvas.height = bbox.height;
	var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, bbox.width, bbox.height);
	var data = (new XMLSerializer()).serializeToString(dragIcon);
	var svg64 = btoa(data);
	var b64Start = 'data:image/svg+xml;base64,';
	var image64 = b64Start + svg64;
	var img = new Image();
	img.src = image64;
	inEvent.dataTransfer.setDragImage(img, 25, 25);
	inEvent.target.style.cursor = "grabbing";
}//dragStartFromProvider

function getSVG(inLetter){
	/* https://stackoverflow.com/questions/9281199/adding-text-to-svg-document-in-javascript */

	var txt = document.createElementNS("http://www.w3.org/2000/svg", 'text');
	txt.setAttribute('x', '25');
	txt.setAttribute('y', '40');
	txt.setAttribute('style', 'text-anchor: middle;fill:linen;stroke:maroon;font-family:Verdana, Geneva, sans-serif;font-size:40px;font-weight:bold;');
	txt.innerHTML = inLetter;

	var ret = document.createElementNS("http://www.w3.org/2000/svg", "svg");
	ret.setAttribute('width', '50');
	ret.setAttribute('height', '50');
	ret.appendChild(txt);

	return ret;
}

function dragLeaveFromProvider(inEvent){
	inEvent.stopPropagation();
	inEvent.target.style.cursor = "default";
}//dragLeaveFromProvider

function allowDropOnList(inEvent) {
	inEvent.preventDefault();
}//allowDropOnList

function handleDropOnList(inEvent){
	inEvent.stopPropagation();

	var jstr = inEvent.dataTransfer.getData("text");
	var dragInfo = JSON.parse(jstr);

	var actLetter = dragInfo.letter;
	var draggedFrom = dragInfo.sourceType;

	var sourceDDE=null;
	var sourceDDList=null;
	var sourceDDListObject=null;
	if(draggedFrom=="list"){
		sourceDDE = document.getElementById(dragInfo.sourceDDEID);
		sourceDDList = document.getElementById(dragInfo.sourceListID);
		sourceDDListObject = G_DDLISTS.get(sourceDDList.id);
	}

	var targetDDList = inEvent.target;
	var targetDDListObject = G_DDLISTS.get(targetDDList.id);
	//console.log("handleDropOnList :: candidateLetter=" + actLetter + " --> targetDDList=" + targetDDList.id);

	var enabled = targetDDListObject.isLetterEnabled(actLetter);
	//console.log("   enabled=" + enabled + (dragInfo.sourceType=="list" && dragInfo.sourceListID===targetDDList.id ? " but within same list! => TRUE" : "") );


	//where does the letter come from?
	if(dragInfo.sourceType=="list"){
		if( sourceDDList.id===targetDDList.id
           || enabled ){
			//has it been dragged from a list, we should remove it from the source
			//remove dragged item from source
			sourceDDList.removeChild( sourceDDE );
			//add dragged item before target item
			targetDDListObject.addLetter(actLetter);

			sourceDDListObject.refresh();
			targetDDListObject.refresh();
		}
	} else if(enabled){
		//provider => simply add it...
		targetDDListObject.addLetter(actLetter);
	}//list or provider

}//handleDrop

function dragStartLetter(inEvent){
	inEvent.stopPropagation();
	var targetObject = inEvent.target;
	var actLetter = targetObject.innerHTML;
	var sourceDDList = targetObject.parentNode;

	//add info as JSON object
	var dragInfo={
		letter: actLetter,
		sourceDDEID: targetObject.id,
		sourceType: "list",
		sourceListID: sourceDDList.id
	};
	var jstr = JSON.stringify(dragInfo);

	inEvent.dataTransfer.setData('text/plain', jstr);
}//dragStartLetter

function dragLeaveLetter(inEvent){
	inEvent.stopPropagation();
}//dragLeaveLetter

function dropOnLetter(inEvent){
	inEvent.stopPropagation();
	var targetObject = inEvent.target;
	var jstr = inEvent.dataTransfer.getData("text");

	var dragInfo = JSON.parse(jstr);
	var actLetter = dragInfo.letter;
	var draggedFrom = dragInfo.sourceType;

	var sourceDDE=null;
	var sourceDDList=null;
	var sourceDDListObject=null;
	if(draggedFrom=="list"){
		sourceDDE = document.getElementById(dragInfo.sourceDDEID);
		sourceDDList = document.getElementById(dragInfo.sourceListID);
		sourceDDListObject = G_DDLISTS.get(sourceDDList.id);
	}

	var targetDDList = targetObject.parentNode;
	var targetDDListObject = G_DDLISTS.get(targetDDList.id);
	/*
	console.log("dropOnLetter :: type="+draggedFrom
		+(  draggedFrom=="list" ? "sourceDDEID="+dragInfo.sourceListID+", sourceDDE="+sourceDDE+", targetObject="+targetObject
		                        : "targetDDList.id="+targetDDList.id+", actLetter="+actLetter
		 )
		);
	*/

	//check if...
	if( draggedFrom=="provider" ){
		//dragged from the Provider => check if...
		//	anything has been dragged
		//  the letter is enabled on the target		
		if(   inEvent.dataTransfer.items.length > 0
		   && targetDDListObject.isLetterEnabled(actLetter)){
			targetDDListObject.addLetterBefore(actLetter, targetObject);
		}
	} else if( draggedFrom=="list" ){
		if( sourceDDList.id===targetDDList.id ){
			//move within same list => enabled for sure
			//remove dragged item from source
			var removed = sourceDDList.removeChild( sourceDDE );
			//add dragged item before target item
			targetDDList.insertBefore(removed, targetObject);
			targetDDListObject.refresh();

		} else {
			//dragged from another List => check if...
			//	anything has been dragged
			//  the letter is enabled on the target
			if(   inEvent.dataTransfer.items.length > 0
			   && targetDDListObject.isLetterEnabled(actLetter)){
				//remove dragged item from source
				sourceDDList.removeChild( sourceDDE );
				//add dragged item before target item
				targetDDList.insertBefore(sourceDDE, targetObject);

				sourceDDListObject.refresh();
				targetDDListObject.refresh();
			}
		}//endif different List	
	}//endif draggedFrom
}//handleDropOnLetter

function deleteLetter(inEvent){
	var targetObject = inEvent.target;
	var letter = targetObject.innerHTML;
	var targetDDList = targetObject.parentNode;
	//console.log("deleteLetter :: letter=" + letter + " from list=" + targetDDList.id);

	targetDDList.removeChild(targetObject);
	var targetDDListObject = G_DDLISTS.get(targetDDList.id);
	targetDDListObject.refresh();
}//deleteLetter

function createDiv(inLetter){
	var ret = document.createElement("div");
	ret.setAttribute('class', 'dragDropElement');
	ret.setAttribute('id', "DDE"+genId());
	ret.setAttribute('data-DDType', 'elem');
	ret.setAttribute('draggable', 'true');
	ret.setAttribute('ondragstart', 'dragStartLetter(event)');
	ret.setAttribute('ondragleave', 'dragLeaveLetter(event)');
	ret.setAttribute('ondrop', 'dropOnLetter(event)');
	ret.setAttribute('ondblclick', 'deleteLetter(event)');
	ret.innerHTML = inLetter;
	return ret;
}//createDiv

function genId(){
	return (++G_COUNTER);
}

function onFcAnimEnded(inEvent){
	if(inEvent!=undefined){
		//console.log("onFcAnimEnded called, target="+inEvent.target);
		init();
	}
}

function init(){
	//console.log("init called");

	G_DDLISTS = null;
	G_COUNTER = 0;

	G_DDLISTS = new Map();
	for(var i=1; i<=2; i++){
		var s = new DragDropList('divGfxList-'+i,'divListReflex-'+i);
		s.initialize();
		s.refresh();
	}
	/*
	console.log("G_DDLISTS content: len=" + G_DDLISTS.size);
	for(const [key, elem] of G_DDLISTS){
		console.log("   "+key+" -> "+elem);
	}*/

}

