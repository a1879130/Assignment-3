import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;

/**
 * CalculatorServer is the server bootstrap class that starts the RMI server
 * 
 * This class creates and registers the calculator service with the RMI registry
 */
public class CalculatorServer {
    
    // Default registry port for RMI
    private static final int REGISTRY_PORT = 1099;
    
    // Service name for binding in the registry
    private static final String SERVICE_NAME = "CalculatorService";
    
    /**
     * Main method to start the calculator server
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting Calculator RMI Server...");
            
            // Create and start the RMI registry
            startRegistry();
            
            // Create the calculator implementation
            CalculatorImplementation calculator = new CalculatorImplementation();
            
            // Bind the calculator service to the registry
            bindService(calculator);
            
            System.out.println("Calculator RMI Server is ready and waiting for client connections.");
            System.out.println("Service bound as: " + SERVICE_NAME);
            System.out.println("Registry running on port: " + REGISTRY_PORT);
            
            // Keep the server running
            keepServerAlive();
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Start the RMI registry on the default port
     * 
     * Creates a local registry if one doesn't exist
     * 
     * @throws RemoteException if the registry cannot be created
     */
    private static void startRegistry() throws RemoteException {
        try {
            // Try to locate existing registry
            LocateRegistry.getRegistry(REGISTRY_PORT).list();
            System.out.println("RMI registry already running on port " + REGISTRY_PORT);
        } catch (RemoteException e) {
            // Registry doesn't exist, create a new one
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            System.out.println("Created new RMI registry on port " + REGISTRY_PORT);
        }
    }
    
    /**
     * Bind the calculator service to the RMI registry
     * 
     * @param calculator the calculator implementation to bind
     * @throws RemoteException if binding fails due to network issues
     * @throws MalformedURLException if the service URL is malformed
     */
    private static void bindService(CalculatorImplementation calculator) 
            throws RemoteException, MalformedURLException {
        
        // Construct the service URL
        String serviceUrl = "rmi://localhost:" + REGISTRY_PORT + "/" + SERVICE_NAME;
        
        try {
            // Bind the service
            Naming.rebind(serviceUrl, calculator);
            System.out.println("Calculator service successfully bound to: " + serviceUrl);

            
        } catch (Exception e) {
            System.err.println("Failed to bind service: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Keep the server alive by waiting for user input to shutdown
     * 
     * The server will continue running until the user presses Enter
     */
    private static void keepServerAlive() {
        System.out.println("\nServer is running. Press Enter to shutdown the server.");
        System.out.println("==========================================");
        
        try {
            // Wait for user input to shutdown
            System.in.read();
            System.out.println("\nShutting down server...");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }
}