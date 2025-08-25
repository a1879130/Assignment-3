Java RMI Calculator Assignment 
Overview 
This project implements a distributed calculator system using Java RMI (Remote Method Invocation). The system consists of a server that maintains a shared stack and supports mathematical operations, and clients that can connect remotely to perform calculations.
Features 
Core RMI Operations 

pushValue(int val) - Push integer values onto the stack 
pushOperation(String operator) - Perform operations on all stack values 

min - Find minimum value 
max - Find maximum value 
gcd - Calculate greatest common divisor 
lcm - Calculate least common multiple 


pop() - Pop and return top stack value 
isEmpty() - Check if stack is empty 
delayPop(int millis) - Pop with specified delay 

Additional Features 

Thread-safe operations for multiple concurrent clients 
Comprehensive error handling 
Automated testing suite 
Interactive client mode 
Multiple client testing 

File Structure
├── Calculator.java                 
├── CalculatorImplementation.java   
├── CalculatorServer.java          
├── CalculatorClient.java          
├── TestMultipleClients.java       
├── test_system.sh                 
└── README.md                      
Prerequisites 

Java JDK 8 or higher 
Linux/Unix environment (for shell scripts) 
Network access on localhost port 1099 

Compilation Instructions 
Compile all Java files 
bashjavac *.java
This will compile all the necessary Java source files including the interface, implementation, server, client, and test classes.
Running Instructions 
Step 1: Start RMI Registry 
bashrmiregistry 1099 &
Step 2: Start Calculator Server 
bashjava CalculatorServer
The server will output:
Starting Calculator RMI Server... 
Calculator RMI Server is ready and waiting for client connections.
Step 3: Run Clients 
Option A: Automated Testing 
bashjava CalculatorClient test
Option B: Interactive Mode 
bashjava CalculatorClient interactive
Option C: Multiple Client Testing 
bashjava TestMultipleClients
Automated Testing 
Run Complete Test Suite 
bashchmod +x test_system.sh
./test_system.sh
This script will:


Compile all Java files 
Start RMI registry and server 
Run single client tests 
Run multiple client tests 
Run concurrent operation tests
Clean up all processes 

Manual Testing Commands 
Single Client Test 
bashjava CalculatorClient test SingleClient
Multiple Concurrent Clients 
bash# Terminal 1 
java CalculatorClient test Client1 &

# Terminal 2 
java CalculatorClient test Client2 &

# Terminal 3 
java CalculatorClient test Client3 &
Interactive Mode Commands 
When running in interactive mode, use these commands:


push <number> - Push a number onto stack 
pop - Pop top value from stack 
operation <op> - Apply operation (min/max/gcd/lcm) 
delaypop <milliseconds> - Pop with delay 
empty - Check if stack is empty 
test - Run automated tests 
help - Show available commands 
quit or exit - Exit client 

Example Interactive Session 
[Client-123] Enter command: push 10
Pushed 10 

[Client-123] Enter command: push 15
Pushed 15 

[Client-123] Enter command: push 20
Pushed 20

[Client-123] Enter command: operation min
Applied operation: min 

[Client-123] Enter command: pop
Popped: 10 

[Client-123] Enter command: empty
Stack is empty: false 
Testing Scenarios 
1. Single Client Operations 

Basic push/pop operations 
Mathematical operations (min, max, gcd, lcm) 
isEmpty functionality 
delayPop with timing verification 
Error handling for empty stack 

2. Multiple Client Operations 

Concurrent push operations 
Shared stack verification 
Thread safety testing 
Concurrent mathematical operations 
Concurrent pop and delayPop operations 

Architecture Details 
Server Architecture 

CalculatorServer.java: Bootstrap class that starts RMI registry and binds the service 
CalculatorImplementation.java: Thread-safe implementation of all calculator operations 
Shared Stack: Single stack shared by all clients 

Client Architecture 

CalculatorClient.java: Main client with both interactive and automated test modes / 具有交互式和自动化测试模式的主客户端
TestMultipleClients.java: Specialized multi-client testing framework / 专门的多客户端测试框架

Thread Safety 
All server operations are synchronized to ensure thread safety when multiple clients access the shared stack simultaneously.
Mathematical Operations Details 
GCD (Greatest Common Divisor) 
Uses Euclidean algorithm for efficient calculation.
LCM (Least Common Multiple) 
Calculated using the formula: LCM(a,b) = (a * b) / GCD(a,b)
MIN/MAX Operations 
Uses Java Streams for efficient processing of all stack values.
Error Handling 
The system handles various error conditions:

Empty Stack Operations: Throws RemoteException with descriptive message 
Invalid Operations: Validates operation strings 
Network Errors: Proper RMI exception handling 
Interrupted Operations: Handles thread interruption in delayPop 

Troubleshooting 
Common Issues 

Registry Connection Error 
Solution: Ensure rmiregistry is running on port 1099


Compilation Errors 
Solution: Ensure Java JDK is properly installed and in PATH

Port Already in Use 
bash# Kill existing processes 
pkill -f "rmiregistry\|CalculatorServer"

Permission Denied on Scripts 
bashchmod +x test_system.sh


Performance Considerations 

The server uses synchronized methods to ensure thread safety, which may impact performance under high load
Mathematical operations are optimized using efficient algorithms 
delayPop operations use Thread.sleep() which is blocking 
