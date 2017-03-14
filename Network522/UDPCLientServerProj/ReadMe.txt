Mahmood uz Zaman
E01407909
Network UDP socket Programming Project-1
COSC 522

To compile the client program, type: gcc -o uclient.exe UdpClient.c  DieWithError.c
To execute the client program, type: ./uclient.exe 127.0.0.1 21234 

To compile the server program, type: gcc -o userver.exe UdpServer.c DieWithError.c -lm
To run the server program, type: ./userver.exe 21234

I used some text files to store some of data:
userIds.txt contain valid users, can be added more intergers(max=20)
testfile.txt contains table of user posts.
ClientMessage.h and ServerMessage.h that respectively contain the structs used for the clients message and the servers message.
One extra header file is added for leader Ids LeaderIds.h that is struct to store all leader id's
a user is following.

Note: programs still behave buggy when I applied forking that's why some segments are commented
but if I took it out it works for all major functionalities(login,post,follow,unfollow, logout) and
perform proper action on server side.

10:56 PM 11/13/2016