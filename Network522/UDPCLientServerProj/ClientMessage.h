
//UDP Client struct client Message
//By: Mahmood Zaman

#ifndef ClientMessage_h
#define ClientMessage_h

#include <stdio.h>

typedef struct {

		enum {Login, Post, Follow, UnFollow, Logout }request_Type;   
													
		unsigned int UserID;                        /* unique client identifier */
		unsigned int LeaderID;                        /* unique leader identifier */
		char message[100];                          /* text message*/

	} ClientMessage;                             
	
#endif 												