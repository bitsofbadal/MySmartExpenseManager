import javax.swing.JOptionPane;

public class ExpenseManager {
    static double initialWallet = 0; // Total paise jo starting mein the
    static double totalSpent = 0;    // Total kharcha
    static String history = "";      // Kharchon ki list

    // 1. Method: Starting balance set karne ke liye
    public static void setInitialBalance() {
        String input = JOptionPane.showInputDialog("Aapke paas total kitne paise hain? (Initial Balance):");
        if (input != null && !input.isEmpty()) {
            initialWallet = Double.parseDouble(input);
        }
    }

    // 2. Method: Kharcha add karne ke liye
    public static void addExpense() {
        String note = JOptionPane.showInputDialog("Kharcha kis par kiya? (e.g., Food, Rent):");
        String input = JOptionPane.showInputDialog("Kitne paise kharch kiye? (₹):");
        
        if (input != null && !input.isEmpty()) {
            double amount = Double.parseDouble(input);
            totalSpent += amount;
            history += "- " + note + ": ₹" + amount + "\n";
            JOptionPane.showMessageDialog(null, "₹" + amount + " added for " + note);
        }
    }

    // 3. Method: Detailed Summary (Savings aur Karjaa logic)
    public static void showSummary() {
        double remaining = initialWallet - totalSpent;
        String report = "--- FINAL SUMMARY ---\n";
        report += "Initial Amount: ₹" + initialWallet + "\n";
        report += "Total Spent: ₹" + totalSpent + "\n";
        report += "----------------------\n";

        if (remaining >= 0) {
            report += "Balance Bacha Hai: ₹" + remaining + "\n";
            report += "✅ Aapne ₹" + remaining + " ki savings ki hai!";
        } else {
            double debt = Math.abs(remaining);
            report += "⚠️ Warning: Aap Limit se bahar hain!\n";
            report += "❌ Aapne ₹" + debt + " ka KARJAA (Debt) liya hai!";
        }

        JOptionPane.showMessageDialog(null, report);
    }

    public static void main(String[] args) {
        // Sabse pehle initial balance puchenge
        setInitialBalance();

        while (true) {
            String menu = "=== SMART WALLET MANAGER ===\n" +
                          "1. Add Expense\n" +
                          "2. View Expense History\n" +
                          "3. View Final Summary (Balance/Debt)\n" +
                          "4. Exit\n\n" +
                          "Choose an option:";
            
            String choice = JOptionPane.showInputDialog(menu);
            if (choice == null || choice.equals("4")) break;

            switch (choice) {
                case "1": addExpense(); break;
                case "2": 
                    String list = history.isEmpty() ? "No history." : history;
                    JOptionPane.showMessageDialog(null, "--- HISTORY ---\n" + list);
                    break;
                case "3": showSummary(); break;
                default: JOptionPane.showMessageDialog(null, "Invalid Option!");
            }
        }
    }
}
