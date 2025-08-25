import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.util.Scanner;

/**
 * CalculatorClient is a test client that connects to the calculator server
 * 
 * This class demonstrates all the remote operations and provides both
 * interactive and automated testing capabilities
 */
public class CalculatorClient {
    
    // Default server configuration
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1099;
    private static final String SERVICE_NAME = "CalculatorService";
    
    private Calculator calculator;
    private String clientId;
    
    /**
     * Constructor initializes the client with a unique identifier
     * 
     * @param clientId unique identifier for this client instance
     */
    public CalculatorClient(String clientId) {
        this.clientId = clientId;
    }
    
    /**
     * Main method to start the calculator client
     * 
     * @param args command line arguments:
     *            args[0] - client mode: "interactive" or "test"
     *                     
     *            args[1] - client ID (optional)
     *                   
     */
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "interactive";
        String clientId = args.length > 1 ? args[1] : "Client-" + System.currentTimeMillis();
        
        CalculatorClient client = new CalculatorClient(clientId);
        
        try {
            // Connect to the server
            client.connectToServer();
            
            if ("test".equalsIgnoreCase(mode)) {
                // Run automated tests
                client.runAutomatedTests();
            } else {
                // Run interactive mode
                client.runInteractiveMode();
            }
            
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Connect to the RMI server and lookup the calculator service
     * 
     * @throws RemoteException if connection fails
     * @throws NotBoundException if the service is not bound in the registry
     * @throws MalformedURLException if the service URL is malformed
     */
    private void connectToServer() throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("[" + clientId + "] Connecting to calculator server...");
        
        String serviceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + SERVICE_NAME;
        calculator = (Calculator) Naming.lookup(serviceUrl);
        
        System.out.println("[" + clientId + "] Successfully connected to: " + serviceUrl);
    }
    
    /**
     * Run automated tests to verify all calculator operations
     * 
     * Tests include single-client and multi-client scenarios
     */
    private void runAutomatedTests() {
        System.out.println("\n[" + clientId + "] Running automated tests...");
        System.out.println("==========================================");
        
        try {
            // Test 1: Basic push and pop operations
            testBasicOperations();
            
            // Test 2: isEmpty functionality
            testIsEmptyFunction();
            
            // Test 3: Mathematical operations
            testMathematicalOperations();
            
            // Test 4: Delay pop functionality
            testDelayPop();
            
            // Test 5: Error handling
            testErrorHandling();
            
            System.out.println("\n[" + clientId + "] All automated tests completed successfully!");
    
            
        } catch (Exception e) {
            System.err.println("[" + clientId + "] Test failed: " + e.getMessage());
            System.err.println("[" + clientId + "] 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test basic push and pop operations
     */
    private void testBasicOperations() throws RemoteException {
        System.out.println("\n--- Test 1: Basic Operations ---");
        
        // Push some values
        calculator.pushValue(10);
        calculator.pushValue(20);
        calculator.pushValue(30);
        
        // Pop values and verify
        int val1 = calculator.pop(); // Should be 30
        int val2 = calculator.pop(); // Should be 20
        int val3 = calculator.pop(); // Should be 10
        
        System.out.println("Popped values: " + val1 + ", " + val2 + ", " + val3);
        
        assert val1 == 30 && val2 == 20 && val3 == 10 : "Basic operations test failed";
        System.out.println("✓ Basic operations test passed");
    }
    
    /**
     * Test isEmpty functionality
     */
    private void testIsEmptyFunction() throws RemoteException {
        System.out.println("\n--- Test 2: isEmpty Function ---");
        
        // Stack should be empty now
        boolean empty1 = calculator.isEmpty();
        System.out.println("Stack empty (should be true): " + empty1);
        
        // Push a value
        calculator.pushValue(100);
        boolean empty2 = calculator.isEmpty();
        System.out.println("Stack empty after push (should be false): " + empty2);
        
        // Pop the value
        calculator.pop();
        boolean empty3 = calculator.isEmpty();
        System.out.println("Stack empty after pop (should be true): " + empty3);
        
        assert empty1 && !empty2 && empty3 : "isEmpty function test failed";
        System.out.println("✓ isEmpty function test passed");
    }
    
    /**
     * Test mathematical operations (min, max, lcm, gcd)
     */
    private void testMathematicalOperations() throws RemoteException {