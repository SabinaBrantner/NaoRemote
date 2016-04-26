#include <stdio.h>
#include <stdlib.h>

#include <netdb.h>
#include <netinet/in.h>

#include <string.h>

void handle (int sock) {
   int n;
   char buffer[256];
   bzero(buffer,256);
   n = read(sock,buffer,255);
   
   if (n < 0) {
      perror("Error while reading from socket");
      exit(1);
   }
   printf("Here is the message: %s\n",buffer);
   n = write(sock,"I got your message",18);
   
   if (n < 0) {
      perror("Error while writing to socket");
      exit(1);
   }
	
}

int main( int argc, char *argv[] ) {
   int sockfd, newsockfd, port ,clilen;
   char buffer[256];
   struct sockaddr_in serv_addr, cli_addr;
   int  n, pid;
   
   
   sockfd = socket(AF_INET, SOCK_STREAM, 0);
   
   if (sockfd < 0) {
      perror("Cannot open the socket");
      exit(1);
   }
   
   //Socket Struktur initialisieren
   bzero((char *) &serv_addr, sizeof(serv_addr));
   port = 8000;
   
   serv_addr.sin_family = AF_INET;
   serv_addr.sin_addr.s_addr = INADDR_ANY;
   serv_addr.sin_port = htons(port);
   
   //Die Adresse des Hosts an den Server binden
   if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
      perror("There was an error while binding");
      exit(1);
   }
      
   //Es wird gewartet bis ein Client connected
   listen(sockfd,5);
   clilen = sizeof(cli_addr);
   
   while (1) {
      newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
		
      if (newsockfd < 0) {
         perror("Error while accepting.");
         exit(1);
      }
      
      //Neuen process mit jedem Client erstellen
      pid = fork();
		
      if (pid < 0) {
         perror("Error while fork");
         exit(1);
      }
      
      
      if (pid == 0) {
          //Der Process ist ein Client-Process
         close(sockfd);
         handle(newsockfd);
         exit(0);
      }
      else {
         close(newsockfd);
      }
		
   }
   
   return 1;
}