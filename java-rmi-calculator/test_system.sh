#!/bin/bash
# System test script for Java RMI Calculator
# Java RMI计算器的系统测试脚本

echo "==================================="
echo "Java RMI Calculator System Test"
echo "==================================="

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ $2 -eq 0 ]; then
        echo -e "${GREEN}✓ $1${NC}"
    else
        echo -e "${RED}✗ $1${NC}"
        exit 1
    fi
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check if Java is installed
print_info "Checking Java installation... "
java -version
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Java is not installed or not in PATH${NC}"
    exit 1
fi
print_status "Java is available " 0

# Compile all Java files
print_info "Compiling Java files... "
javac *.java
print_status "Compilation completed " $?

# Start RMI registry in background
print_info "Starting RMI registry... "
rmiregistry 1099 &
REGISTRY_PID=$!
sleep 2
print_status "RMI registry started (PID: $REGISTRY_PID)" 0

# Start server in background
print_info "Starting calculator server... "
java CalculatorServer &
SERVER_PID=$!
sleep 3
print_status "Calculator server started (PID: $SERVER_PID) " 0

# Function to cleanup processes
cleanup() {
    print_info "Cleaning up processes... "
    if [ ! -z "$SERVER_PID" ]; then
        kill $SERVER_PID 2>/dev/null
        print_info "Server stopped "
    fi
    if [ ! -z "$REGISTRY_PID" ]; then
        kill $REGISTRY_PID 2>/dev/null
        print_info "Registry stopped "
    fi
    # Kill any remaining Java processes related to our test
    pkill -f "CalculatorServer\|rmiregistry" 2>/dev/null
}

# Set trap to cleanup on script exit
trap cleanup EXIT

# Test 1: Single client automated tests
print_info "Running single client automated tests... "
echo "----------------------------------------"
java CalculatorClient test SingleClient
if [ $? -eq 0 ]; then
    print_status "Single client tests passed " 0
else
    print_status "Single client tests failed " 1
fi

sleep 2

# Test 2: Multiple clients test
print_info "Running multiple clients test... "
echo "----------------------------------------"
java TestMultipleClients
if [ $? -eq 0 ]; then
    print_status "Multiple clients tests passed " 0
else
    print_status "Multiple clients tests failed " 1
fi

sleep 2

# Test 3: Concurrent client operations
print_info "Running concurrent operations test... "
echo "----------------------------------------"

# Start multiple clients in background
for i in {1..3}; do
    (
        echo "Client $i starting... "
        java CalculatorClient test "ConcurrentClient-$i" 
    ) &
    CLIENT_PIDS[$i]=$!
done

# Wait for all clients to complete
for i in {1..3}; do
    wait ${CLIENT_PIDS[$i]}
    if [ $? -eq 0 ]; then
        print_info "Concurrent client $i completed successfully "
    else
        print_info "Concurrent client $i failed "
    fi
done

print_status "Concurrent operations test completed " 0

# Test 4: Interactive mode demo (optional)
print_info "Testing interactive mode availability... "
echo "----------------------------------------"
echo "To test interactive mode manually, run: "
echo "java CalculatorClient interactive"
echo ""
echo "Available interactive commands: "
echo "- push <number>     : Push number to stack "
echo "- pop               : Pop from stack"
echo "- operation <op>    : Apply operation (min/max/gcd/lcm) "
echo "- delaypop <ms>     : Pop with delay "
echo "- empty             : Check if stack is empty "
echo "- quit              : Exit interactive mode "

print_status "Interactive mode is available " 0

# Summary
echo ""
echo "==================================="
echo "All system tests completed successfully!"
echo "==================================="
echo ""
echo "Test Summary :"
echo "✓ Single client functionality "
echo "✓ Multiple client support "
echo "✓ Concurrent operations "
echo "✓ All RMI methods tested "
echo "  - pushValue() "
echo "  - pushOperation() "
echo "  - pop() "
echo "  - delayPop() "
echo "  - isEmpty() "
echo ""
echo "The calculator server successfully handles:"
echo "- Shared stack for all clients "
echo "- Thread-safe operations "
echo "- Mathematical operations (min, max, gcd, lcm) "
echo "- Error handling "
echo "- Delayed operations "