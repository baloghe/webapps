# webapps
First adventures in the world of Java Servlets, Server Pages, Facelets, AJAX calls, Websockets

<h2>Adventure 0</h2>
"Pure" webapps without relying on any project management tool (e.g. MAVEN), designed to be copied directly under tomcat when run on localhost...

<h3>Lückentext</h3>
Lückentext (english: Cloze-test) is a type of grammar test where certain words are missing from an otherwise coherent tesxt and the participant has to fill in the gaps by typing the missing words.<br/>
The app operates with Servlets and without a database (instead: texts are provided in a static XML file)

<h3>Variations on Tic-Tac-Toe</h3>
<p><strong>TicTacToe1:</strong> Java Servlet version of the fancied Tic-Tac-Toe game (computer opponent taking random moves)</p>
<p><strong>TicTacToe2:</strong> same functionality with Java Server Pages (JSP) + two possible opponent strategies at disposal</p>

<h3>Variations on the NumberGuess theme</h3>
<strong>numguess1:</strong> "vanilla" based on instructions at ntu.edu, using JSP
<ul><li>app.NumberGuessBean</li>
<li>app.Guess -> only to keep track of former guesses</li>
<li>numguess1.jsp</li></ul>
This one does not even need web.xml, only these 3 files. Start (on localhost): http://localhost:9999/numguess1/numguess1.jsp
<br/>
<strong>numguess2:</strong> visual in that guessable numbers are shown as either buttons (number has not been tried yet) or as a label+relational operator (<, >) when a guess has been made, indicating whether the correct answer is smaller or grater than the guess was
<br/>
<strong>numguess3:</strong> JSF version of numguess1. Start (on localhost): http://localhost:9999/numguess3/faces/numguess3.xhtml
<br/>
<strong>numguess4:</strong> JSF version of numguess2, showing only the set of available numbers as potential guesses as <em>h:commandButton</em> elements
<br/>
<strong>numguess4ajax:</strong> almost the same except that clicking on a button only the actual form is refreshed through an AJAX call
