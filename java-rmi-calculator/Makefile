JAVAC=javac
JAVA=java
RMIREGISTRY=rmiregistry

# Source files
SOURCES=Calculate.java CalculatorImplementation.java CalculatorServer.java CalculatorClient.java TestMultipleClients.java

# Class files
CLASSES=$(SOURCES:.java=.class)

.PHONY: all clean run-server run-client test

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(SOURCES)

clean:
	rm -f *.class

run-server:
	$(RMIREGISTRY) & \
	$(JAVA) CalculatorServer

run-client:
	$(JAVA) CalculatorClient

test:
	./test_system.sh

# Rule to make the files executable
permissions:
	chmod +x *.sh
