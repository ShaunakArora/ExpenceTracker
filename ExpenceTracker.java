import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A professional, user-friendly Daily Expense Tracker application.
 * It helps users monitor their financial inflows and outflows with a clean,
 * high-contrast black and red theme.
 */
public class ExpenceTracker extends JFrame {
    // Colors matching the CSS
    private final Color primaryRed = new Color(229, 57, 53); // A vibrant red
    private final Color backgroundDark = new Color(30, 30, 30);
    private final Color componentDark = new Color(45, 45, 45);
    private final Color textLight = new Color(224, 224, 224);
    private final Color textMuted = new Color(158, 158, 158);
    private final Color incomeGreen = new Color(102, 187, 106);

    // Transaction Types (made public for access by other classes)
    public enum TransactionType { INCOME, EXPENSE }

    // Fonts
    private Font openSans;
    private Font roboto;

    // --- Data and Models ---
    private final java.util.List<Transaction> transactions = new ArrayList<>();
    private DefaultTableModel transactionTableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("en", "IN")); // For ₹

    // --- Dynamic UI Components ---
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private JTabbedPane tabbedPane;
    private DataVisualizationPanel dataVisualizationPanel;

    public ExpenceTracker() {
        // --- Data Initialization ---
        loadInitialData();

        // --- UI Initialization ---
        initializeUI();
    }
    private void initializeUI() {
        openSans = new Font("SansSerif", Font.PLAIN, 14);
        roboto = new Font("SansSerif", Font.BOLD, 16);

        // Setup main window
        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create and set the menu bar
        setJMenuBar(createMenuBar());

        // Create the main content area with tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(backgroundDark);
        tabbedPane.setForeground(textLight);
        tabbedPane.setFont(openSans);

        dataVisualizationPanel = new DataVisualizationPanel(transactions);
        tabbedPane.addTab("Transactions", createTransactionsPanel());
        tabbedPane.addTab("Dashboard", dataVisualizationPanel);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createActionBar(), BorderLayout.SOUTH);

        // Initial data load for UI
        updateDashboard();
    }

    private void loadInitialData() {
        // Dummy data for demonstration
        transactions.add(new Transaction(new Date(), "Salary", "Monthly Paycheck", 75000.00, TransactionType.INCOME));
        transactions.add(new Transaction(new Date(), "Rent", "Monthly Rent", 20000.00, TransactionType.EXPENSE));
        transactions.add(new Transaction(new Date(), "Food", "Groceries", 8500.50, TransactionType.EXPENSE));
        transactions.add(new Transaction(new Date(), "Transport", "Gasoline", 3000.00, TransactionType.EXPENSE));
        transactions.add(new Transaction(new Date(), "Entertainment", "Movie tickets", 1200.00, TransactionType.EXPENSE));
        transactions.add(new Transaction(getPastDate(5), "Freelance", "Project Work", 15000.00, TransactionType.INCOME));
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        return null; // This method is replaced
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(componentDark);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.BLACK);
        fileMenu.setFont(roboto.deriveFont(14f));
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        exportItem.addActionListener(e -> exportTransactionsToCSV());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setForeground(Color.BLACK);
        editMenu.setFont(roboto.deriveFont(14f));
        JMenuItem addItem = new JMenuItem("Add Transaction");
        addItem.addActionListener(e -> showAddEditTransactionDialog(null));
        JMenuItem editItem = new JMenuItem("Edit Selected");
        editItem.addActionListener(e -> editSelectedTransaction());
        JMenuItem deleteItem = new JMenuItem("Delete Selected");
        deleteItem.addActionListener(e -> deleteSelectedTransaction());
        editMenu.add(addItem);
        editMenu.add(editItem);
        editMenu.add(deleteItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.BLACK);
        helpMenu.setFont(roboto.deriveFont(14f));
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        JMenu chartsMenu = new JMenu("Charts");
        chartsMenu.setForeground(Color.BLACK);
        chartsMenu.setFont(roboto.deriveFont(14f));
        JMenuItem addChartItem = new JMenuItem("Add Chart");
        addChartItem.addActionListener(e -> manageChart("add"));
        JMenuItem editChartItem = new JMenuItem("Edit Chart");
        editChartItem.addActionListener(e -> manageChart("edit"));
        JMenuItem deleteChartItem = new JMenuItem("Delete Chart");
        deleteChartItem.addActionListener(e -> manageChart("delete"));

        chartsMenu.add(addChartItem);
        chartsMenu.add(editChartItem);
        chartsMenu.add(deleteChartItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(chartsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(componentDark);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Initialize labels
        totalIncomeLabel = createStatsValueLabel();
        totalExpenseLabel = createStatsValueLabel();
        balanceLabel = createStatsValueLabel();

        // Balance Panel (most prominent)
        JPanel balancePanel = createHeaderCard("Current Balance", balanceLabel);
        balanceLabel.setFont(roboto.deriveFont(Font.BOLD, 32f));

        // Income Panel
        JPanel incomePanel = createHeaderCard("Total Income", totalIncomeLabel);

        // Expense Panel
        JPanel expensePanel = createHeaderCard("Total Expense", totalExpenseLabel);

        headerPanel.add(incomePanel);
        headerPanel.add(balancePanel);
        headerPanel.add(expensePanel);

        return headerPanel;
    }

    private JPanel createHeaderCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(componentDark);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(openSans.deriveFont(16f));
        titleLabel.setForeground(textMuted);

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createStatsValueLabel() {
        JLabel label = new JLabel("₹0.00");
        label.setForeground(textLight);
        label.setFont(roboto.deriveFont(Font.BOLD, 24f));
        return label;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundDark);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Create table
        String[] columns = {"Date", "Type", "Category", "Description", "Amount"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Custom renderer to color rows based on transaction type
        transactionTable = new JTable(transactionTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Color the 'Amount' column based on transaction type
                    Transaction t = transactions.get(convertRowIndexToModel(row));
                    c.setForeground(textLight);
                    if (column == 4) { // Amount column
                        c.setForeground(t.type == TransactionType.INCOME ? incomeGreen : primaryRed);
                    }
                }
                return c;
            }
        };

        transactionTable.setBackground(backgroundDark);
        transactionTable.setForeground(textLight);
        transactionTable.setGridColor(componentDark);
        transactionTable.setFont(openSans);
        transactionTable.setRowHeight(35);
        transactionTable.setSelectionBackground(primaryRed);
        transactionTable.setSelectionForeground(textLight);
        transactionTable.getTableHeader().setBackground(componentDark);
        transactionTable.getTableHeader().setForeground(textMuted);
        transactionTable.getTableHeader().setFont(roboto.deriveFont(14f));
        transactionTable.getTableHeader().setBorder(BorderFactory.createLineBorder(componentDark));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.getViewport().setBackground(backgroundDark);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionBar() {
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionBar.setBackground(componentDark);

        JButton addButton = createActionButton("Add Transaction");
        addButton.addActionListener(e -> showAddEditTransactionDialog(null));

        JButton editButton = createActionButton("Edit Selected");
        editButton.addActionListener(e -> editSelectedTransaction());

        JButton deleteButton = createActionButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedTransaction());

        JButton exportButton = createActionButton("Export to CSV");
        exportButton.addActionListener(e -> exportTransactionsToCSV());

        actionBar.add(addButton);
        actionBar.add(editButton);
        actionBar.add(deleteButton);
        actionBar.add(exportButton);

        return actionBar;
    }

    private void updateTransactionTable(String categoryFilter) {
        transactionTableModel.setRowCount(0); // Clear table
        // Sort transactions by date descending
        transactions.sort(Comparator.comparing((Transaction t) -> t.date).reversed());

        for (Transaction t : transactions) {
            if (categoryFilter.isEmpty() || t.category.equalsIgnoreCase(categoryFilter)) {
                transactionTableModel.addRow(new Object[] {
                        dateFormat.format(t.date),
                        t.type,
                        t.category,
                        t.description,
                        currencyFormat.format(t.amount)
                });
            }
        }
    }

    private void showAddEditTransactionDialog(Transaction transaction) {
        AddEditTransactionDialog dialog = new AddEditTransactionDialog(this, transaction);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            Transaction newTransaction = dialog.getTransaction();
            if (transaction == null) { // Add new
                transactions.add(newTransaction);
            } else { // Edit existing
                transaction.date = newTransaction.date;
                transaction.type = newTransaction.type;
                transaction.category = newTransaction.category;
                transaction.description = newTransaction.description;
                transaction.amount = newTransaction.amount;
            }
            updateDashboard();
        }
    }
    
    private void updateDashboard() {
        // 1. Update Stats Cards
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.type == TransactionType.INCOME) {
                totalIncome += t.amount;
            } else {
                totalExpense += t.amount;
            }
        }

        double balance = totalIncome - totalExpense;

        totalIncomeLabel.setText(currencyFormat.format(totalIncome));
        totalIncomeLabel.setForeground(incomeGreen);

        totalExpenseLabel.setText(currencyFormat.format(totalExpense));
        totalExpenseLabel.setForeground(primaryRed);

        balanceLabel.setText(currencyFormat.format(balance));
        balanceLabel.setForeground(balance >= 0 ? textLight : primaryRed);

        // 2. Update Transaction Table
        updateTransactionTable(""); // "" means no filter

        // 3. Update Visualization Panel
        dataVisualizationPanel.updateData(transactions);
    }

    private static boolean showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("<html><b>Note:</b> This is a simulated login for a UI demo.<br>A real application would use a backend with Spring Security.</html>"));
        panel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField("demo");
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField("password");
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login - Expense Tracker",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        return result == JOptionPane.OK_OPTION;
    }

    private void editSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            // convertRowIndexToModel is important if the table is sorted/filtered
            int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
            showAddEditTransactionDialog(transactions.get(modelRow));
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this transaction?", "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                transactions.remove(modelRow);
                updateDashboard();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportTransactionsToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        fileChooser.setSelectedFile(new File("transactions.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write header
                writer.append("Date,Type,Category,Description,Amount\n");
                // Write data
                for (Transaction t : transactions) {
                    writer.append(new SimpleDateFormat("yyyy-MM-dd").format(t.date)).append(",");
                    writer.append(t.type.toString()).append(",");
                    writer.append("\"").append(t.category).append("\",");
                    writer.append("\"").append(t.description).append("\",");
                    writer.append(String.valueOf(t.amount)).append("\n");
                }
                JOptionPane.showMessageDialog(this, "Successfully exported to " + fileToSave.getName(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Daily Expense Tracker v1.0\nCreated to help manage personal finances.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void manageChart(String action) {
        // Switch to the dashboard tab to make it clear the action relates to charts
        tabbedPane.setSelectedIndex(1); // 0 is Transactions, 1 is Dashboard

        String message;
        switch (action) {
            case "add":
                message = "Functionality to add a new chart is not yet implemented.";
                break;
            case "edit":
                message = "Functionality to edit the current chart is not yet implemented.";
                break;
            default:
                message = "Functionality to delete a chart is not yet implemented.";
                break;
        }
        JOptionPane.showMessageDialog(this, message, "In Progress", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(primaryRed);
        button.setForeground(textLight);
        button.setFont(roboto.deriveFont(14f));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        return button;
    }

    private static Date getPastDate(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysAgo);
        return cal.getTime();
    }

    // Simple data class for a transaction
    public static class Transaction {
        Date date;
        String category;
        String description;
        double amount;
        TransactionType type;

        public Transaction(Date date, String category, String description, double amount, TransactionType type) {
            this.date = date;
            this.category = category;
            this.description = description;
            this.amount = amount;
            this.type = type;
        }
    }

    // A custom dialog for adding/editing transactions
    private class AddEditTransactionDialog extends JDialog {
        private boolean succeeded;
        private final JComboBox<TransactionType> typeComboBox;
        private final JTextField categoryField;
        private final JTextField descriptionField;
        private final JTextField amountField;
        // Using a more modern date picker would require an external library like JDatePicker.
        // For a self-contained Swing solution, we'll use formatted text fields.
        private final JFormattedTextField dateField;

        public AddEditTransactionDialog(Frame parent, Transaction transaction) {
            super(parent, true);
            setTitle(transaction == null ? "Add Transaction" : "Edit Transaction");

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(backgroundDark);
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 5, 8, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // --- Fields ---
            dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
            typeComboBox = new JComboBox<>(TransactionType.values());
            categoryField = new JTextField(20);
            descriptionField = new JTextField(20);
            amountField = new JTextField(20);

            // --- Populate fields if editing ---
            if (transaction != null) {
                dateField.setValue(transaction.date);
                typeComboBox.setSelectedItem(transaction.type);
                categoryField.setText(transaction.category);
                descriptionField.setText(transaction.description);
                amountField.setText(String.valueOf(transaction.amount));
            } else {
                dateField.setValue(new Date()); // Default to today
            }

            // --- Layout ---
            gbc.gridx = 0; gbc.gridy = 0; panel.add(createDialogLabel("Date (yyyy-MM-dd):"), gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(styleTextField(dateField), gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(createDialogLabel("Type:"), gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(styleComboBox(typeComboBox), gbc);
            gbc.gridx = 0; gbc.gridy = 2; panel.add(createDialogLabel("Category:"), gbc);
            gbc.gridx = 1; gbc.gridy = 2; panel.add(styleTextField(categoryField), gbc);
            gbc.gridx = 0; gbc.gridy = 3; panel.add(createDialogLabel("Description:"), gbc);
            gbc.gridx = 1; gbc.gridy = 3; panel.add(styleTextField(descriptionField), gbc);
            gbc.gridx = 0; gbc.gridy = 4; panel.add(createDialogLabel("Amount:"), gbc);
            gbc.gridx = 1; gbc.gridy = 4; panel.add(styleTextField(amountField), gbc);

            // --- Buttons ---
            JButton saveButton = createActionButton("Save");
            saveButton.addActionListener(e -> onOK());

            JButton cancelButton = new JButton("Cancel");
            // Style the cancel button to match the theme
            cancelButton.setBackground(componentDark);
            cancelButton.setForeground(textLight);
            cancelButton.setFont(roboto.deriveFont(14f));
            cancelButton.setFocusPainted(false);
            cancelButton.setBorder(new EmptyBorder(8, 15, 8, 15));
            cancelButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(backgroundDark);
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            getContentPane().add(panel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(parent);
        }

        private void onOK() {
            try {
                // Basic validation
                if (categoryField.getText().trim().isEmpty() || amountField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Category and Amount cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Double.parseDouble(amountField.getText()); // Check if amount is a valid number
                succeeded = true;
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSucceeded() { return succeeded; }

        public Transaction getTransaction() {
            try {
                Date date = (Date) dateField.getValue();
                TransactionType type = (TransactionType) typeComboBox.getSelectedItem();
                String category = categoryField.getText().trim();
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                return new Transaction(date, category, description, amount, type);
            } catch (Exception e) {
                // This should not happen due to validation in onOK()
                e.printStackTrace();
                return null;
            }
        }

        private JLabel createDialogLabel(String text) { JLabel label = new JLabel(text); label.setForeground(textMuted); return label; }
        private JComponent styleTextField(JComponent field) { field.setForeground(textLight); field.setBackground(componentDark); field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(textMuted), new EmptyBorder(5, 5, 5, 5))); return field; }
        private JComponent styleComboBox(JComboBox<?> box) { box.setForeground(textLight); box.setBackground(componentDark); return styleTextField(box); }
    }


    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ExpenceTracker app = new ExpenceTracker();
            app.setVisible(true);
        });
    }
}
