package retailmanagement;
import java.util.*;
import java.text.SimpleDateFormat;

public class StoreManager {
    private List<Product> products;
    private Map<String, Product> productMap;
    private List<Order> orders;
    private Set<String> categories;
    private int orderCounter;
    private List<String> activityLog;
    private SimpleDateFormat dateFormat;
    private OrderProcessingQueue orderQueue;

    public StoreManager() {
        products = new ArrayList<>();
        productMap = new HashMap<>();
        orders = new ArrayList<>();
        categories = new TreeSet<>();
        orderCounter = 1;
        activityLog = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderQueue = new OrderProcessingQueue();
    }

    public void log(String message) {
        String timestamp = dateFormat.format(new java.util.Date());
        activityLog.add("[" + timestamp + "] " + message);
    }

    public List<String> getActivityLog() {
        return new ArrayList<>(activityLog);
    }

    public void clearLog() {
        activityLog.clear();
    }

    public void addProduct(Product product) {
        products.add(product);
        productMap.put(product.getId(), product);
        categories.add(product.getCategory());
        log("Added product: " + product.getId() + " - " + product.getName());
    }

    public boolean removeProduct(String productId) {
        Product product = productMap.get(productId);
        if (product != null) {
            products.remove(product);
            productMap.remove(productId);
            log("Removed product: " + productId);
            return true;
        }
        return false;
    }

    public Product getProductById(String id) {
        return productMap.get(id);
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> searchProducts(String query) {
        List<Product> results = new ArrayList<>();
        String searchLower = query.toLowerCase();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(searchLower) || 
                p.getId().toLowerCase().contains(searchLower)) {
                results.add(p);
            }
        }
        return results;
    }

    public Order createOrder(String customerName) {
        String orderId = "ORD" + String.format("%04d", orderCounter++);
        Order order = new Order(orderId, customerName);
        orders.add(order);
        log("Created order: " + orderId + " for " + customerName);
        return order;
    }

    public boolean completeOrder(String orderId) {
        for (Order order : orders) {
            if (order.getOrderId().equals(orderId)) {
                order.completeOrder();
                log("Completed order: " + orderId);
                return true;
            }
        }
        return false;
    }

    public Order getOrderById(String orderId) {
        for (Order o : orders) {
            if (o.getOrderId().equals(orderId)) {
                return o;
            }
        }
        return null;
    }

    public List<Order> getPendingOrders() {
        List<Order> pending = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == Order.Status.PENDING) {
                pending.add(o);
            }
        }
        return pending;
    }

    public List<Order> getCompletedOrders() {
        List<Order> completed = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == Order.Status.COMPLETED) {
                completed.add(o);
            }
        }
        return completed;
    }

    public Set<String> getCategories() {
        return new TreeSet<>(categories);
    }
    
    public boolean queueOrderForProcessing(String orderId) {
        Order order = getOrderById(orderId);
        if (order != null && order.getStatus() == Order.Status.PENDING) {
            orderQueue.enqueueOrder(order);
            log("Order " + orderId + " added to processing queue");
            return true;
        }
        return false;
    }

    public Order processNextOrder() {
        Order order = orderQueue.dequeueOrder();
        if (order != null) {
            order.completeOrder();
            log("Processed order from queue: " + order.getOrderId());
        }
        return order;
    }

    public Order peekNextOrderInQueue() {
        return orderQueue.peekNextOrder();
    }

    public int getOrderQueueSize() {
        return orderQueue.getQueueSize();
    }

    public Order[] getQueuedOrders() {
        return orderQueue.getAllOrders();
    }
}