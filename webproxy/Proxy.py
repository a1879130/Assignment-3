# Include the libraries for socket and system calls
import socket
import sys
import os
import argparse
import re

# Library to extract and calculate age
import email.utils as eut
import datetime
from pytz import timezone

"""
Code adapted from the following lecture:

Author: Associate Professor Damith Ranasinghe
Title: 4 Application Layer Socket Programming Python
Date: 6 Mar 2019
Course: COMP SCI 7039
Institution: The University of Adelaide


"""

# helper function 1
# return current time based on the timeZone given
def getCurrentTime(timeZone):
  return datetime.datetime.now(timeZone).replace(tzinfo=None)

# helper function 2
# parse date to have the same format as the date returned from getCurrentTime()
def parseDate(date):
  return datetime.datetime(*eut.parsedate(date)[:6])

# 1MB buffer size
BUFFER_SIZE = 1000000

parser = argparse.ArgumentParser()
parser.add_argument('hostname', help='the IP Address Of Proxy Server')
parser.add_argument('port', help='the port number of the proxy server')
args = parser.parse_args()

# Create a server socket, bind it to a port and start listening
# The server IP is in args.hostname and the port is in args.port
# bind() accepts an integer only
# You can use int(string) to convert a string to an integer
# ~~~~ INSERT CODE ~~~~

# ~~~~ END CODE INSERT ~~~~

try:
  # Create a server socket
  # ~~~~ INSERT CODE ~~~~
  serverSocket = socket.socket(socket.AF_INET,socket.SOCK_STREAM) 
  
  # ~~~~ END CODE INSERT ~~~~
  print 'Connected socket'
except:
  print 'Failed to create socket'
  sys.exit()

try:
  # Bind the server socket to a host and port
  # ~~~~ INSERT CODE ~~~~
  serverSocket.bind((args.hostname,int(args.port)))
  # ~~~~ END CODE INSERT ~~~~
  print 'Port is bound'
except:
  print('Port is in use')
  sys.exit()

try:
  # Listen on the server socket
  # ~~~~ INSERT CODE ~~~~
  serverSocket.listen(1) #server began to listen for incoming TCP requests
  # ~~~~ END CODE INSERT ~~~~
  print 'Listening to socket'
except:
  print 'Failed to listen'
  sys.exit()

while True:
  print '\n\nWaiting connection...'

  clientSocket = None
  try:
    # Accept connection from client and store in the clientSocket
    # ~~~~ INSERT CODE ~~~~
    (clientSocket,address) =  serverSocket.accept()
    # ~~~~ END CODE INSERT ~~~~
    print 'Received a connection from:', args.hostname
  except:
    print 'Failed to accept connection'
    sys.exit()

  clientRequest = 'METHOD URI VERSION'
  # Get request from client
  # and store it in clientRequest
  # ~~~~ INSERT CODE ~~~~
  clientRequest =  clientSocket.recv(1024)
  # ~~~~ END CODE INSERT ~~~~

  print 'Received request:'
  print '< ' + clientRequest

  # Extract the parts of the HTTP request line from the given message
  requestParts = clientRequest.split()
  method = requestParts[0]
  URI = requestParts[1]
  version = requestParts[2]
  
  print 'Method:\t\t' + method
  print 'URI:\t\t' + URI
  print 'Version:\t' + version
  print ''
  print ''

  # Remove http protocol from the URI
  URI = re.sub('^(/?)http(s?)://', '', URI, 1)

  # Remove parent directory changes - security
  URI = URI.replace('/..', '')

  # Split hostname from resource
  resourceParts = URI.split('/', 1)
  hostname = resourceParts[0]
  resource = '/'

  if len(resourceParts) == 2:
    # Resource is absolute URI with hostname and resource
    resource = resource + resourceParts[1]

  print 'Requested Resource:\t' + resource

  cachePath = './' + hostname + resource
  if cachePath.endswith('/'):
    cachePath = cachePath + 'default'

  print 'Cache location:\t\t' + cachePath

  fileExists = os.path.isfile(cachePath)
  
  try:
    # Check wether the file exist in the cache
    # read the cache
    cacheFile = open(cachePath, "r")
    cacheData = cacheFile.readlines()

    print 'Cache hit! Loading from cache file: ' + cachePath
    
    # ProxyServer finds a cache hit
    # handle cache directives in the header field
    # Check if the cache is suitable to re-use (Any "cache-control" header in the cache?)
    # If the cache can be re-use, send back contents of cached file
    # ~~~~ INSERT CODE ~~~~
    for i in cacheData:
        clientSocket.send(i)
    # ~~~~ END CODE INSERT ~~~~
    
    cacheFile.close()
    print 'cache file closed'

  # Error handling for file not found in cache and cache is not suitable to send
  except IOError:
    originServerSocket = None
    # Create a socket to connect to origin server
    # and store in originServerSocket
    # ~~~~ INSERT CODE ~~~~
    originServerSocket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    # ~~~~ END CODE INSERT ~~~~

    print 'Connecting to:\t\t' + hostname + '\n'
    try:
      # Get the IP address for a hostname (origin server)
      hostAddress = socket.gethostbyname(hostname)

      # Connect to the origin server
      # ~~~~ INSERT CODE ~~~~
      originServerSocket.connect((hostname,80))
      # ~~~~ END CODE INSERT ~~~~

      print 'Connected to origin Server'

      # Create a file object associated with this socket
      # This lets us use file function calls
      originServerFileObj = originServerSocket.makefile('+', 0)

      # Create origin server request line and headers to send
      # and store in originServerRequestHeader and originServerRequestLine
      originServerRequestLine = ''
      originServerRequestHeader = ''
     
      # originServerRequestLine is the first line in the request and
      # originServerRequestHeader is the second line in the request
      # ~~~~ INSERT CODE ~~~~
      originServerRequestLine = method + ' ' + resource + ' ' + version # done the request line
      originServerRequestHeader = "Host: " + hostname + '\r\n'

      # ~~~~ END CODE INSERT ~~~~

      # Construct the request to send to the origin server
      originServerRequest = originServerRequestLine + '\r\n' + originServerRequestHeader + '\r\n\r\n'

      # Request the web resource from origin server
      print 'Forwarding request to origin server:'
      for line in originServerRequest.split('\r\n'):
        print '> ' + line

      try:
        originServerSocket.sendall(originServerRequest)
      except socket.error:
        print 'Send failed'
        sys.exit()

      print 'Request sent to origin server\n'
      originServerFileObj.write(originServerRequest)

      # use to store response from the origin server
      data  = ''
      
      # Get the response from the origin server
      # ~~~~ INSERT CODE ~~~~
      data = originServerSocket.recv(1024)

      # ~~~~ END CODE INSERT ~~~~

      # use to determine if this response should be cached?
      isCache = True

      # Get the response code from the response
      dataLines = data.split('\r\n')
      responseCode = dataLines[0] 

      # Decide which content should be cached
      # ~~~~ INSERT CODE ~~~~

      # ~~~~ END CODE INSERT ~~~~
      
      # Send the data to the client
      # ~~~~ INSERT CODE ~~~~
      clientSocket.send(data)
      # ~~~~ END CODE INSERT ~~~~

      # cache the content if it should be cached
      if isCache:
        # Create a new file in the cache for the requested file.
        # Also send the response in the buffer to client socket
        # and the corresponding file in the cache
        cacheDir, file = os.path.split(cachePath)
        print 'cached directory ' + cacheDir
        if not os.path.exists(cacheDir):
          os.makedirs(cacheDir)
        cacheFile = open(cachePath, 'wb')

        # Save origin server response (data) in the cache file
        # ~~~~ INSERT CODE ~~~~
        for line in data:
            cacheFile.write(line)
        # ~~~~ END CODE INSERT ~~~~

        cacheFile.close()
        print 'cache file closed'

      # finished sending to origin server - shutdown socket writes
      originServerSocket.shutdown(socket.SHUT_WR)
      
      print 'origin server done sending'
      originServerSocket.close()
      
      clientSocket.shutdown(socket.SHUT_WR)
      print 'client socket shutdown for writing'
    except IOError, (value, message):
      print 'origin server request failed. ' + message
  try:
    clientSocket.close()
  except:
    print 'Failed to close client socket'