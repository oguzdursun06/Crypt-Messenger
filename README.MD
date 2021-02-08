## Client-Server Encryption/Decryption Messaging Application

***Information Security Lab Assignment*** 


- ***Oğuzhan DURSUN*** 


- ***Alperen Toraman*** 

`$ Client: Client.java`

`$ Server: Server.java`

#### Requirements
- Program support two encryption modes (CBC, OFB) and two encryption algorithms (AES, DES).
- Padding mode set to “PKCS5 Padding”

The server applications should be started first and wait for incoming connections. All encrypted messages received by the server will be distributed to clients directly. Besides, when the server starts, it will produce a random key and
random initialization vector (IV) and send these key and vector to all the clients. Finally, the server must write, produced random key, produced IV and the encrypted message sent to itself with information about user name who sent the
message to it’s console and must create/update a log file (log.txt) whenever a message is sent to itself (All these informations must be in Base64 Encoded (except “username”, username will be displayed as normal text )


Client application should have similar GUI. In the GUI There are three textboxes. First and the biggest textbox is for messages. Incoming messages will be displayed in this textbox. Second textbox titled text is for
message writing. After typing a message user will press encrypt button to cipher the message. The encryption method and mode can be selected by using radio buttons top of the window. Then user should press send button to send encrypted message to server

![4x80d9](https://user-images.githubusercontent.com/72974967/107277864-149d6580-6a66-11eb-9c21-823d57dcae6f.gif)

![crypt1](https://user-images.githubusercontent.com/72974967/107280271-2a605a00-6a69-11eb-9a9f-8302311789ff.png)
![crypt2](https://user-images.githubusercontent.com/72974967/107280277-2d5b4a80-6a69-11eb-862d-1e774f48605f.png)
![crypt3](https://user-images.githubusercontent.com/72974967/107280284-2fbda480-6a69-11eb-808f-8a49115827ec.png)

