import java.rmi.Naming;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TestMultipleClients tests the calculator server with multiple concurrent clients
 * 
 * This class demonstrates that the server can handle multiple clients simultaneously
 * and that all clients share the same stack 
 */
public class TestMultipleClients {
    
    private static final String SERVICE_URL = "rmi://localhost:1099/CalculatorService";
    private static final int NUM_CLIENTS = 5;
    
    /**
     * Main method to run multiple client tests
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting multiple client test with " + NUM_CLIENTS + " clients...");
        
        
        try {
            // Test concurrent push operations
            testConcurrentPushOperations();
            
            Thread.sleep(2000); // Wait between tests
            
            // Test concurrent operations
            testConcurrentOperations();
            
            Thread.sleep(2000); // Wait between tests
            
            // Test concurrent pop operations
            testConcurrentPopOperations();
            
            System.out.println("\nAll multiple client tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Multiple client test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test multiple clients pushing values concurrently
     */
    private static void testConcurrentPushOperations() throws Exception {
        System.out.println("\n=== Test 1: Concurrent Push Operations ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);
        CountDownLatch latch = new CountDownLatch(NUM_CLIENTS);
        
        // Each client pushes values
        for (int i = 0; i < NUM_CLIENTS; i++) {
            final int clientId = i + 1;
            executor.submit(() -> {
                try {
                    Calculator calc = (Calculator) Naming.lookup(SERVICE_URL);
                    
                    // Each client pushes multiple values
                    for (int j = 1; j <= 3; j++) {
                        int value = clientId * 10 + j;
                        calc.pushValue(value);
                        System.out.println("Client " + clientId + " pushed: " + value );
                        Thread.sleep(100); // Small delay to see interleaving
                    }
                    
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Check final stack state
        Calculator calc = (Calculator) Naming.lookup(SERVICE_URL);
        System.out.println("Stack empty after pushes: " + calc.isEmpty());
    }
    
    /**
     * Test multiple clients performing operations concurrently
     */
    private static void testConcurrentOperations() throws Exception {
        System.out.println("\n=== Test 2: Concurrent Operations 并发操作测试 ===");
        
        // First, ensure we have some values on the stack
        Calculator setupCalc = (Calculator) Naming.lookup(SERVICE_URL);
        if (setupCalc.isEmpty()) {
            setupCalc.pushValue(12);
            setupCalc.pushValue(18);
            setupCalc.pushValue(24);
            setupCalc.pushValue(36);
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        String[] operations = {"gcd", "max"};
        
        for (int i = 0; i < 2; i++) {
            final int clientId = i + 1;
            final String operation = operations[i % operations.length];
            
            executor.submit(() -> {
                try {
                    Calculator calc = (Calculator) Naming.lookup(SERVICE_URL);
                    
                    // Add more values before operation
                    calc.pushValue(6 * clientId);
                    calc.pushValue(9 * clientId);
                    
                    System.out.println("Client " + clientId + " performing operation: " + operation);
                    
                    calc.pushOperation(operation);
                    
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        System.out.println("Concurrent operations completed 并发操作完成");
    }
    
    /**
     * Test multiple clients popping values concurrently
     */
    private static void testConcurrentPopOperations() throws Exception {
        System.out.println("\n=== Test 3: Concurrent Pop Operations ===");
        
        // Ensure we have values to pop
        Calculator setupCalc = (Calculator) Naming.lookup(SERVICE_URL);
        for (int i = 1; i <= 10; i++) {
            setupCalc.pushValue(i * 5);
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);
        CountDownLatch latch = new CountDownLatch(NUM_CLIENTS);
        
        for (int i = 0; i < NUM_CLIENTS; i++) {
            final int clientId = i + 1;
            executor.submit(() -> {
                try {
                    Calculator calc = (Calculator) Naming.lookup(SERVICE_URL);
                    
                    // Each client attempts to pop values
                    for (int j = 0; j < 2; j++) {
                        try {
                            int value = calc.pop();
                            System.out.println("Client " + clientId + " popped: " + value);
                            Thread.sleep(200);
                        } catch (Exception e) {
                            System.out.println("Client " + clientId + " couldn't pop: " + e.getMessage());
                            break;
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Test delayPop with multiple clients
        testConcurrentDelayPop();
        
        System.out.println("Concurrent pop operations completed ");
    }
    
    /**
     * Test multiple clients using delayPop concurrently
     */
    private static void testConcurrentDelayPop() throws Exception {
        System.out.println("\n--- Concurrent DelayPop Test  ---");
        
        // Add values for delayPop test
        Calculator setupCalc = (Calculator) Naming.lookup(SERVICE_URL);
        setupCalc.pushValue(100);
        setupCalc.pushValue(200);
        setupCalc.pushValue(300);
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        
        for (int i = 0; i < 3; i++) {
            final int clientId = i + 1;
            final int delay = 500 + (i * 200); // Different delays for each client
            
            executor.submit(() -> {
                try {
                    Calculator calc = (Calculator) Naming.lookup(SERVICE_URL);
                    
                    long startTime = System.currentTimeMillis();
                    System.out.println("Client " + clientId + " starting delayPop with " + delay + "ms delay");
                    
                    int value = calc.delayPop(delay);
                    long endTime = System.currentTimeMillis();
                    
                    System.out.println("Client " + clientId + " delayPopped: " + value + 
                                     " after " + (endTime - startTime) + "ms");
                    
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " delayPop error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
    }
}