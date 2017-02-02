# Go
- - - -
## Install
For the code from GitHub and put it in a IDE. There should be no need for extra libraries. The GUI is included as code.
- - - -
## Run the code
To start GO you need to run the server in the communication package first and choose a port. Then you can start the client in the communication package and fill in the IP-address from the server and the chosen port. Your client should be connected now! If you want to play against yourself or the AI start another client.

You can choose to be an AI by typing 2 if the question is asked. —> this is hardcode the random AI.

### Play GO
You can play go by typing GO and a board size that meets the requirements from the [protocol](https://github.com/MinThaMie/GoProtocol) and wait for another client to join that wants to play on the same board size. 
For further keywords, see the protocol.

### Test the code
You can run the tests in the test package without any terminal input.
- - - -
## Minor bug with the AI
The AI can play, but needs some encouragement sometimes. Please press enter in the AI terminal when it’s the AIs turn, after approximately two it should do the turns by themselves.