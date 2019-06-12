if (!flipcard) var flipcard = {}
 
/**
               class LetterModel
*/
if (!flipcard.LetterModel) {
 
               flipcard.LetterModel = function(inHtmlID,inIdPfx,inClassPfx,inFlipCntBase,inFlipCntRange,inFlipOffset,inAlphabet){
              
                              /* public properties:*/
                              this.htmlID                        = inHtmlID;
                              this.idPrefix                       = inIdPfx;
                              this.classPrefix   = inClassPfx;
                              this.flipCntBase = Number(inFlipCntBase);
                              this.flipCntRange             = Number(inFlipCntRange);
                              this.flipCntOffset             = Number(inFlipOffset);
                             
                              this.standardAlphabet = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
                              this.alphabet = (inAlphabet==null ? this.standardAlphabet : inAlphabet );
                             
                              this.getRandomChar = function(){
                                            return this.alphabet[ Math.floor(Math.random() * this.alphabet.length) ];
                              }
                             
                              this.draw = function(){
                                            document.getElementById(this.idPrefix+"-front-"+this.htmlID).innerHTML = this.frontChar;
                                            document.getElementById(this.idPrefix+"-back-"+this.htmlID).innerHTML = this.backChar;
                              }
                             
                              this.face = 0;
                              this.animCnt = 0;
                              this.frontChar = this.getRandomChar();
                              this.backChar = this.getRandomChar();
                              this.expectedOutcome = null;
                              this.draw();
                              this.isAnimating = false;
                             
                              this.toString = function(){
                                            return "[LetterModel: id="+inHtmlID+"]";
                              }
                             
                              this.playExpected = function(inExpectedOutcome){
                                            this.expectedOutcome = inExpectedOutcome;
                                            this.play();
                              }
                             
                              this.play = function(){
                                            this.face = 0;
                                            this.animCnt = this.flipCntBase + Math.floor(Math.random() * this.flipCntRange);
                                            this.isAnimating = true;
                                            var offset = Math.floor(Math.random() * this.flipCntOffset);
                                            //console.log(this.htmlID + " :: offset=" + offset);
                                            this.animStart(offset);
                              }
                             
                              this.animStart = function(inOffset){
                                            var elem = document.getElementById(this.idPrefix + "-inner-"+this.htmlID);
                                            //console.log(this.htmlID + "started");
                                            if(this.flipCntOffset != null) elem.style.transitionDelay = inOffset+"ms";
                                            elem.classList.add(this.classPrefix+"-animation");
                              }
                             
                              this.animEnd = function(){
                                            this.changeFace();
                                            var elem = document.getElementById(this.idPrefix + "-inner-"+this.htmlID);
                                            elem.classList.remove(this.classPrefix+"-animation");
                                            if( this.face % 2 == 0 ) {
                                                           //decrease animation counter
                                                           this.animCnt--;
                                                           if(this.animCnt > 0){
                                                                          //animate again
                                                                          this.animStart();
                                                           } else {
                                                                          //all animations done => enable Play button
                                                                          this.isAnimating = false;
                                                                          //console.log( this.htmlID + " stopped." );
                                                           }
                                            }
                              }
                             
                              this.changeFace = function(){
                                            this.face++;
                                            if( this.face % 2 == 0 ){
                                                           this.backChar = this.getRandomChar();
                                                           document.getElementById(this.idPrefix+"-back-"+this.htmlID).innerHTML = this.backChar;
                                            } else if( this.animCnt==1 && this.expectedOutcome != null ) {
                                                           this.frontChar = this.expectedOutcome;
                                                           document.getElementById(this.idPrefix+"-front-"+this.htmlID).innerHTML = this.frontChar;
                                            } else {
                                                           this.frontChar = this.getRandomChar();
                                                           document.getElementById(this.idPrefix+"-front-"+this.htmlID).innerHTML = this.frontChar;
                                            }
                              }
               }
}
 
if (!flipcard.js) {
               flipcard.js = {
                             
                              modelMap: null,
                             
                              idPrefix: null,
                             
                              elemsToEnable: null,
                             
                              setTargetValues: function(inTargetValues){
                                            //console.log("flipcard.js.setTargetValues called, inTargetValues="+inTargetValues);
                                            var jsonObject = JSON.parse(inTargetValues);
                                            for(const [key, model] of flipcard.js.modelMap){
                                                           var nextLetter = jsonObject[key];
                                                           //console.log("   "+key+" -> "+nextLetter);
                                                           if(nextLetter != null){
                                                                          model.expectedOutcome = jsonObject[key];
                                                           }
                                            }
                              },
                             
                              init : function(inHtmlID,inIdPfx,inClassPfx,inFlipCntBase,inFlipCntRange,inFlipOffset,inJSonAlphabet) {
                                           
                                                           if(!flipcard.js.modelMap){
                                                                          flipcard.js.modelMap = new Map();
                                                                          //console.log('flipcard.js.init :: new modelMap created -> size=' + flipcard.js.modelMap.size);
                                                           }
                                           
                                                           flipcard.js.idPrefix = inIdPfx;
                                           
                                                           var model = new flipcard.LetterModel(inHtmlID,inIdPfx,inClassPfx,inFlipCntBase,inFlipCntRange,inFlipOffset,inJSonAlphabet);
                                                           flipcard.js.modelMap.set( inHtmlID, model );
                                                           //console.log('flipcard.js.init :: '+model.toString()+' added to modelMap -> size=' + flipcard.js.modelMap.size);
                              },
                             
                              animEnded: function(srcElem, dstElemsToEnable){
                                            var trfID = srcElem.id.replace(flipcard.js.idPrefix+'-inner-','');
                                            //console.log("flipcard.js.animEnded :: srcElem.id=" + srcElem.id + ", trfID=" + trfID);
                                            var model = flipcard.js.modelMap.get(trfID);
                                            //console.log("anim ended :: modelMap.size=" + flipcard.js.modelMap.size + "srcElem.id=" + srcElem.id + " -> model.htmlID=" + model.htmlID);
                                            model.animEnd();
                                            //flipcard.js.enableComponents(dstElemsToEnable);
                                            flipcard.js.enableComponents(flipcard.js.elemsToEnable);
                              },
                             
                              animStart: function(srcElem, dstElemsToEnable, inTargetValues){
                                            flipcard.js.setTargetValues(inTargetValues);
                                            flipcard.js.elemsToEnable = JSON.parse(dstElemsToEnable);
                                            //console.log("flipcard.js.animStart :: srcElem.id=" + srcElem.id + ", dstElemsToEnable=" + dstElemsToEnable);
                                            //var model = flipcard.js.modelMap.get(srcElem.id);
                                            for(const [key, model] of flipcard.js.modelMap){
                                                           //model.animStart();
                                                           model.play();
                                            }
                                            flipcard.js.disableComponents(flipcard.js.elemsToEnable);
                              },
                             
                              disableComponents: function(dstElemsToEnable){
                                            for(var tmpid of dstElemsToEnable){
                                                           var elem=document.getElementById( tmpid );
                                                           if(elem!=null){
                                                                          elem.disabled=true;
                                                           }//elem not null
                                                           else {
                                                                          console.log("disableComponents :: tmpid="+tmpid+" has no matching element!");
                                                           }
                                            }
                              },
                             
                              enableComponents: function(dstElemsToEnable){
                                           
                                            //enable Play button when all letters are still
                                            var b = false;
                                            for(var [key, model] of flipcard.js.modelMap ){
                                                           b = b || model.isAnimating;
                                            }
                                            if( !b && dstElemsToEnable != null ){
                                                           for(var tmpid of dstElemsToEnable){
                                                                          var elem=document.getElementById( tmpid );
                                                                          if(elem!=null){
                                                                                         elem.disabled=false;
                                                                          }//elem not null
                                                                          else {
                                                                                         console.log("enableComponents :: tmpid="+tmpid+" has no matching element!");
                                                                          }
                                                            }//next tmpid
                                            }
                              }
               }//end flipcard.js
}