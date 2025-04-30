package retailmanagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainUI extends JFrame {
    private StoreManager manager;
    private JTabbedPane tabs;
    
    private JTable productTable;
    private DefaultTableModel productTableModel;
    
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    
    private JTextArea logArea;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private JPanel salesReportPanel;
    private JLabel totalOrdersLabel;
    private JLabel totalRevenueLabel;
    private JTable salesReportTable;
    private DefaultTableModel salesReportTableModel;

    private JTable queueTable;
    private DefaultTableModel queueTableModel;
    private JLabel queueCountLabel;

    public MainUI() {
        manager = new StoreManager();
        setupUI();
        loadSampleData();
        refreshAll();
    }

    private void setupUI() {
        setTitle("Retail Store Manager");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tabs = new JTabbedPane();
        
        JPanel productsPanel = new JPanel(new BorderLayout());
        
        String[] productColumns = {"ID", "Name", "Category", "Price", "Stock"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productsPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        
        JPanel productButtons = new JPanel();
        productButtons.add(new JButton(new AbstractAction("Add Product") {
            public void actionPerformed(ActionEvent e) { addProduct(); }
        }));
        productButtons.add(new JButton(new AbstractAction("Edit Product") {
            public void actionPerformed(ActionEvent e) { editProduct(); }
        }));
        productButtons.add(new JButton(new AbstractAction("Remove Product") {
            public void actionPerformed(ActionEvent e) { removeProduct(); }
        }));
        productButtons.add(new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) { refreshAll(); }
        }));
        productsPanel.add(productButtons, BorderLayout.SOUTH);
        
        JPanel ordersPanel = new JPanel(new BorderLayout());
        
        String[] orderColumns = {"Order ID", "Customer", "Date", "Items", "Total", "Status"};
        orderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        ordersPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        
        JPanel orderButtons = new JPanel();
        orderButtons.add(new JButton(new AbstractAction("New Order") {
            public void actionPerformed(ActionEvent e) { newOrder(); }
        }));
        orderButtons.add(new JButton(new AbstractAction("Complete Order") {
            public void actionPerformed(ActionEvent e) { completeOrder(); }
        }));
        orderButtons.add(new JButton(new AbstractAction("View Details") {
            public void actionPerformed(ActionEvent e) { viewOrderDetails(); }
        }));
        orderButtons.add(new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) { refreshAll(); }
        }));
        ordersPanel.add(orderButtons, BorderLayout.SOUTH);
        
        JPanel logPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea();
        logArea.setEditable(false);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        JPanel logButtons = new JPanel();
        logButtons.add(new JButton(new AbstractAction("Clear Log") {
            public void actionPerformed(ActionEvent e) { 
                manager.clearLog();
                refreshLog(); 
            }
        }));
        logButtons.add(new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) { refreshLog(); }
        }));
        logPanel.add(logButtons, BorderLayout.SOUTH);
        
        setupSalesReportPanel();

        JPanel queuePanel = new JPanel(new BorderLayout());

        String[] queueColumns = {"Order ID", "Customer", "Items", "Total"};
        queueTableModel = new DefaultTableModel(queueColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queueTable = new JTable(queueTableModel);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        JPanel queueInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queueInfoPanel.add(new JLabel("Orders in queue: "));
        queueCountLabel = new JLabel("0");
        queueInfoPanel.add(queueCountLabel);
        queuePanel.add(queueInfoPanel, BorderLayout.NORTH);

        JPanel queueButtons = new JPanel();
        queueButtons.add(new JButton(new AbstractAction("Add to Queue") {
            public void actionPerformed(ActionEvent e) { addToQueue(); }
        }));
        queueButtons.add(new JButton(new AbstractAction("Process Next") {
            public void actionPerformed(ActionEvent e) { processNextInQueue(); }
        }));
        queueButtons.add(new JButton(new AbstractAction("Process All") {
            public void actionPerformed(ActionEvent e) { processAllInQueue(); }
        }));
        queueButtons.add(new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) { refreshQueue(); }
        }));
        queuePanel.add(queueButtons, BorderLayout.SOUTH);
        
        tabs.addTab("Products", productsPanel);
        tabs.addTab("Orders", ordersPanel);
        tabs.addTab("Sales Report", salesReportPanel);
        tabs.addTab("Activity Log", logPanel);
        tabs.addTab("Order Queue", queuePanel);

        
        add(tabs);
    }
    
    private void setupSalesReportPanel() {
        salesReportPanel = new JPanel(new BorderLayout());
        
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        summaryPanel.add(new JLabel("Total Completed Orders:"));
        totalOrdersLabel = new JLabel("0");
        summaryPanel.add(totalOrdersLabel);
        
        summaryPanel.add(new JLabel("Total Revenue:"));
        totalRevenueLabel = new JLabel("$0.00");
        summaryPanel.add(totalRevenueLabel);
        
        salesReportPanel.add(summaryPanel, BorderLayout.NORTH);
        
        String[] reportColumns = {"Order ID", "Customer", "Date", "Products", "Quantity", "Amount"};
        salesReportTableModel = new DefaultTableModel(reportColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesReportTable = new JTable(salesReportTableModel);
        salesReportPanel.add(new JScrollPane(salesReportTable), BorderLayout.CENTER);
        
        JPanel reportButtons = new JPanel();
        reportButtons.add(new JButton(new AbstractAction("Refresh Report") {
            public void actionPerformed(ActionEvent e) { refreshSalesReport(); }
        }));
        salesReportPanel.add(reportButtons, BorderLayout.SOUTH);
    }

    private void loadSampleData() {
        manager.addProduct(new Product("P1", "Laptop", "Electronics", 999.99, 10));
        manager.addProduct(new Product("P2", "Mouse", "Electronics", 19.99, 50));
        manager.addProduct(new Product("P3", "Notebook", "Stationery", 2.99, 100));
        
        Order order1 = manager.createOrder("John");
        order1.addProduct(manager.getProductById("P1"), 1);
        order1.addProduct(manager.getProductById("P2"), 2);
        manager.completeOrder(order1.getOrderId());
        
        Order order2 = manager.createOrder("Alice");
        order2.addProduct(manager.getProductById("P3"), 5);
    }

    private void refreshAll() {
        refreshProducts();
        refreshOrders();
        refreshSalesReport();
        refreshLog();
        refreshQueue();
    }

    private void refreshProducts() {
        productTableModel.setRowCount(0);
        for (Product p : manager.getProducts()) {
            productTableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),
                String.format("$%.2f", p.getPrice()),
                p.getStock()
            });
        }
    }

    private void refreshOrders() {
        orderTableModel.setRowCount(0);
        for (Order o : manager.getPendingOrders()) {
            addOrderToTable(o);
        }
        for (Order o : manager.getCompletedOrders()) {
            addOrderToTable(o);
        }
    }
    
    private void refreshSalesReport() {
        java.util.List<Order> completedOrders = manager.getCompletedOrders();
        totalOrdersLabel.setText(String.valueOf(completedOrders.size()));
        
        double totalRevenue = 0.0;
        for (Order order : completedOrders) {
            totalRevenue += order.getTotal();
        }
        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        
        salesReportTableModel.setRowCount(0);
        
        for (Order order : completedOrders) {
            for (int i = 0; i < order.getProducts().size(); i++) {
                Product product = order.getProducts().get(i);
                int quantity = order.getQuantities().get(i);
                double amount = product.getPrice() * quantity;
                
                salesReportTableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    dateFormat.format(new Date()),
                    product.getName(),
                    quantity,
                    String.format("$%.2f", amount)
                });
            }
        }
    }
    
    private void addOrderToTable(Order order) {
        orderTableModel.addRow(new Object[]{
            order.getOrderId(),
            order.getCustomerName(),
            dateFormat.format(new Date()),
            order.getProducts().size(),
            String.format("$%.2f", order.getTotal()),
            order.getStatus().toString()
        });
    }

    private void refreshLog() {
        logArea.setText("");
        for (String entry : manager.getActivityLog()) {
            logArea.append(entry + "\n");
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void addProduct() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product(
                    idField.getText(),
                    nameField.getText(),
                    categoryField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(stockField.getText())
                );
                manager.addProduct(p);
                refreshProducts();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "select a product to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = (String) productTableModel.getValueAt(selectedRow, 0);
        Product product = manager.getProductById(productId);

        JTextField nameField = new JTextField(product.getName());
        JTextField categoryField = new JTextField(product.getCategory());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField stockField = new JTextField(String.valueOf(product.getStock()));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Product: " + productId, 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                product.setName(nameField.getText());
                product.setCategory(categoryField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setStock(Integer.parseInt(stockField.getText()));
                refreshProducts();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "select a product to remove", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = (String) productTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove " + productName + "?", 
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.removeProduct(productId)) {
                refreshProducts();
            }
        }
    }

    private void newOrder() {
        String customer = JOptionPane.showInputDialog(this, "Customer name:");
        if (customer == null || customer.trim().isEmpty()) {
            return;
        }

        Order order = manager.createOrder(customer.trim());
        java.util.List<Product> available = manager.getProducts();

        while (true) {
            Product[] productArray = available.toArray(new Product[0]);
            Product selection = (Product) JOptionPane.showInputDialog(this, 
                    "Select product to add (or Cancel to finish):", "Add to Order", 
                    JOptionPane.PLAIN_MESSAGE, null, 
                    productArray, productArray.length > 0 ? productArray[0] : null);
            
            if (selection == null) break;

            String qty = JOptionPane.showInputDialog(this, "Quantity:");
            if (qty == null) break;

            try {
                int quantity = Integer.parseInt(qty);
                if (quantity > 0) {
                    order.addProduct(selection, quantity);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        refreshOrders();
    }

    private void completeOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to complete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String status = (String) orderTableModel.getValueAt(selectedRow, 5);

        if (status.equals("COMPLETED")) {
            JOptionPane.showMessageDialog(this, "This order is already completed", 
                    "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Mark this order as completed?", 
                "Complete Order", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            manager.completeOrder(orderId);
            refreshOrders();
            refreshSalesReport();
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        Order order = manager.getOrderById(orderId);

        if (order != null) {
            StringBuilder details = new StringBuilder();
            details.append("Order ID: ").append(order.getOrderId()).append("\n");
            details.append("Customer: ").append(order.getCustomerName()).append("\n");
            details.append("Status: ").append(order.getStatus()).append("\n");
            details.append("Date: ").append(dateFormat.format(new Date())).append("\n\n");
            details.append("Products:\n");
            
            for (int i = 0; i < order.getProducts().size(); i++) {
                Product p = order.getProducts().get(i);
                int qty = order.getQuantities().get(i);
                details.append(String.format("  - %s x%d @ $%.2f = $%.2f\n", 
                    p.getName(), qty, p.getPrice(), p.getPrice() * qty));
            }
            
            details.append("\nTotal: $").append(String.format("%.2f", order.getTotal()));

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                    "Order Details: " + orderId, 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addToQueue() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to add to the queue", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String status = (String) orderTableModel.getValueAt(selectedRow, 5);
    
        if (status.equals("COMPLETED")) {
            JOptionPane.showMessageDialog(this, "This order is already completed", 
                    "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        if (manager.queueOrderForProcessing(orderId)) {
            refreshQueue();
            JOptionPane.showMessageDialog(this, "Order added to processing queue", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add order to queue", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processNextInQueue() {
        Order processed = manager.processNextOrder();
        
        if (processed != null) {
            refreshAll();
            JOptionPane.showMessageDialog(this, 
                    "Processed order: " + processed.getOrderId(), 
                    "Order Processed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No orders in queue", 
                    "Queue Empty", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processAllInQueue() {
        int count = manager.getOrderQueueSize();
        
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No orders in queue", 
                    "Queue Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Process all " + count + " orders in the queue?", 
                "Confirm", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            int processed = 0;
            while (manager.getOrderQueueSize() > 0) {
                manager.processNextOrder();
                processed++;
            }
            
            refreshAll();
            JOptionPane.showMessageDialog(this, 
                    "Processed " + processed + " orders", 
                    "Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshQueue() {
        queueTableModel.setRowCount(0);
        
        Order[] queuedOrders = manager.getQueuedOrders();
        for (Order order : queuedOrders) {
            queueTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerName(),
                order.getProducts().size(),
                String.format("$%.2f", order.getTotal())
            });
        }
        
        queueCountLabel.setText(String.valueOf(manager.getOrderQueueSize()));
    }
}