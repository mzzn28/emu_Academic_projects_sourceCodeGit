/*
Student:Mahmood Zaman
Instructor: Dr. Poh

UDP Client for News Feed App
To Compile : gcc -o uclient.exe UdpClient.c  DieWithError.c
*/
#include <stdio.h>      /* for printf() and fprintf() */
#include <sys/socket.h> /* for socket(), connect(), sendto(), and recvfrom() */
#include <arpa/inet.h>  /* for sockaddr_in and inet_addr() */
#include <stdlib.h>     /* for atoi() and exit() */
#include <string.h>     /* for memset() */
#include <unistd.h>     /* for close() */
#include <sys/types.h>

#include "ClientMessage.h"
#include "ServerMessage.h"

#define ECHOMAX 255     /* Longest string to echo */

void DieWithError(char *errorMessage);  /* External error handling function */

int main(int argc, char *argv[])
{
    int sock;                        /* Socket descriptor */
    struct sockaddr_in echoServAddr; /* Echo server address */
    struct sockaddr_in fromAddr;     /* Source address of echo */
    unsigned short echoServPort;     /* Echo server port */
    unsigned int fromSize;           /* In-out of address size for recvfrom() */
    char *servIP;                    /* IP address of server */
    int respStringLen;               /* Length of received response */  
    
    if ((argc < 2) || (argc > 3))    /* Test for correct number of arguments */
    {
       fprintf(stderr,"Usage: %s <1: Server IP> [2: <Echo Port>]\n", argv[0]);
       exit(1);
    }
    servIP = argv[1];           /* First arg: server IP address (dotted quad) */
    if (argc == 3)
        echoServPort = atoi(argv[2]);  /* Use given port, if any */
    else
        echoServPort = 7;  /* 7 is the well-known port for the echo service */
    printf("in UDPClient");
    /* Create a datagram/UDP socket */
    if ((sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0)
        DieWithError("socket() failed");
    
    /* Construct the server address structure */
    memset(&echoServAddr, 0, sizeof(echoServAddr));    /* Zero out structure */
    echoServAddr.sin_family = AF_INET;                 /* Internet addr family */
    echoServAddr.sin_addr.s_addr = inet_addr(servIP);  /* Server IP address */
    echoServAddr.sin_port   = htons(echoServPort);     /* Server port */

	// struct for the server message that the client will recieve from server 
    ServerMessage serverMsg;
	int serverMsgLen = sizeof(serverMsg);
    memset(&serverMsg, 0, serverMsgLen);
    
    // get username and password from the user; put it into clientMessage struct 
    ClientMessage clientMsg;
    int clientMsgLen = sizeof(clientMsg);
    memset(&clientMsg, 0, clientMsgLen);
	//welcome Prompt
	printf("\n");
	printf("**************************************\n");
	printf("* welcome to Message Feed From Friends\n* To continue:\n* Please provide userId \n* or Enter 0 to quit\n");
	printf("**************************************\n");
	
	//child process for news feed
	//pid_t pid=fork();
	
	//authenticating the user
	ValidateUser: do { 
        printf("Enter Your userID / 0 to quit: ");
        scanf("%d", &clientMsg.UserID);
        if (clientMsg.UserID == 0) {
            printf("Exiting program\n");
            exit(0);
        }
        clientMsg.request_Type = Login;
		//send to server
        if (sendto(sock, &clientMsg, clientMsgLen, 0, (struct sockaddr *)
                   &echoServAddr, sizeof(echoServAddr)) != clientMsgLen)
            DieWithError("sendto() sent a different number of bytes than expected");
        
        // Receive a response
        fromSize = sizeof(fromAddr);
        if ((respStringLen = recvfrom(sock, &serverMsg, serverMsgLen, 0,
                                      (struct sockaddr *) &fromAddr, &fromSize)) != serverMsgLen)
            DieWithError("recvfrom() failed");
        
        if (echoServAddr.sin_addr.s_addr != fromAddr.sin_addr.s_addr)
        {
            fprintf(stderr,"Error: received a packet from unknown source.\n");
            exit(0);
        }
        //check server message response
        if (serverMsg.response_Type == Ok) {
            printf("\n User authenticated");
            break;
        }else if (serverMsg.response_Type == Nok){
            printf("invalid login credentials; you will have to re-enter your credentials.\n");
        }else {
            printf("undefined message from server; you will have to re-enter your credentials.\n");
        }
    }while (1);//infinite loop infinitely until a break / exit hits.
	
	/*user is authenticated,now implementation of main
	function post,follow, unfollow etc.  to get news feed!*/
	do{
		printf("\n*******************************\n");
		printf("Friends News Feed Selection:\n");
        printf("* 1: Post a Message\n");
        printf("* 2: Follow a friend\n");
        printf("* 3: UnFoloow Friends\n");
        printf("* 4: To logout\n");
        printf("* 0: quit any time \n");
		printf("*******************************\n");
        printf("your selection Number: ");
        
			int choice;
			scanf("%d", &choice);
			int c;
			do{	//need to manually remove the rest of the current line(stack overflow) to read via fgetc
				c = getchar();
			}while(c != EOF && c != '\n');
		
        if (choice == 1){//post a message
			//read message in clientMsg.message    
			printf("Enter Your message to Post, 100 char only: ");
			//scanf("%100s", clientMsg.message);
			//printf("\n your message:%s", clientMsg.message);
            fgets(clientMsg.message,100,stdin); //only read 100 char
			printf("your message read is(100 char only): %s", clientMsg.message);
            
			clientMsg.request_Type = Post;
        }else if(choice == 2){//Follow a leader
			//read the leader ID   
			printf("Enter Leader ID to follow: ");
            scanf("%d", &clientMsg.LeaderID);
            clientMsg.request_Type = Follow;
            
        }else if (choice == 3){//UnFoloow
			//read the leader ID   
			printf("\n Enter Leader ID to follow: ");
            scanf("%d", &clientMsg.LeaderID);
            clientMsg.request_Type = UnFollow;
		
        }else if (choice == 4){//logout
			clientMsg.request_Type=Logout;
        }else if(choice == 0){//quit
            clientMsg.request_Type = Logout;
            if (sendto(sock, &clientMsg, clientMsgLen, 0, (struct sockaddr *)
                       &echoServAddr, sizeof(echoServAddr)) != clientMsgLen)
                DieWithError("sendto() sent a different number of bytes than expected");
            printf("exiting program.\n");
            exit(0);
        }else{//undetermined
            printf("no valid choice selected; select a valid choice.\n");
            continue;
        }

        /* After taking user appropriate choice, 
         send the message to the server and await for response. */
        
        if (sendto(sock, &clientMsg, clientMsgLen, 0, (struct sockaddr *)
                   &echoServAddr, sizeof(echoServAddr)) != clientMsgLen)
            DieWithError("sendto() sent a different number of bytes than expected");
        
         //Recv a response 
			fromSize = sizeof(fromAddr);
			if ((respStringLen = recvfrom(sock, &serverMsg, serverMsgLen, 0,
										  (struct sockaddr *) &fromAddr, &fromSize)) != serverMsgLen)
				DieWithError("recvfrom() failed");
			
			if (echoServAddr.sin_addr.s_addr != fromAddr.sin_addr.s_addr)
			{
				fprintf(stderr,"Error: received a packet from unknown source.\n");
				exit(0);
			}
		/*
		/*if any news feed comes then...*/
		/* if(pid==0)
		{
			fromSize = sizeof(fromAddr);
			if ((respStringLen = recvfrom(sock, &serverMsg, serverMsgLen, 0,
										  (struct sockaddr *) &fromAddr, &fromSize)) != serverMsgLen)
				DieWithError("recvfrom() failed");
			if (echoServAddr.sin_addr.s_addr != fromAddr.sin_addr.s_addr)
			{
				fprintf(stderr,"Error: received a packet from unknown source.\n");
				//exit(0);
			}
			// print the forked news feed 
			 if (serverMsg.response_Type == Ok) {
				printf("*******************************\n");
				printf("User: %d, posted: %s\n",serverMsg.LeaderID,serverMsg.message);
				printf("*******************************\n");
			 }
		} */
        
        /* Now I have the response from the server in the serverMessage struct */
        if (serverMsg.response_Type == Ok) {
            if (clientMsg.request_Type == Post) {
              printf("successfully Posted the message: %s\n", clientMsg.message); 
            }else if(clientMsg.request_Type == Follow){
               printf(" User: %d added to your Follow list\n", clientMsg.LeaderID);
				//wait for news feed
				
            }else if (clientMsg.request_Type == UnFollow){
				printf(" User: %d removed from Follow list\n", clientMsg.LeaderID);
            }else if(clientMsg.request_Type == Logout){
                printf("you have logged out; you may log in again if you wish, or quit.\n");
                goto ValidateUser;//go back up so the user can start over
            }
        }
        else {
            printf("Unexpected Message received \n");
        }
    }while (1);//loop infinitely until a break statement or a call to exit()
	 
	 //end program and close socket exit
	 printf("end of Client App");
    close(sock);
    exit(0);
}
	