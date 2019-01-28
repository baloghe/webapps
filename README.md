# webapps
First adventures in the world of Java Servlets, Server Pages, Facelets, AJAX calls, Websockets

<h2>Adventure 0</h2>
"Pure" webapps without relying on any project management tool (e.g. MAVEN), designed to be copied directly under tomcat when run on localhost...

<h3>Lückentext</h3>
Lückentext (english: Cloze-test) is a type of grammar test where certain words are missing from an otherwise coherent tesxt and the participant has to fill in the gaps by typing the missing words.<br/>
The app operates with Servlets and without a database (instead: texts are provided in a static XML file)

<h3>Variations on the NumberGuess theme</h3>
<p><strong>numguess1:</strong> "vanilla" based on instructions at ntu.edu, using JSP
<ul><li>app.NumberGuessBean</li>
<li>app.Guess -> only to keep track of former guesses</li>
<li>numguess1.jsp</li></ul>
This one does not even need web.xml, only these 3 files. Start (on localhost): http://localhost:9999/numguess1/numguess1.jsp
<strong>numguess2:</strong> visual in that guessable numbers are shown as either buttons (number has not been tried yet) or as a label+relational operator (<, >) when a guess has been made, indicating whether the correct answer is smaller or grater than the guess was</p>
<p><strong>numguess3:</strong> JSF version of numguess1. Start (on localhost): http://localhost:9999/numguess3/faces/numguess3.xhtml</p>
<p><strong>numguess4:</strong> JSF version of numguess2, showing only the set of available numbers as potential guesses as <em>h:commandButton</em> elements</p>
<p><strong>numguess4ajax:</strong> almost the same except that clicking on a button would refresh only the actual form through an AJAX call</p>

<h3>Variations on Tic-Tac-Toe</h3>
<p><strong>TicTacToe1:</strong> Java Servlet version of the fancied Tic-Tac-Toe game (computer opponent taking random moves)</p>
<p><strong>TicTacToe2:</strong> same functionality with Java Server Pages (JSP) + two possible opponent strategies at disposal</p>
<p><strong>TicTacToe3:</strong> a single page with JSF, AJAX and a little bit of Primefaces. Page refresh with AJAX does not work fr some reason unless you reload the page at least once after starting the app. After a page reload everything works fine...

<h3>Websockets: Chat application</h3>
<p><strong>chat1:</strong> Clients joining the conversation are given a name by the Server. They send short messages to each other. Whenever a message is sent, it appears (as unmodifiable text) on the panel of the others. On such occasions the page must not be reloaded as the recipient might himself/herself be writing a message. However the server filters the messages in that if a message contains a word from a given list (e.g. swearwords) it replaces the entire message with a standard text AND calls upon the sender to comply with profanity rules in the future (by sending him a message that appears in different typesetting as messages from the normal conversation).</p>
