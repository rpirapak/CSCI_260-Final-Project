package retailmanagement;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Retail Store Manager Application...");
        SwingUtilities.invokeLater(() -> {
            try {
                MainUI ui = new MainUI();
                ui.setVisible(true);
                System.out.println("Application started successfully!");
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}