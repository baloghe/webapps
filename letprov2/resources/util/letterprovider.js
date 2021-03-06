if (!letterprovider) var letterprovider = {}

if (!letterprovider.LetterProviderModel) {
	letterprovider.LetterProviderModel = function(inHtmlID){

		/* public properties:*/
		this.htmlID 		= inHtmlID;
		this.letters		= new Map(); /* flipCard HtmlID -> flipcard.js.LetterModel */

		this.addCard = function(inCardId){
			var cardCollection = flipcard.js.modelMap.get(this.htmlID);
			this.letters.set(inCardId, cardCollection.get(inCardId));
		}

	}//end of flipcard.LetterProviderModel
}//end of (!letterprovider.LetterProviderModel)

if (!letterprovider.js) {
	letterprovider.js = {

		modelMap: null,

		init: function(inHtmlID){
			if(!letterprovider.js.modelMap){
				letterprovider.js.modelMap = new Map();
			}

			var model = new letterprovider.LetterProviderModel(inHtmlID);
			letterprovider.js.modelMap.set( inHtmlID, model );
		},

		addCard: function(inLetterProviderID, inFlipCardId){
			var model = letterprovider.js.modelMap.get(inLetterProviderID);
			model.addCard(inFlipCardId);
		},

		animStart: function(dstProviderIDs, dstElemsToEnable, inTargetValues){
			flipcard.js.animStart(dstProviderIDs,dstElemsToEnable,inTargetValues);
		}

	}//end of letterprovider.js
}//end of (!letterprovider.js)
