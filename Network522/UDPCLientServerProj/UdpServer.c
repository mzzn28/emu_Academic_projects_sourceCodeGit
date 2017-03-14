/*
Student:Mahmood Zaman
Instructor: Dr. Poh
UDP Server for News Feed App

To compile this program: gcc -o userver.exe UdpServer.c DieWithError.c -lm
To run ./userver.exe <port #>
*/
#include <stdio.h>      /* for printf() and fprintf() */
#include "ClientMessage.h"
#include "ServerMessage.h"
#include "LeaderIds.h"

#include <sys/socket.h> /* for socket() and bind() */
#include <arpa/inet.h>  /* for sockaddr_in and inet_ntoa() */
#include <stdlib.h>     /* for atoi() and exit() */
#include <string.h>     /* for memset() */
#include <unistd.h>     /* for close() */
#include <math.h>
#include <stdlib.h>

#define MAXUSER 20

void DieWithError(char *errorMessage);  /* External error handling function */
int validateUserId(int inputUserId);
int postMessage(int inputUserId,char *strng);
void filluserIdinArr(LeaderIds *arr);
int followLeader(int inputUserId,int leaderId,LeaderIds *leaderArr);
int unFollowLeader(int inputUserId,int leaderId,LeaderIds *leaderArr);
void findleaderId(int inputUserId,int *leaderId,LeaderIds *leaderArr);
int checkNoOfPosts(int inputUserId);

/*Method to get messages for a leader ID*/
void getPosts(int inputUserId,char *posts[]){
	
	FILE *fp = fopen("testfile.txt", "r");
    int bufferSize = 255;
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));
    int i=0;
    while (fgets(buffer, bufferSize, (FILE*)fp) != NULL) {
		int index;
		int currentUserId=0;
		int lenId=3;
		// get 3 digit index(leaderId) of message
		for (index = 0; index < lenId; index++) {
            int numChar = (int)(buffer[index] - '0');
            int multiplier = (int)pow(10, (lenId - 1) - index);
            currentUserId += numChar * multiplier;
        }
		if(currentUserId==inputUserId){
			 posts[i]=&buffer[index+1];
			 i++;
		}
	}
    fclose(fp);  
}
int main(int argc, const char * argv[]) {

	int sock;                        /* Socket */
    struct sockaddr_in echoServAddr; /* Local address */
    struct sockaddr_in echoClntAddr; /* Client address */
    unsigned int cliAddrLen;         /* Length of incoming message */
	struct sockaddr_in clntAddr; 
    unsigned int clintAddrLen;         
    unsigned short echoServPort;     /* Server port */
    int recvMsgSize;                 /* Size of received message */

    ClientMessage clientMsg;
    ServerMessage serverMsg;
	LeaderIds arrOfLeaderIds[MAXUSER];	//assuming max user 20
	
	int userIDs[MAXUSER];
    memset(&userIDs, 0, sizeof(userIDs));
    int indexUserID = 0;
	
	printf("in UDpServer");

	 if (argc != 2)         /* Test for correct number of parameters */
    {
        fprintf(stderr,"Usage:  %s <UDP SERVER PORT>\n", argv[0]);
        exit(1);
    }
	printf("in UDpServer");
	echoServPort = atoi(argv[1]);  /* First arg:  local port */
	/* Create socket for sending/receiving datagrams */
    if ((sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0)
        DieWithError("socket() failed");

    /* Construct local address structure */
    memset(&echoServAddr, 0, sizeof(echoServAddr));   /* Zero out structure */
    echoServAddr.sin_family = AF_INET;                /* Internet address family */
    echoServAddr.sin_addr.s_addr = htonl(INADDR_ANY); /* Any incoming interface */
    echoServAddr.sin_port = htons(echoServPort);      /* Local port */

	/* Bind to the local address */
    if (bind(sock, (struct sockaddr *) &echoServAddr, sizeof(echoServAddr)) < 0)
        DieWithError("bind() failed");
		
		//fill table of users
		filluserIdinArr(arrOfLeaderIds);

for (;;) /* Run forever */
    {
        cliAddrLen = sizeof(echoClntAddr);
        /* Block until receive message from a client */
        if ((recvMsgSize = recvfrom(sock, &clientMsg, sizeof(clientMsg), 0,
                                    (struct sockaddr *) &echoClntAddr, &cliAddrLen)) < 0)
            DieWithError("recvfrom() failed");
		printf("Handling client %s\n", inet_ntoa(echoClntAddr.sin_addr));
        /*
         The server responds to a client based on the client request:
         request types are Login, Logout, Post, Subscribe, Unsusbscribe
         */
        memset(&serverMsg, 0, sizeof(serverMsg));
        switch (clientMsg.request_Type) {
            case Login:
                if (validateUserId(clientMsg.UserID)) {
                    userIDs[indexUserID] = clientMsg.UserID;
                    indexUserID = indexUserID + 1;
					serverMsg.response_Type = Ok;
                    printf("successful login attempt by userID: %d\n", clientMsg.UserID);
					/*login success now check for news feed for return users if any*/
					int leadIds[MAXUSER];	//will save all leaders of this user
					memset(&leadIds, 0, sizeof(leadIds));
					//find corresponding leaders 
					findleaderId(clientMsg.UserID,leadIds,arrOfLeaderIds );
					//I got the leaders,how to find corresponding post
					int i;
					int j;
					char *str[100]; 
					for(i=0;i<20;i++){ //check all leader list 
						
						int lid=leadIds[i];
						if(validateUserId(lid)){ //if valid leader ID then find posts
							getPosts(lid,str);	//get the posts from file
							int n=checkNoOfPosts(lid);
							for(j=0;j<n;j++){
								strcpy(serverMsg.message,str[j]);	//assign to server message
								serverMsg.response_Type=Ok;
								serverMsg.LeaderID=lid;
								//send to client
								 if (sendto(sock, &serverMsg, sizeof(serverMsg), 0,
									(struct sockaddr *) &echoClntAddr, sizeof(echoClntAddr)) < 0)
									DieWithError("sendto() sent a different number of bytes than expected");
							}
						}
					}
					//save the client address to send news feed later	
                }else{//if there is no Id found
                    serverMsg.response_Type = Nok;
                    printf("unsuccessful login attempt; claimed userID: %d\n", clientMsg.UserID);
                }
                break;
            case Logout:
                printf("**********\nUser Logout \t userID: %d\n**********\n", clientMsg.UserID);
                serverMsg.response_Type = Ok;
				//clear the user address to send newsfeed
                break;
            case Post:
				/*get the message and save it*/
				 if (postMessage(clientMsg.UserID,clientMsg.message)){
						printf("user: %d message posted to file.\n", clientMsg.UserID);
						serverMsg.response_Type = Ok;
						/*send this message to other user following
						check if */
						int i;
						int j;
						/*for (i=0;i<20;i++){
							for (j=0;j<20;i++){
								printf("line168\n");
								//if find as leader then send to user
								if(arrOfLeaderIds[i].LeaderID[j]==clientMsg.UserID){
									strcpy(serverMsg.message,clientMsg.message);
									serverMsg.LeaderID=clientMsg.UserID;
									serverMsg.response_Type=Ok;
									//send to client
									if (sendto(sock, &serverMsg, sizeof(serverMsg), 0,
										(struct sockaddr *) &clntAddr, sizeof(clntAddr)) < 0)
										DieWithError("sendto() sent a different number of bytes than expected");
									
								}
							}
						}*/
					}
				else
					serverMsg.response_Type=Nok;
			break;
			case Follow:
                if (validateUserId(clientMsg.LeaderID)) {
                    /*add to the leader list*/
					followLeader(clientMsg.UserID,clientMsg.LeaderID,arrOfLeaderIds);
					serverMsg.response_Type = Ok;
					
					/*Get the news feed and send to user*/
					int j;
					char *str[100]; 
					int lid= clientMsg.LeaderID;
					getPosts(lid,str);	//get the posts from file
					int n=checkNoOfPosts(lid);
						for(j=0;j<n;j++){
							strcpy(serverMsg.message,str[j]);	//assign to server message
							serverMsg.response_Type=Ok;
							serverMsg.LeaderID=lid;
							//send to client
							 if (sendto(sock, &serverMsg, sizeof(serverMsg), 0,
								(struct sockaddr *) &echoClntAddr, sizeof(echoClntAddr)) < 0)
								DieWithError("sendto() sent a different number of bytes than expected");
						}
                }
                else{//if leader ID is not correct
                    serverMsg.response_Type = Nok;
                }
			break;
			case UnFollow:
			    if (validateUserId(clientMsg.LeaderID)) {
					/*remove from the leader list*/
					unFollowLeader(clientMsg.UserID,clientMsg.LeaderID,arrOfLeaderIds);
					serverMsg.response_Type = Ok;
                }
                else{//if leader ID is not correct
                    serverMsg.response_Type = Nok;
                }
			break;
            default:
                printf("request type is Unknown. \n");
        }//end of switch

        /* Send datagram back to the client */
        if (sendto(sock, &serverMsg, sizeof(serverMsg), 0,
                   (struct sockaddr *) &echoClntAddr, sizeof(echoClntAddr)) < 0)
            DieWithError("sendto() sent a different number of bytes than expected");

    }//end of infinite for loop

}//end main

/*Method to validate userID*/
int validateUserId(int inputUserId){
   FILE *fp = fopen("userIds.txt", "r");
    int bufferSize = 255;
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));

    while (fgets(buffer, bufferSize, (FILE*)fp) != NULL) {
        int i = 0;
        int LenUserId =0;
        int currentUserId = 0;
        while (buffer[i] != 0) {
            if (buffer[i] == '-') {
                LenUserId = i;
            }
            i++;
        }
        //checking the userID
		int index;
        for (index = LenUserId - 1; index >= 0; index--) {
            int numChar = (int)(buffer[index] - '0');
            int multiplier = (int)pow(10, (LenUserId - 1) - index);
            currentUserId += numChar * multiplier;
        }
        if (currentUserId == inputUserId ) {
            fclose(fp);
            printf("Validating user %d \t", inputUserId);
            return 1; //user match from list
        }
    }
    fclose(fp);
    return 0;//did not match
}//end of user authentication
int postMessage(int inputUserId,char *strng){
		FILE *fp;
		fp = fopen("testfile.txt", "a"); 
		if(fp!=NULL){
			fprintf(fp, "%d-%s",inputUserId,strng);
			fprintf(fp,"\r\n");	//each message at new line
			fclose(fp);
			return 1;
		}
			fclose(fp);
			return 0;
}
/*this method will insert all the existing user ID's into
 struct Array so that we can add leaders against each of 
 those later when follow is called*/
void filluserIdinArr(LeaderIds *arr){
	//read user from user file
	FILE *fp = fopen("userIds.txt", "r");
    int bufferSize = 255;
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));
	int j = 0;
	int k = 0;
    while (fgets(buffer, bufferSize, (FILE*)fp) != NULL) {
        int i = 0;
		
        int LenUserId =0;
        int currentUserId = 0;
        while (buffer[i] != 0) {
            if (buffer[i] == '-') {
                LenUserId = i;
            }
            i++;
        }
        //checking the userID
		int index;
        for (index = LenUserId - 1; index >= 0; index--) {
            int numChar = (int)(buffer[index] - '0');
            int multiplier = (int)pow(10, (LenUserId - 1) - index);
            currentUserId += numChar * multiplier;
        }
			arr[j].UserID=currentUserId;
			for(k=0;k<20;k++){	//initial leaders values are zero
					arr[j].LeaderID[k]=0;
				}
			j=j+1;
	}

}
int followLeader(int inputUserId,int leaderId,LeaderIds *leaderArr){
	//find the struct with userID=id
	int len= sizeof(leaderArr[0].LeaderID)/sizeof(leaderArr[0].LeaderID[0]);
	int i;
	int j;
	for(i=0;i<len;i++){
		if(leaderArr[i].UserID==inputUserId){
			for(j=0;j<len;j++){
				if(leaderArr[i].LeaderID[j]==0){
					leaderArr[i].LeaderID[j]=leaderId;
					printf("\nleader Added:%d for user:%d\n",leaderId,inputUserId);
					break;
					}
			}
		}
		
	}	
}
int unFollowLeader(int inputUserId,int leaderId,LeaderIds *leaderArr){
	int len= sizeof(leaderArr[0].LeaderID)/sizeof(leaderArr[0].LeaderID[0]);
	int i;
	int j;
	for(i=0;i<len;i++){
		if(leaderArr[i].UserID==inputUserId){
			for(j=0;j<len;j++){
				if(leaderArr[i].LeaderID[j]==leaderId){
					leaderArr[i].LeaderID[j]=0;
					printf("\nleader removed:%d for user:%d\n",leaderId,inputUserId);
					break;
					}
			}
		}
		
	}		 
}
void findleaderId(int inputUserId,int *leaderId,LeaderIds *leaderArr){	
	int len= sizeof(leaderArr[0].LeaderID)/sizeof(leaderArr[0].LeaderID[0]);
	int i;
	int j;
	int k=0;
	for(i=0;i<len;i++){
		if(leaderArr[i].UserID==inputUserId){
			for(j=0;j<len;j++){
				if(leaderArr[i].LeaderID[j]!=0){
					leaderId[k]=leaderArr[i].LeaderID[j];
					k++;
				}else
					leaderId[j]=0;
			}
		}
		
	}	
}
int checkNoOfPosts(int inputUserId){	
	int count=0;
	FILE *fp = fopen("testfile.txt", "r");
    int bufferSize = 255;
    char buffer[bufferSize];
    memset(buffer, 0, sizeof(buffer));
    while (fgets(buffer, bufferSize, (FILE*)fp) != NULL) {
        int i = 0;
        int LenUserId =0;
        int currentUserId = 0;
        while (buffer[i] != 0) {
            if (buffer[i] == '-') {
                LenUserId = i;
            }
            i++;
        }
        //checking the userID
		int index;
        for (index = LenUserId - 1; index >= 0; index--) {
            int numChar = (int)(buffer[index] - '0');
            int multiplier = (int)pow(10, (LenUserId - 1) - index);
            currentUserId += numChar * multiplier;
        }
        if (currentUserId == inputUserId ) {
            
			printf("Validating user %d \t", inputUserId);
			count=count+1; //user match from list
        }
    }
    fclose(fp);
    return count;
}
