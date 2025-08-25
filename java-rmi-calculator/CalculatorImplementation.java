import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/**
 * CalculatorImplementation provides the concrete implementation of the Calculator interface
 * 
 * This class extends UnicastRemoteObject to enable RMI functionality
 */
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    
    private static final long serialVersionUID = 1L;
    
    // Shared stack for all clients
    
    private Stack<Integer> stack;
    
    /**
     * Constructor initializes the calculator implementation
     * 
     * @throws RemoteException if the remote object cannot be created
     *                        
     */
    public CalculatorImplementation() throws RemoteException {
        super();
        this.stack = new Stack<>();
        System.out.println("Calculator implementation initialized.");
    }
    
    /**
     * Push a value onto the stack (thread-safe implementation)
     * 
     * @param val the integer value to push onto the stack
     *          
     * @throws RemoteException if a network error occurs during the remote call
     */
    @Override
    public synchronized void pushValue(int val) throws RemoteException {
        stack.push(val);
        System.out.println("Pushed value: " + val + " | Stack size: " + stack.size());
    }
    
    /**
     * Push an operation onto the stack and execute it on all values
     * 
     * @param operator the operation to perform: "min", "max", "lcm", or "gcd"
     *  
     * @throws RemoteException if a network error occurs during the remote call
     *                    
     */
    @Override
    public synchronized void pushOperation(String operator) throws RemoteException {
        if (stack.isEmpty()) {
            System.out.println("Warning: Operation " + operator + " called on empty stack. " 
            return;
        }
        
        // Pop all values from the stack
    
        List<Integer> values = new ArrayList<>();
        while (!stack.isEmpty()) {
            values.add(stack.pop());
        }
        
        int result;
        switch (operator.toLowerCase()) {
            case "min":
                result = findMin(values);
                break;
            case "max":
                result = findMax(values);
                break;
            case "lcm":
                result = findLCM(values);
                break;
            case "gcd":
                result = findGCD(values);
                break;
            default:
                throw new RemoteException("Invalid operator: " + operator + 
                                        " 无效操作符: " + operator);
        }
        
        // Push the result back onto the stack
        stack.push(result);
        System.out.println("Operation " + operator + " executed. Result: " + result);
    }
    
    /**
     * Pop and return the top value from the stack
     * 
     * @return the top value from the stack
     * @throws RemoteException if the stack is empty or a network error occurs
     */
    @Override
    public synchronized int pop() throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty, cannot pop. 堆栈为空，无法弹出。");
        }
        int value = stack.pop();
        System.out.println("Popped value: " + value + " | Remaining stack size: " + stack.size() +
                          " 弹出值: " + value + " | 剩余堆栈大小: " + stack.size());
        return value;
    }
    
    /**
     * Check if the stack is empty
     * 
     * @return true if the stack is empty, false otherwise
     *         
     * @throws RemoteException if a network error occurs during the remote call
     *                       
     */
    @Override
    public synchronized boolean isEmpty() throws RemoteException {
        boolean empty = stack.isEmpty();
        System.out.println("Stack empty check: " + empty + " 堆栈空检查: " + empty);
        return empty;
    }
    
    /**
     * Wait for specified milliseconds then pop the top value from the stack
     * @param millis the number of milliseconds to wait before popping
     * @return the top value from the stack after the delay
     * @throws RemoteExcept
     */
    @Override
    public synchronized int delayPop(int millis) throws RemoteException {
        System.out.println("DelayPop called with delay: " + millis + "ms. ");
        
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty, cannot delay pop.");
        }
        
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("DelayPop interrupted.", e);
        }
        
        int value = stack.pop();
        System.out.println("DelayPop completed. Popped value: " + value);
        return value;
    }
    
    /**
     * Find the minimum value in the list
     * 
     * @param values list of integers to find minimum from
     * @return the minimum value
     */
    private int findMin(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).min().orElse(0);
    }
    
    /**
     * Find the maximum value in the list
     * 
     * @param values list of integers to find maximum from
     * @return the maximum value
     */
    private int findMax(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).max().orElse(0);
    }
    
    /**
     * Find the least common multiple of all values in the list
     * 查找列表中所有值的最小公倍数
     * 
     * @param values list of integers to find LCM from
     *              要查找最小公倍数的整数列表
     * @return the least common multiple
     *         最小公倍数
     */
    private int findLCM(List<Integer> values) {
        if (values.isEmpty()) return 0;
        
        int lcm = Math.abs(values.get(0));
        for (int i = 1; i < values.size(); i++) {
            lcm = lcm(lcm, Math.abs(values.get(i)));
        }
        return lcm;
    }
    
    /**
     * Find the greatest common divisor of all values in the list
     * 
     * @param values list of integers to find GCD from
     *             
     * @return the greatest common divisor
     */
    private int findGCD(List<Integer> values) {
        if (values.isEmpty()) return 0;
        
        int gcd = Math.abs(values.get(0));
        for (int i = 1; i < values.size(); i++) {
            gcd = gcd(gcd, Math.abs(values.get(i)));
        }
        return gcd;
    }
    
    /**
     * Calculate LCM of two numbers
     * 
     * @param a first number 
     * @param b second number 
     * @return LCM of a and b
     */
    private int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }
    
    /**
     * Calculate GCD of two numbers using Euclidean algorithm
     * 
     * @param a first number 
     * @param b second number 
     * @return GCD of a and b
     */
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}