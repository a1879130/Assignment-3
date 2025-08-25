import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Calculator interface defines the remote operations for the RMI calculator service
 * Calculator接口定义了RMI计算器服务的远程操作
 * 
 * This interface extends Remote to enable RMI functionality
 
public interface Calculator extends Remote {
    
    /**
     * Push a value onto the stack
     * 
     * @param val the integer value to push onto the stack
     *            
     * @throws RemoteException if a network error occurs during the remote call
     *                        
     */
    void pushValue(int val) throws RemoteException;
    
    /**
     * Push an operation onto the stack and execute it
     * 
     * This method pushes an operator to the stack, which causes the server to:
     * - Pop all values from the stack
     * - Perform the specified operation on all values
     * - Push the result back onto the stack
     * 
     * @param operator the operation to perform: "min", "max", "lcm", or "gcd"
     *             
     * @throws RemoteException if a network error occurs during the remote call
     *                       
     */
    void pushOperation(String operator) throws RemoteException;
    
    /**
     * Pop and return the top value from the stack
     * 
     * 
     * @return the top value from the stack
     *        
     * @throws RemoteException if a network error occurs during the remote call
     *                        
     */
    int pop() throws RemoteException;
    
    /**
     * Check if the stack is empty
     * 
     * @return true if the stack is empty, false otherwise
     *    
     * @throws RemoteException if a network error occurs during the remote call
     *                      
     */
    boolean isEmpty() throws RemoteException;
    
    /**
     * Wait for specified milliseconds then pop the top value from the stack
     * 
     * This method introduces a delay before performing the pop operation
     * 
     * @param millis the number of milliseconds to wait before popping
     *              
     * @return the top value from the stack after the delay
     *         
     * @throws RemoteException if a network error occurs during the remote call
     *                        
     */
    int delayPop(int millis) throws RemoteException;
}