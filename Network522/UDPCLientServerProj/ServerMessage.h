

#ifndef ServerMessage_h	
#define ServerMessage_h

#include <stdio.h>

typedef struct {
		enum {Ok,Nok} response_Type;
		unsigned int LeaderID;                          // leader identifier 
		char message[100];                             // text message
	} ServerMessage;                                 

#endif 					// ServerMessage_h 