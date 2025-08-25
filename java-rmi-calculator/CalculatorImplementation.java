import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.math.BigInteger;

/**
 * Implementation of the Calculator remote interface.
 * Uses a single shared stack on the server for all clients (default requirement).
 *
 * Thread-safety:
 *   All stack operations synchronize on the internal stack object.
 */
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private static final long serialVersionUID = 1L;

    // Server-side stack that stores integer values.
    private final Deque<Integer> stack = new ArrayDeque<>();

    /**
     * Constructor required for UnicastRemoteObject subclasses.
     * @throws RemoteException if RMI setup fails
     */
    protected CalculatorImplementation() throws RemoteException {
        super();
    }

    /**
     * Push an integer value onto the server-side stack.
     * @param val integer value to push
     * @throws RemoteException on RMI error
     */
    @Override
    public void pushValue(int val) throws RemoteException {
        synchronized (stack) {
            stack.push(val);
            stack.notifyAll();
        }
    }

    /**
     * Push an operation token which causes the server to pop all values and
     * compute the specified operation ("min", "max", "lcm", "gcd").
     * The server will push the computed integer result back on the stack.
     * Special cases: if stack is empty, no-op (but per assignment we assume sensible usage).
     *
     * @param operator operation token
     * @throws RemoteException on RMI error
     */
    @Override
    public void pushOperation(String operator) throws RemoteException {
        List<Integer> values = new ArrayList<>();
        synchronized (stack) {
            while (!stack.isEmpty()) {
                values.add(stack.pop());
            }
        }

        if (values.isEmpty()) {
            // nothing to operate on; according to assignment this shouldn't occur
            return;
        }

        int result;
        switch (operator.toLowerCase()) {
            case "min":
                result = values.get(0);
                for (int v : values) result = Math.min(result, v);
                break;
            case "max":
                result = values.get(0);
                for (int v : values) result = Math.max(result, v);
                break;
            case "gcd":
                result = values.get(0);
                for (int v : values) result = gcd(result, v);
                break;
            case "lcm":
                result = values.get(0);
                for (int v : values) result = lcm(result, v);
                break;
            default:
                // Unknown operator: ignore (assignment promises only valid operators)
                return;
        }

        synchronized (stack) {
            stack.push(result);
            stack.notifyAll();
        }
    }

    /**
     * Pop the top integer from the server stack and return it.
     * Assumes caller will not pop when empty (assignment guarantee), but we handle empty by throwing RemoteException.
     * @return popped integer value
     * @throws RemoteException on RMI error or empty stack
     */
    @Override
    public int pop() throws RemoteException {
        synchronized (stack) {
            if (stack.isEmpty()) {
                throw new RemoteException("Pop attempted on empty stack");
            }
            return stack.pop();
        }
    }

    /**
     * Return true if the server stack is empty.
     * @return true if empty
     * @throws RemoteException on RMI error
     */
    @Override
    public boolean isEmpty() throws RemoteException {
        synchronized (stack) {
            return stack.isEmpty();
        }
    }

    /**
     * Wait millis milliseconds, then pop the top integer and return it.
     * The waiting occurs before acquiring the stack lock so other clients can operate while waiting.
     * @param millis sleep time in milliseconds before popping
     * @return popped integer value
     * @throws RemoteException on RMI error or empty stack at time of pop
     */
    @Override
    public int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(Math.max(0, millis));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("delayPop interrupted", e);
        }
        // perform pop (this will validate non-empty)
        return pop();
    }

    // Helper: gcd using BigInteger (handles negative values).
    private int gcd(int a, int b) {
        BigInteger A = BigInteger.valueOf(Math.abs((long)a));
        BigInteger B = BigInteger.valueOf(Math.abs((long)b));
        BigInteger g = A.gcd(B);
        return g.intValue();
    }

    // Helper: lcm using BigInteger to avoid overflow where possible.
    private int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        BigInteger A = BigInteger.valueOf(Math.abs((long)a));
        BigInteger B = BigInteger.valueOf(Math.abs((long)b));
        BigInteger g = A.gcd(B);
        BigInteger l = A.divide(g).multiply(B);
        return l.intValue();
    }
}