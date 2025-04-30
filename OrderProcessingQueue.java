package retailmanagement;
import java.util.LinkedList;
import java.util.Queue;

public class OrderProcessingQueue {
    private Queue<Order> processingQueue;
    
    public OrderProcessingQueue() {
        processingQueue = new LinkedList<>();
    }
    
    public void enqueueOrder(Order order) {
        processingQueue.add(order);
    }
    
    public Order dequeueOrder() {
        if (processingQueue.isEmpty()) {
            return null;
        }
        return processingQueue.remove();
    }
    
    public Order peekNextOrder() {
        return processingQueue.peek();
    }
    
    public boolean containsOrder(String orderId) {
        for (Order order : processingQueue) {
            if (order.getOrderId().equals(orderId)) {
                return true;
            }
        }
        return false;
    }
    
    public int getQueueSize() {
        return processingQueue.size();
    }
    
    public boolean isEmpty() {
        return processingQueue.isEmpty();
    }
    
    public Order[] getAllOrders() {
        return processingQueue.toArray(new Order[0]);
    }
}