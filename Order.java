package retailmanagement;
import java.util.ArrayList;
import java.util.List;

public class Order {
    public enum Status { PENDING, COMPLETED }
    
    private String orderId;
    private String customerName;
    private List<Product> products;
    private List<Integer> quantities;
    private Status status;

    public Order(String orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.products = new ArrayList<>();
        this.quantities = new ArrayList<>();
        this.status = Status.PENDING;
    }

    public void addProduct(Product product, int quantity) {
        products.add(product);
        quantities.add(quantity);
        product.setStock(product.getStock() - quantity);
    }

    public void completeOrder() {
        this.status = Status.COMPLETED;
    }

    public double getTotal() {
        double total = 0;
        for (int i = 0; i < products.size(); i++) {
            total += products.get(i).getPrice() * quantities.get(i);
        }
        return total;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public List<Product> getProducts() { return products; }
    public List<Integer> getQuantities() { return quantities; }
    public Status getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Order %s - %s ($%.2f) [%s]", 
            orderId, customerName, getTotal(), status);
    }

    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Products:\n");
        
        for (int i = 0; i < products.size(); i++) {
            sb.append(String.format("  - %s x%d @ $%.2f\n", 
                products.get(i).getName(), 
                quantities.get(i), 
                products.get(i).getPrice()));
        }
        
        sb.append("Total: $").append(String.format("%.2f", getTotal()));
        return sb.toString();
    }
}