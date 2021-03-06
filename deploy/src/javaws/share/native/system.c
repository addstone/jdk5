/*
 * @(#)system.c	1.17 04/04/16
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/* Implementation of shared system code. This code is the same
 * for all platforms.
 */
#include "system.h"

/* 
 * Create a loopback socket and set it up to listen on a dynamically
 * allocated "ephemeral" port.  Return the port and a new socket or 
 * INVALID_SOCKET if there's an error.  
 */
SOCKET sysCreateListenerSocket(int *port)
{
    SOCKET server;
    SOCKADDR_IN iname = {0};
    int length = sizeof(iname);

    sysInitSocketLibrary();

    iname.sin_family = AF_INET;
    iname.sin_addr.s_addr = inet_addr("127.0.0.1");

    if ((server = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
	return INVALID_SOCKET;
    }
    if (bind(server, (SOCKADDR *)&iname, sizeof(iname)) == SOCKET_ERROR) {
	sysCloseSocket(server);
	return INVALID_SOCKET;
    }
    if (getsockname(server, (SOCKADDR *)&iname, &length) == SOCKET_ERROR) {
	sysCloseSocket(server);
	return INVALID_SOCKET;
    }
    if (listen(server, SOMAXCONN) == SOCKET_ERROR) {
	sysCloseSocket(server);
	return INVALID_SOCKET;
    }

    *port = ntohs(iname.sin_port);
    return server;
}


/* 
 * Create a loopback socket and set it up to listen on the specified 
 * port.  Return the new socket or INVALID_SOCKET if there's an error.
 */
SOCKET sysCreateServerSocket(int port)
{
    SOCKET server;
    SOCKADDR_IN iname = {0};
    int length = sizeof(iname);

    sysInitSocketLibrary();

    iname.sin_family = AF_INET;
    iname.sin_port = htons((u_short)port);
    iname.sin_addr.s_addr = inet_addr("127.0.0.1");

    if ((server = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
	return INVALID_SOCKET;
    }
    if (bind(server, (SOCKADDR *)&iname, sizeof(iname)) == SOCKET_ERROR) {
	sysCloseSocket(server);
	return INVALID_SOCKET;
    }
    if (listen(server, SOMAXCONN) == SOCKET_ERROR) {
	sysCloseSocket(server);
	return INVALID_SOCKET;
    }

    return server;
}


/* 
 * Create a loopback socket connected to the specified port.  Return the 
 * new socket or INVALID_SOCKET if there's an error.
 */
SOCKET sysCreateClientSocket(int port) 
{
    SOCKET client;
    SOCKADDR_IN iname = {0};

    sysInitSocketLibrary();

    iname.sin_family = AF_INET;
    iname.sin_port = htons((u_short)port);
    iname.sin_addr.s_addr = inet_addr("127.0.0.1");

    if ((client = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
	return INVALID_SOCKET;
    }
    if (connect(client, (SOCKADDR *)&iname, sizeof(iname)) == SOCKET_ERROR) {
	sysCloseSocket(client);
	return INVALID_SOCKET;
    }

    return client;
}
    

/* 
 * Tests creatability of a loopback socket for the specified port.
 * If the test fails, INVALID_SOCKET is returned, otherwise the
 * would-be socket is returned.    For the special case *port == 0,
 * the port will be reset to an ephemeral port (chosen by the kernel).
 */
SOCKET sysTestServerSocketCreatable(int* port)
{
    SOCKET sock;
    SOCKADDR_IN iname = {0};
    int length = sizeof(iname);

    sysInitSocketLibrary();

    iname.sin_family = AF_INET;
    iname.sin_port = htons((u_short)*port);
    iname.sin_addr.s_addr = inet_addr("127.0.0.1");

    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
	return INVALID_SOCKET;
    }
/* When bind() is called and *port==0, kernel will choose ephemeral port */
    if (bind(sock, (SOCKADDR *)&iname, sizeof(iname)) == SOCKET_ERROR) {
	sysCloseSocket(sock);
	return INVALID_SOCKET;
    }
/* When getsockname() is called, iname is updated to reflect port used
   by kernel */
    if (getsockname(sock, (SOCKADDR *)&iname, &length) == SOCKET_ERROR) {
	sysCloseSocket(sock);
	return INVALID_SOCKET;
    }
/* At this juncture, we have a valid socket and a valid (possibly
   ephemeral) port  */
    *port = ntohs(iname.sin_port);
    sysCloseSocket(sock);

    return sock;
}


/* 
 * Write a short string to the socket, adding a "\r\n" at the end.  If
 * an error occurs, 0 is returned.
 */
int sysWriteSocket(SOCKET s, char *str) 
{
    char* buffer;
    int result = 0;
    if (str == NULL) return result;
    /* allocate length of str + 3, for str itself, \r\n and \0 by sprintf */
    buffer = (char*)malloc(sizeof(char) * (strlen(str) + 3));
    if (buffer == NULL) return result;
    sprintf(buffer, "%s\r\n", str);
    result =  send(s, buffer, strlen(buffer), 0) != SOCKET_ERROR ;
    free(buffer);
    return result;
}


/* 
 * Read a short newline terminated string from the socket or NULL if
 * an error occurs.  The newline is replaced with a null character.  
 * The returned value is a pointer to a single static buffer so callers 
 * should copy the returned string if they need to hold on to it.
 */
char *sysReadSocket(SOCKET s)
{
  static char buffer[1024];  
  int count = 0;
  int r;

  do {
    r =  recv(s, &buffer[count], 1, 0);    
    if (r == SOCKET_ERROR) return NULL;
    if (r == 0 || buffer[count] == '\n') {
      buffer[count] = '\0';
      return buffer;
    }
    if (buffer[count] != '\r') count++;
  } 
  while(count < sizeof(buffer) - 1);

  /* Buffer overflow. Just return the part we read */
  buffer[sizeof(buffer)-1] = '\0';
  return buffer;
}



/* 
 * If the string contains spaces and is not quoted already,
 * then add double quotes at both ends and escape any internal
 * double quotes.  Here are some examples of the way this 
 * function transforms a string:
 * 
 * one two => "one two"
 * one "two" three => "one \"two\" three"
 * noEmbeddedSpaces => noEmbeddedSpaces
 * "already quoted" => "already quoted"
 *
 * A copy of the string is always returned unless s is NULL,
 * then it's just returned as is.
 */
char *sysQuoteString(char *s) 
{
    int length0;

    if (!s) {
	return s;
    }

    length0 = strlen(s);

    if (strpbrk(s, " \t") == NULL) {
	return strdup(s);
    }
    else if ((length0 > 1) && (s[0] == '"') && (s[length0 - 1] == '"')) {
	return strdup(s);
    }
    else {
	int i, j = 0, length1 = length0 + 3; 
	char *ss;
	/* count the number of embedded double quote chars */
       	for(i = 0; i < length0; i++) {
	    if (s[i] == '"') {
		length1 += 1;
	    }
	}
	/* copy s, replacing each '"' with '\"' */
	ss = (char *)malloc(length1);
	ss[j++] = '"';
	for(i = 0; i < length0; i++) {
	    if (s[i] == '"') {
		ss[j++] = '\\';
	    }
	    ss[j++] = s[i];
	}
	ss[j++] = '"';
	ss[j] = '\0';
	return ss;
    }
}

char* sysGetJarLib(void) {
  static char path[MAXPATHLEN];
  static int  initialized = FALSE;

  if (!initialized) {  
    char *appHome = sysGetApplicationHome();  
    strncpy(path, appHome, strlen(appHome) - 3);
    strcat(path, "lib");
    initialized = TRUE;
  }

  return path;
}

char* sysGetJavawsResourcesLib(void) {
  static char path[MAXPATHLEN];
  static int  initialized = FALSE;

  if (!initialized) {  
    char *jarlib = sysGetJarLib();
    sprintf(path, "%s%c%s", jarlib, FILE_SEPARATOR, "javaws");
    initialized = TRUE;
  }
  return path;
}

char* sysGetSecurityLib(void) {
  static char path[MAXPATHLEN];
  static int  initialized = FALSE;

  if (!initialized) {  
    char *jarlib = sysGetJarLib();
    sprintf(path, "%s%c%s", jarlib, FILE_SEPARATOR, "security");
    initialized = TRUE;
  }
  return path;
}

 /* remove temp file on exit */
void sysExit(int copiedfile, int copiedFileIndex, char **argv) {
  if (copiedfile && argv[copiedFileIndex] != NULL) {
    remove(argv[copiedFileIndex]);
  }
  exit(0);
}

char * sysSaveConvert(unsigned short *theString) {
 
  char* output = (char*)malloc(sizeof(char)*MAXPATHLEN);
  int i = 0;
 
  while (*theString != 0) {

    if (*theString == 0xFEFF || *theString == 0xFFFE) {
      /* skip it */
  
    } else if (*theString < 0x007F) {
    
      sprintf(&output[i], "%c", *theString);
    
      i++;
    } else {
      /* write out unicode in java properties file escaped sequence */
      sprintf(&output[i], "%c", '\\');
      i++;
      sprintf(&output[i], "%c", 'u');
      i++;
      sprintf(&output[i], "%x", (*theString >> 12) & 0xF );
      i++;
      sprintf(&output[i], "%x", (*theString >> 8) & 0xF );
      i++;
      sprintf(&output[i], "%x", (*theString >> 4) & 0xF);
      i++;
      sprintf(&output[i], "%x", (*theString) & 0xF);
      i++;
    }
    
    *theString++;
  }
  
  return output;
}

void sysReplaceChar(char* orig, char oldChar, char newChar) {

  while (*orig != 0) {
    if (*orig == oldChar) {
      *orig = newChar;
    }
    *orig++;
  }
}

int sysFindSiPort(char *canonicalHome) {
  char siFilename[MAXPATHLEN];
  char *portS;
  int port = -1;
  int ret;

  ret = sysFindSiFile(canonicalHome, siFilename);
  if (ret == 1) {
    portS = strrchr(siFilename, '_');
    portS++;
    port = atoi((const char*)portS);
  }

  return port;
}
/* path must be an absolute path, and must not have FILE_SEPARATOR at end */
void recursive_create_directory(char *path) {
  char *p, *dir;
  struct stat stat_buf;

  /* if path already exists then return */
  if (stat(path, &stat_buf)==0) return;

  /* find previous file separator char from end of string */
  p = strrchr(path, FILE_SEPARATOR);
  if (p == NULL) return;

  /* store before chopping */
  dir = strdup(path);

  /* chop string at file separator char */
  if (p > path && *(p-1) != ':') *p = 0;
  else *(p+1) = 0;

  /* recurse */
  recursive_create_directory(path);

  sysCreateDirectory(dir);

  free(dir);
}
