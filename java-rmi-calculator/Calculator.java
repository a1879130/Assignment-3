import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the Java RMI Calculator assignment.
 */
public interface Calculator extends Remote {
    /**
     * Push an integer value onto the server-side stack.
     * @param val integer value to push
     * @throws RemoteException on RMI error
     */
    void pushValue(int val) throws RemoteException;

    /**
     * Push an operation token which causes the server to pop all values and
     * compute the specified operation ("min", "max", "lcm", "gcd").
     * The server will push the computed integer result back on the stack.
     * @param operator operation token
     * @throws RemoteException on RMI error
     */
    void pushOperation(String operator) throws RemoteException;

    /**
     * Pop the top integer from the server stack and return it.
     * @return popped integer value
     * @throws RemoteException on RMI error
     */
    int pop() throws RemoteException;

    /**
     * Return true if the server stack is empty.
     * @return true if empty
     * @throws RemoteException on RMI error
     */
    boolean isEmpty() throws RemoteException;

    /**
     * Wait millis milliseconds, then pop the top integer and return it.
     * @param millis sleep time in milliseconds before popping
     * @return popped integer value
     * @throws RemoteException on RMI error
     */
    int delayPop(int millis) throws RemoteException;
}