#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <dirent.h>
#include <stdbool.h>
#include <wchar.h>
/*
 *P
 * Programm erkennt nicht das wirkliche Ende eines Files oder Übertragunsproblem
 * Programm encodiert wahrscheinlich falsch
 * In java wird UTF8 codiert das file eingelesen 
 * das Java Programm schickt über einen outputStreamWriter die Daten weg
 * UTF 8 codierung ist beim outputStreamWriter angegeben
 */

bool writeDataInFile(char* buffer, int len); // Schreibt empfangene Daten in eine xar-Datei
bool handle(int sock); // Behandelt die Socket-Verbindung (Empfangen der Daten, Schreiben)
void truncateFile(); // Erstellt oder Löscht den Inhalt eines der xar-Datei
bool directoryExists(); // Überprüft, ob der Ordner, indem die xar-Datei gespeichert ist, existiert
void startStartProgram(); // Ruft das Python Script auf, dass das behaviour startet
bool action(int sock);
bool speak(int sock);
bool runMoving(int sock);

int bufferlen = 1000;
char* naoPath = "/home/nao/behaviors/naoremote/";
char* fileName = "/home/nao/behaviors/naoremote/behavior.xar";
const char* installStartPython = "python installAndStart.py";
const char* getBattery = "python getBattery.py";
const char* getName = "python getData.py";
const char* getTemp = "python getTemperature.py";
int port = 9999;

int main(int argc, char *argv[]) {
    int sockfd, newsockfd, n, pid;
    socklen_t clilen;
    char buffer[bufferlen];
    struct sockaddr_in serv_addr, cli_addr;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0) {
        perror("Cannot open the socket");
        exit(1);
    }

    //Socket Struktur initialisieren
    bzero((char *) &serv_addr, sizeof (serv_addr));

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(port);

    //Die Adresse des Hosts an den Server binden
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof (serv_addr)) < 0) {
        perror("There was an error while binding");
        exit(1);
    }

    //Es wird gewartet bis ein Client connected
    listen(sockfd, 5);
    clilen = sizeof (cli_addr);

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
            printf("Verbindung mit Client hergestellt\n");
            //Der Process ist ein Client-Process
            close(sockfd);
            while(handle(newsockfd)){

            }
            exit(0);
        } else {
            close(newsockfd);
        }
    }
    if (newsockfd != 0) {
        close(newsockfd);
    }
    return 0;
}

void truncateFile() {
    FILE *fp;
    fp = fopen(fileName, "w");
    fclose(fp);
}

bool directoryExists() {
    if (naoPath == NULL)
        return false;

    DIR *dir;
    bool exist = false;

    dir = opendir(naoPath);

    if (dir != NULL) {
        exist = true;
        closedir(dir);
    }

    return exist;
}

bool runExternalProgram(int sock, const char *progname){
	char processbuffer[200+1];
	FILE *process;
	char *substr;
	int count=0;
	int n=0;
	bool success=false;
	bool returnValue=false;

	process=popen(progname,"r");
	do
	{
		count=fread(processbuffer,sizeof(char),200,process);
		if (count!=0)
			{
				processbuffer[count]='\0';
				substr=strtok(processbuffer,"\n");
				if (strstr(substr,"[INFO ]")==NULL)
				{	
					success=true;
				}
				else
				{
					while ((substr!=NULL)&&(!success))
					{
						substr=strtok(NULL,"\n");
						if (strstr(substr,"[INFO ]")==NULL)
						{
							success = true;
						}
					}
				}
			}
	} while ((count!=0)&&(!success));
	pclose(process);
	if (success)
	{
		printf("Ergebnis: %s\n\n", substr);
		n = write(sock, substr, strlen(substr));
		returnValue = true;
	}
	else {
		n = write(sock, "ERROR", count);returnValue=false;
		returnValue = false;
	}
	return returnValue;
}

bool handle(int sock) {
    int n;
    int toDoLen = 7;
    char buffer[toDoLen + 1];
    bool returnValue = false;
    FILE *process;
    char processbuffer[200 + 1];
    int count=0;
    bool success=false;
    char *substr;
    
    bzero(buffer, toDoLen);
    n = read(sock, buffer, toDoLen-1);
    if(n > 0){
        buffer[n] = '\0';
        printf("%s\n", buffer);
        if(strstr(buffer, "SET Ac") != NULL){
            returnValue = action(sock);
        }
        if(strstr(buffer, "SET Sp") != NULL){
            returnValue = speak(sock);
        }
        if(strstr(buffer, "GET Ba") != NULL){
            returnValue=runExternalProgram(sock,getBattery);
        }
        if(strstr(buffer, "GET Na") != NULL){
            returnValue=runExternalProgram(sock,getName);
        }
        if (strstr(buffer, "GET Te") != NULL)
        {
                returnValue=runExternalProgram(sock,getTemp);
        }
        if (strstr(buffer, "SET Ko") != NULL)
        {
                returnValue=runMoving(sock);
        }
		
		
        fflush(sock);
    }
    if(n < 0){
        perror("Error while reading from socket");
        returnValue = false;
    }
    return returnValue;
}

bool runMoving(int sock){
	bool returnValue = false;
	char buffer[bufferlen + 1];
	int n;
	
	bzero(buffer, bufferlen);
	n = read(sock, buffer, bufferlen-1);
	buffer[bufferlen] = '\0';
	
	if (n < 0)
	{
		perror("Error while reading from socket");
	}
	else
	{
		char* x = strtok(buffer, ";");
                char* y = strtok(NULL, ";");
		printf("X:%s\n", x);
		printf("Y:%s\n", y);
		returnValue = true;
		
		char output[100];
		sprintf(output, "python moveAndWalk.py %s %s", x, y);
		system(output);
	}
	
	
	return returnValue;
}

bool speak(int sock){
	bool returnValue = false;
	char buffer[bufferlen + 1];
	int n;
	
	bzero(buffer, bufferlen);
	n = read(sock, buffer, bufferlen-1);
	buffer[bufferlen] = '\0';
	
	if (n < 0)
	{
		perror("Error while reading from socket");
		returnValue = false;
	}
	else
	{
		char output[230];
		sprintf(output, "python speakingNao.py \"%s\"", buffer);
		system(output);
		returnValue = true;
	}
	
	
    return returnValue;
}

bool action(int sock){
    int n;
    char buffer[bufferlen + 1];
    bool returnValue = false;

    bzero(buffer, bufferlen);
    n = read(sock, buffer, bufferlen-1);
    buffer[bufferlen] = '\0';

    if (n < 0) {
        perror("Error while reading from socket");
        exit(1);
    } 
    else {
		system("python waitTillStopped.py");
        bool suc = true;
        if (!directoryExists())
        {
            mkdir(naoPath, 0777);
        }

        truncateFile();
        bool nextTimeEnd = false;
        bool end = false;
        while (n > 0 && end == false) {
            suc = writeDataInFile(buffer, n);
            if(nextTimeEnd){
                end = true;
            }
            else
                n = read(sock, buffer, bufferlen-1);
            if(strstr(buffer, "</ChoregrapheProject>") != NULL){
                nextTimeEnd = true;
            }
            buffer[n] = '\0';
        }

        if (n < 0 || suc == false) {
            truncateFile();
            perror("Error while reading from socket");
            returnValue = false;
        } 
        else {
            printf("File wurde erstellt und wurde beschrieben.\n");
            startStartProgram();
            returnValue = true;
        }
    }
    return returnValue;
}

bool writeDataInFile(char* buffer, int len) {
    FILE* fp;

    fp = fopen(fileName, "a");
    if (fp == NULL) {
        printf("Datei konnte nicht geoeffnet werden.\n");
        return false;
    } else {
        int suc = 0;
        int i;
        suc = fprintf(fp, "%s", buffer);
        if (suc < 0) {
            printf("Error while writing in File");
            return false;
        }
        fflush(fp);
        fclose(fp);
    }
    return true;
}



void startStartProgram() {
	printf("Behavior wird installiert und gestartet.");
    system(installStartPython);
}
