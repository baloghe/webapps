﻿# webapps
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

<h3>Websockets: The classical Chat application and beyond...</h3>
<p><strong>chat1:</strong> Simple chat application with Html/CSS/Javascript and Java backend using the Websocket API. No Facelets this time </p>
<p><strong>wordscramble:</strong> given a fixed bag of words, User is presented a randomly chosen element with shuffled characters, and he/she has to submit the unscrambled version. Whenever a good answer is sent in, user score is incremented and a new scramble is broadcasted to the participants. Should more than one player be connected, they compete against each other in that the fastest submitter wins the point...<ul>
<li>WebSocket is used to establish and maintain connection throughout the game
<li>Server Endpoint (Java) is responsible to maintain the actual list of users with their respective scores, broadcast score changes and generate new scrambles whenever a good solution has been sent in
<li>Client Endpoint (Html5+CSS+Javascript) is responsible to manage user interactions with the web page and to present the actual scramble along with opponent score values
<li>score calculation is overlapping between the two sides in that the client side is able to calculate player score (e.g. increasing it by one in case of a server-side confirmation of the answer sent in), however the final score is broadcasted by the server in a separate message
<li>the client side is not provided with the solution to the actual scramble, however a validation on the answer could be performed prior to submission (to prove whether it is really a reordering of the character sequence given in the scramble)
<li>the game itself (game.html) is nested into an IFRAME element on the page (index.html). The latter (along with its Javascript objects) is mostly responsible to translate messages to Client actions and vice versa.
</ul></p>