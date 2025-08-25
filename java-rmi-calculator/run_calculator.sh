#!/bin/bash
# Quick start script for Java RMI Calculator

echo "====================================="
echo "Java RMI Calculator Quick Start"
echo "====================================="

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_header() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# Compile Java files
print_header "Step 1: Compilation "
print_info "Compiling Java files... "
javac *.java
if [ $? -eq 0 ]; then
    print_success "Compilation successful "
else
    echo "Compilation failed!"
    exit 1
fi

# Start RMI Registry
print_header "Step 2: Starting RMI Registry "
print_info "Starting RMI registry on port 1099... "
# Kill any existing registry
pkill -f rmiregistry 2>/dev/null
rmiregistry 1099 &
REGISTRY_PID=$!
sleep 2
print_success "RMI registry started (PID: $REGISTRY_PID) "

# Start Server
print_header "Step 3: Starting Calculator Server "
print_info "Starting calculator server... "
java CalculatorServer &
SERVER_PID=$!
sleep 3
print_success "Calculator server started (PID: $SERVER_PID) "

# Function to cleanup
cleanup() {
    print_info "Cleaning up... "
    kill $SERVER_PID 2>/dev/null
    kill $REGISTRY_PID 2>/dev/null
    pkill -f "CalculatorServer\|rmiregistry" 2>/dev/null
    print_info "Cleanup completed "
}

# Set trap for cleanup
trap cleanup EXIT

# Menu for different operations
print_header "Step 4: Choose Operation Mode "
echo ""
echo "Available options "
echo "1) Run automated tests "
echo "2) Start interactive client "  
echo "3) Test multiple clients "
echo "4) Run complete test suite "
echo "5) Keep server running "
echo "6) Exit "
echo ""

while true; do
    read -p "Enter your choice (1-6) : " choice
    case $choice in
        1)
            print_header "Running Automated Tests "
            java CalculatorClient test AutoTest
            break
            ;;
        2)
            print_header "Starting Interactive Client "
            echo "Type 'help' for available commands "
            echo "Type 'quit' to exit "
            java CalculatorClient interactive
            break
            ;;
        3)
            print_header "Testing Multiple Clients "
            java TestMultipleClients
            break
            ;;
        4)
            print_header "Running Complete Test Suite "
            if [ -f "test_system.sh" ]; then
                # Kill current processes since test_system.sh will start its own
                kill $SERVER_PID 2>/dev/null
                kill $REGISTRY_PID 2>/dev/null
                sleep 1
                chmod +x test_system.sh
                ./test_system.sh
                exit 0
            else
                echo "test_system.sh not found! "
                java CalculatorClient test CompleteTest
            fi
            break
            ;;
        5)
            print_header "Server Running "
            echo "Server is now running and ready for clients"
            echo ""
            echo "To connect clients, open new terminals and run:"
            echo "  java CalculatorClient interactive"
            echo "  java CalculatorClient test"
            echo ""
            echo "Press Ctrl+C to stop the server"
            
            # Wait indefinitely
            while true; do
                sleep 1
            done
            ;;
        6)
            print_info "Exiting... "
            break
            ;;
        *)
            echo "Invalid choice. Please enter 1-6. "
            ;;
    esac
done

print_success "Operation completed "