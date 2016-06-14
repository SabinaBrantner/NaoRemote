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
bool directoryExists(char* path); // Überprüft, ob der Ordner, indem die xar-Datei gespeichert ist, existiert
void startStartProgram(); // Ruft das Python Script auf, dass das behaviour startet

int bufferlen = 1000;
char* naoPath = "/home/nao/behaviors/naoremote/";
char* fileName = "/home/nao/behaviors/naoremote/behavior.xar";
const char* installPython = "python runbla.py naoremote";
const char* startPython = "python run.py naoremote";
int port = 9999;

int main(int argc, char *argv[]) {
    int sockfd, newsockfd, n, pid;
    socklen_t clilen;
    char buffer[256];
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
        
/*
        pid = fork();

        if (pid < 0) {
            perror("Error while fork");
            exit(1);
        }


        if (pid == 0) {
*/
            //Der Process ist ein Client-Process
            close(sockfd);
            if (handle(newsockfd)) {
                printf("Programm starten");
                startStartProgram();
            }
            exit(0);
/*
        } else {
            close(newsockfd);
        }
*/
    }
    if (newsockfd != 0) {
        close(newsockfd);
    }
    return 0;
}

void truncateFile() {
    FILE *fp;
    fp = fopen("behavior.xar", "w");
    fclose(fp);
}

bool directoryExists(char* path) {
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

bool handle(int sock) {
    int n;
    char buffer[bufferlen + 1];
    bool returnValue = false;

    bzero(buffer, bufferlen);
    n = read(sock, buffer, bufferlen-1);
    buffer[bufferlen] = '\0';

    if (n < 0) {
        perror("Error while reading from socket");
        exit(1);
    } else {
        bool suc = true;
        if (false)//(!directoryExists())
        {
            mkdir(naoPath, 0777);
        }

        truncateFile();
        while (n > 0) {
            printf("%s", buffer);
            suc = writeDataInFile(buffer, n);
            n = read(sock, buffer, bufferlen-1);
            buffer[n] = '\0';
        }

        if (n < 0 || suc == false) {
            truncateFile();
            perror("Error while reading from socket");
            exit(1);
        } else {
            printf("File wurde erstellt und wurde beschrieben.\n");
            returnValue = true;
        }
    }
    return returnValue;
}

bool writeDataInFile(char* buffer, int len) {
    FILE* fp;

    //fp = fopen("behavior.xar", "a");
    fp = fopen("behavior.xar", "a");
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
    system(installPython);
    printf("Behavior wurde installiert.");
    system(startPython);
}
