import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManagerGUI extends JFrame {
    private double initialWallet = 0;
    private double totalSpent = 0;
    private String history = "";

    private final List<BudgetEntry> budgetEntries = new ArrayList<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private JLabel currentBalanceLabel;
    private JTextArea historyArea;
    private JLabel summaryLabel;

    public ExpenseManagerGUI() {
        setTitle("Expense Manager");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel for initial balance
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Initial Balance:"));
        currentBalanceLabel = new JLabel("₹0.0");
        topPanel.add(currentBalanceLabel);
        JButton setBalanceBtn = new JButton("Set Initial Budget (I)");
        setBalanceBtn.addActionListener(new SetBalanceListener());
        topPanel.add(setBalanceBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for history
        historyArea = new JTextArea(10, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons and summary
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 5, 5));

        JButton addExpenseBtn = new JButton("Add Expense (E)");
        addExpenseBtn.addActionListener(new AddExpenseListener());
        bottomPanel.add(addExpenseBtn);

        JButton viewHistoryBtn = new JButton("View History (H)");
        viewHistoryBtn.addActionListener(new ViewHistoryListener());
        bottomPanel.add(viewHistoryBtn);

        JButton viewSummaryBtn = new JButton("View Summary (S)");
        viewSummaryBtn.addActionListener(new ViewSummaryListener());
        bottomPanel.add(viewSummaryBtn);

        JButton exitBtn = new JButton("Exit (X)");
        exitBtn.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitBtn);

        summaryLabel = new JLabel("Summary will appear here");
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(bottomPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        summaryPanel.add(summaryLabel);
        southPanel.add(summaryPanel);

        add(southPanel, BorderLayout.SOUTH);
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("pressed E"), "addExpense");
        inputMap.put(KeyStroke.getKeyStroke("pressed e"), "addExpense");
        actionMap.put("addExpense", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new AddExpenseListener().actionPerformed(e);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("pressed H"), "viewHistory");
        inputMap.put(KeyStroke.getKeyStroke("pressed h"), "viewHistory");
        actionMap.put("viewHistory", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new ViewHistoryListener().actionPerformed(e);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("pressed S"), "viewSummary");
        inputMap.put(KeyStroke.getKeyStroke("pressed s"), "viewSummary");
        actionMap.put("viewSummary", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new ViewSummaryListener().actionPerformed(e);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("pressed I"), "setInitialBudget");
        inputMap.put(KeyStroke.getKeyStroke("pressed i"), "setInitialBudget");
        actionMap.put("setInitialBudget", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new SetBalanceListener().actionPerformed(e);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("pressed X"), "exitApp");
        inputMap.put(KeyStroke.getKeyStroke("pressed x"), "exitApp");
        actionMap.put("exitApp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private class SetBalanceListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(
                ExpenseManagerGUI.this,
                "Add to initial budget (₹):",
                "Add Initial Budget",
                JOptionPane.PLAIN_MESSAGE
            );
            if (input == null || input.isEmpty()) {
                return;
            }
            try {
                double amount = Double.parseDouble(input);
                initialWallet += amount;
                currentBalanceLabel.setText("₹" + initialWallet);
                LocalDateTime now = LocalDateTime.now();
                budgetEntries.add(new BudgetEntry(now, amount));
                JOptionPane.showMessageDialog(ExpenseManagerGUI.this, "₹" + amount + " added to initial budget.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ExpenseManagerGUI.this, "Invalid number!");
            }
        }
    }

    private class AddExpenseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String note = JOptionPane.showInputDialog("Expense for (e.g., Food, Rent):");
            String amountStr = JOptionPane.showInputDialog("Amount spent (₹):");
            try {
                double amount = Double.parseDouble(amountStr);
                totalSpent += amount;
                history += "- " + note + ": ₹" + amount + "\n";
                JOptionPane.showMessageDialog(null, "₹" + amount + " added for " + note);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid amount!");
            }
        }
    }

    private class ViewHistoryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            double todayBudgetTotal = 0;
            double yesterdayBudgetTotal = 0;

            StringBuilder report = new StringBuilder();
            report.append("Initial Budget Additions:\n");
            if (budgetEntries.isEmpty()) {
                report.append("No initial budget additions yet.\n");
            } else {
                for (BudgetEntry entry : budgetEntries) {
                    report.append(entry.time.format(dtf)).append(" : ₹").append(entry.amount).append("\n");
                    if (entry.time.toLocalDate().equals(today)) {
                        todayBudgetTotal += entry.amount;
                    } else if (entry.time.toLocalDate().equals(yesterday)) {
                        yesterdayBudgetTotal += entry.amount;
                    }
                }
            }

            report.append("\nTotal Initial Budget: ₹").append(initialWallet).append("\n");
            report.append("Today's initial budget: ₹").append(todayBudgetTotal).append("\n");
            report.append("Yesterday's initial budget: ₹").append(yesterdayBudgetTotal).append("\n\n");

            double remaining = initialWallet - totalSpent;
            report.append("Expense History:\n");
            report.append(history.isEmpty() ? "No expense history." : history).append("\n");
            report.append("\nTotal Spent: ₹").append(totalSpent).append("\n");
            report.append("Remaining: ₹").append(remaining >= 0 ? remaining : -remaining).append(remaining >= 0 ? "\n" : " (Over budget)\n");

            historyArea.setText(report.toString());
        }
    }

    private class ViewSummaryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            double remaining = initialWallet - totalSpent;
            String report;
            if (remaining >= 0) {
                report = "Balance: ₹" + remaining + " (Savings: ₹" + remaining + ")";
            } else {
                double debt = Math.abs(remaining);
                report = "Warning: Over budget! Debt: ₹" + debt;
            }
            summaryLabel.setText(report);
            JOptionPane.showMessageDialog(ExpenseManagerGUI.this, report, "Summary", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class BudgetEntry {
        LocalDateTime time;
        double amount;

        BudgetEntry(LocalDateTime time, double amount) {
            this.time = time;
            this.amount = amount;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExpenseManagerGUI().setVisible(true);
        });
    }
}