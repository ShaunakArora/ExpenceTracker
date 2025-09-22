import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenceTracker extends JFrame {
    // Colors matching the CSS
    private final Color primaryColor = new Color(235, 22, 22);
    private final Color secondaryColor = new Color(25, 28, 36);
    private final Color lightColor = new Color(108, 114, 147);
    private final Color darkColor = new Color(0, 0, 0);
    private final Color bgColor = new Color(25, 28, 36);
    
    // Fonts
    private Font openSans;
    private Font roboto;

    // --- Data and Models ---
    private final java.util.List<Transaction> transactions = new ArrayList<>();
    private DefaultTableModel transactionTableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("en", "IN")); // For ₹

    // --- Dynamic UI Components ---
    private JLabel monthlyExpenseLabel;
    private JLabel transactionCountLabel;
    private JLabel topCategoryLabel;
    private JLabel lastTransactionLabel;

    public ExpenceTracker() {
        // --- Data Initialization ---
        loadInitialData();

        // --- UI Initialization ---
        initializeUI();
    }
    private void initializeUI() {
        try {
            // Load fonts (fallback to sans-serif if not available)
            openSans = new Font("Open Sans", Font.PLAIN, 12);
            roboto = new Font("Roboto", Font.PLAIN, 12);
        } catch (Exception e) {
            openSans = new Font("SansSerif", Font.PLAIN, 12);
            roboto = new Font("SansSerif", Font.PLAIN, 12);
        }

        // Setup main window
        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Main container with dark background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(darkColor);

        // Create the sidebar
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        // Create the content area
        mainPanel.add(createContentArea(), BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Initial data load for UI
        updateDashboard();
    }

    private void loadInitialData() {
        // Dummy data for demonstration
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            transactions.add(new Transaction(sdf.parse("2023-10-25"), "Food", "Groceries", 3250.50));
            transactions.add(new Transaction(sdf.parse("2023-10-24"), "Transport", "Gasoline", 1500.00));
            transactions.add(new Transaction(sdf.parse("2023-10-23"), "Entertainment", "Movie tickets", 800.00));
            
            // Use Calendar for current and past dates
            Calendar cal = Calendar.getInstance();
            transactions.add(new Transaction(cal.getTime(), "Bills", "Electricity Bill", 2500.00)); // Today
            cal.add(Calendar.DATE, -1);
            transactions.add(new Transaction(cal.getTime(), "Food", "Restaurant", 1200.75)); // Yesterday

            transactions.add(new Transaction(sdf.parse("2023-09-15"), "Shopping", "Clothing", 4500.00));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(secondaryColor);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        
        // Brand header
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        brandPanel.setBackground(secondaryColor);
        JLabel brandLabel = new JLabel("Expense Tracker");
        brandLabel.setFont(roboto.deriveFont(Font.BOLD, 18));
        brandLabel.setForeground(primaryColor);
        brandPanel.add(brandLabel);
        brandPanel.setMaximumSize(new Dimension(250, 60));
        sidebar.add(brandPanel);
        
        // User profile
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userPanel.setBackground(secondaryColor);
        
        // User icon (simulated with a circle)
        JPanel userIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(lightColor);
                g.fillOval(0, 0, 40, 40);
                
                // Online status indicator
                g.setColor(Color.GREEN);
                g.fillOval(30, 30, 10, 10);
            }
        };
        userIcon.setPreferredSize(new Dimension(40, 40));
        
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setBackground(secondaryColor);
        JLabel userName = new JLabel("Shaunak Arora");
        userName.setForeground(Color.WHITE);
        userName.setFont(roboto.deriveFont(Font.BOLD, 14));
        JLabel userRole = new JLabel("Admin");
        userRole.setForeground(lightColor);
        userRole.setFont(openSans.deriveFont(12f));
        userInfo.add(userName);
        userInfo.add(userRole);
        
        userPanel.add(userIcon);
        userPanel.add(userInfo);
        userPanel.setMaximumSize(new Dimension(250, 70));
        sidebar.add(userPanel);
        
        // Navigation menu
        String[] menuItems = {
            "Dashboard", "Transactions", "Analytics", "Categories",
            "Budgets", "Settings", "Logout"
        };
        
        String[] menuIcons = {
            "📊", "💻", "🔲", "⌨️", "📋", "📈", "📄"
        };
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuIcons[i] + "  " + menuItems[i]);
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setBackground(secondaryColor);
            menuButton.setForeground(lightColor);
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            menuButton.setFocusPainted(false);
            
            // Highlight the active item (Dashboard)
            if (i == 0) {
                menuButton.setForeground(primaryColor);
                menuButton.setBackground(darkColor);
                menuButton.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, primaryColor));
            }
            
            menuButton.setMaximumSize(new Dimension(250, 45));
            sidebar.add(menuButton);
        }
        
        // Add some spacing at the bottom
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JPanel createContentArea() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(darkColor);
        
        // Create navbar
        contentPanel.add(createNavbar(), BorderLayout.NORTH);
        
        // Create main content with scroll pane
        JPanel mainContent = createMainContent();
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(secondaryColor);
        navbar.setPreferredSize(new Dimension(getWidth(), 60));
        navbar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Left side - toggle button and search
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(secondaryColor);
        
        JButton toggleBtn = new JButton("☰");
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setBackground(secondaryColor);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        toggleBtn.setFocusPainted(false);
        
        JTextField searchField = new JTextField(20);
        searchField.setBackground(darkColor);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setText("Search");
        searchField.setPreferredSize(new Dimension(200, 35));
        
        leftPanel.add(toggleBtn);
        leftPanel.add(searchField);
        
        // Right side - icons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(secondaryColor);
        
        // Message dropdown button
        JButton messageBtn = createIconButton("✉️", "Message");
        
        // Notification dropdown button
        JButton notificationBtn = createIconButton("🔔", "Notification");
        
        // User profile dropdown
        JButton userBtn = new JButton();
        userBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        userBtn.setBackground(secondaryColor);
        userBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // User icon
        JPanel navUserIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(lightColor);
                g.fillOval(0, 0, 30, 30);
            }
        };
        navUserIcon.setPreferredSize(new Dimension(30, 30));
        
        JLabel navUserName = new JLabel("Shaunak Arora");
        navUserName.setForeground(Color.WHITE);
        
        userBtn.add(navUserIcon);
        userBtn.add(navUserName);
        
        rightPanel.add(messageBtn);
        rightPanel.add(notificationBtn);
        rightPanel.add(userBtn);
        
        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(rightPanel, BorderLayout.EAST);
        
        return navbar;
    }
    
    private JButton createIconButton(String icon, String text) {
        JButton button = new JButton(icon + " " + text);
        button.setForeground(Color.WHITE);
        button.setBackground(secondaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }
    
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(darkColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add stats cards
        mainContent.add(createStatsCards());
        
        // Add charts section
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createChartsSection());
        
        // Add recent sales section
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createTransactionsPanel());
        
        // Add widgets section
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createWidgetsSection());
        
        // Add footer
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(createFooter());
        
        return mainContent;
    }
    
    private JPanel createStatsCards() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(darkColor);

        String[] titles = {"Current Month Expense", "Transaction Count", "Top Spending Category", "Last Transaction"};
        String[] icons = {"💰", "🧾", "🏆", "🕒"};

        monthlyExpenseLabel = new JLabel("₹0.00");
        transactionCountLabel = new JLabel("0");
        topCategoryLabel = new JLabel("N/A");
        lastTransactionLabel = new JLabel("₹0.00");
        
        for (int i = 0; i < 4; i++) {
            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(secondaryColor);
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JLabel iconLabel = new JLabel(icons[i]);
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
            iconLabel.setForeground(primaryColor);
            
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setBackground(secondaryColor);
            
            JLabel titleLabel = new JLabel(titles[i]);
            titleLabel.setForeground(lightColor);
            titleLabel.setFont(openSans.deriveFont(14f));
            
            JLabel valueLabel;
            switch (i) {
                case 0: valueLabel = monthlyExpenseLabel; break;
                case 1: valueLabel = transactionCountLabel; break;
                case 2: valueLabel = topCategoryLabel; break;
                case 3: valueLabel = lastTransactionLabel; break;
                default: valueLabel = new JLabel("N/A");
            }

            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(roboto.deriveFont(Font.BOLD, 18));
            
            textPanel.add(titleLabel);
            textPanel.add(valueLabel);

            // Align icon to the top
            card.add(iconLabel, BorderLayout.NORTH);
            
            card.add(iconLabel, BorderLayout.WEST);
            card.add(textPanel, BorderLayout.CENTER);
            
            statsPanel.add(card);
        }
        
        return statsPanel;
    }
    
    private JPanel createChartsSection() {
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setBackground(darkColor);
        
        // Create two chart panels
        for (int i = 0; i < 2; i++) {
            JPanel chartCard = new JPanel(new BorderLayout());
            chartCard.setBackground(secondaryColor);
            chartCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(secondaryColor);
            
            JLabel title = new JLabel(i == 0 ? "Monthly Spending" : "Spending by Category");
            title.setForeground(Color.WHITE);
            title.setFont(roboto.deriveFont(Font.BOLD, 16));
            
            JLabel showAll = new JLabel("Show All");
            showAll.setForeground(primaryColor);
            showAll.setFont(openSans.deriveFont(14f));
            
            header.add(title, BorderLayout.WEST);
            header.add(showAll, BorderLayout.EAST);
            
            // Placeholder for chart
            JPanel chartPlaceholder = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(darkColor);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw some sample chart elements
                    g.setColor(primaryColor);
                    int[] data = {30, 45, 60, 35, 70, 40, 55};
                    int barWidth = getWidth() / (data.length * 2);
                    
                    for (int i = 0; i < data.length; i++) {
                        int barHeight = (int) (data[i] / 100.0 * getHeight());
                        g.fillRect(i * (barWidth * 2) + barWidth/2, getHeight() - barHeight, barWidth, barHeight);
                    }
                }
            };
            chartPlaceholder.setPreferredSize(new Dimension(300, 200));
            
            chartCard.add(header, BorderLayout.NORTH);
            chartCard.add(chartPlaceholder, BorderLayout.CENTER);
            
            chartsPanel.add(chartCard);
        }
        
        return chartsPanel;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBackground(secondaryColor);
        salesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Header and Filters ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(secondaryColor);

        JLabel title = new JLabel("Recent Transactions");
        title.setForeground(Color.WHITE);
        title.setFont(roboto.deriveFont(Font.BOLD, 16));

        // Filter and Action Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(secondaryColor);
        JTextField filterField = new JTextField("Filter by category...", 15);
        JButton filterButton = new JButton("Filter");
        JButton addButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Selected");

        actionsPanel.add(filterField);
        actionsPanel.add(filterButton);
        actionsPanel.add(addButton);
        actionsPanel.add(editButton);

        header.add(title, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Date", "Category", "Description", "Amount"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable table = new JTable(transactionTableModel);
        table.setBackground(secondaryColor);
        table.setForeground(Color.WHITE);
        table.setGridColor(lightColor);
        table.setFont(openSans.deriveFont(14f));
        table.setRowHeight(30);
        table.getTableHeader().setBackground(darkColor);
        table.getTableHeader().setForeground(primaryColor);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(secondaryColor);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // --- Action Listeners ---
        addButton.addActionListener(e -> showAddEditTransactionDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                showAddEditTransactionDialog(transactions.get(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        filterButton.addActionListener(e -> {
            String filterText = filterField.getText().trim();
            updateTransactionTable(filterText.equalsIgnoreCase("Filter by category...") ? "" : filterText);
        });

        salesPanel.add(header, BorderLayout.NORTH);
        salesPanel.add(scrollPane, BorderLayout.CENTER);

        return salesPanel;
    }

    private void updateTransactionTable(String categoryFilter) {
        transactionTableModel.setRowCount(0); // Clear table
        for (Transaction t : transactions) {
            if (categoryFilter.isEmpty() || t.category.equalsIgnoreCase(categoryFilter)) {
                transactionTableModel.addRow(new Object[]{
                        dateFormat.format(t.date),
                        t.category,
                        t.description,
                        currencyFormat.format(t.amount)
                });
            }
        }
    }

    private void showAddEditTransactionDialog(Transaction transaction) {
        // For simplicity, using JOptionPane. A custom JDialog would be better.
        String description = JOptionPane.showInputDialog(this, "Enter Description:", transaction != null ? transaction.description : "");
        String amountStr = JOptionPane.showInputDialog(this, "Enter Amount:", transaction != null ? transaction.amount : "");
        String category = JOptionPane.showInputDialog(this, "Enter Category:", transaction != null ? transaction.category : "");

        // Basic validation and update/add logic would go here.
        // This is a placeholder for a more complex dialog.
        JOptionPane.showMessageDialog(this, "Transaction logic not fully implemented in this demo.", "Info", JOptionPane.INFORMATION_MESSAGE);
        // After logic, call updateDashboard()
    }
    
    private JPanel createWidgetsSection() {
        JPanel widgetsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        widgetsPanel.setBackground(darkColor);
        
        // Messages widget
        widgetsPanel.add(createMessagesWidget());
        
        // Calendar widget
        widgetsPanel.add(createCalendarWidget());
        
        // Todo list widget
        widgetsPanel.add(createTodoWidget());
        
        return widgetsPanel;
    }
    
    private JPanel createMessagesWidget() {
        JPanel messagesPanel = new JPanel(new BorderLayout());
        messagesPanel.setBackground(secondaryColor);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(secondaryColor);
        
        JLabel title = new JLabel("Messages");
        title.setForeground(Color.WHITE);
        title.setFont(roboto.deriveFont(Font.BOLD, 16));
        
        JLabel showAll = new JLabel("Show All");
        showAll.setForeground(primaryColor);
        showAll.setFont(openSans.deriveFont(14f));
        
        header.add(title, BorderLayout.WEST);
        header.add(showAll, BorderLayout.EAST);
        
        JPanel messagesList = new JPanel();
        messagesList.setLayout(new BoxLayout(messagesList, BoxLayout.Y_AXIS));
        messagesList.setBackground(secondaryColor);
        
        // Add sample messages
        for (int i = 0; i < 4; i++) {
            JPanel message = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            message.setBackground(secondaryColor);
            message.setMaximumSize(new Dimension(300, 70));
            
            // User icon
            JPanel userIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(lightColor);
                    g.fillOval(0, 0, 40, 40);
                }
            };
            userIcon.setPreferredSize(new Dimension(40, 40));
            
            JPanel messageContent = new JPanel(new GridLayout(2, 1));
            messageContent.setBackground(secondaryColor);
            
            JPanel messageHeader = new JPanel(new BorderLayout());
            messageHeader.setBackground(secondaryColor);
            
            JLabel userName = new JLabel("System Alert");
            userName.setForeground(Color.WHITE);
            userName.setFont(roboto.deriveFont(Font.BOLD, 14));
            
            JLabel time = new JLabel("15 minutes ago");
            time.setForeground(lightColor);
            time.setFont(openSans.deriveFont(12f));
            
            messageHeader.add(userName, BorderLayout.WEST);
            messageHeader.add(time, BorderLayout.EAST);
            
            JLabel messageText = new JLabel("Your electricity bill is due.");
            messageText.setForeground(lightColor);
            messageText.setFont(openSans.deriveFont(14f));
            
            messageContent.add(messageHeader);
            messageContent.add(messageText);
            
            message.add(userIcon);
            message.add(messageContent);
            
            messagesList.add(message);
            
            // Add separator except for last item
            if (i < 3) {
                messagesList.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }
        
        messagesPanel.add(header, BorderLayout.NORTH);
        messagesPanel.add(messagesList, BorderLayout.CENTER);
        
        return messagesPanel;
    }
    
    private JPanel createCalendarWidget() {
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(secondaryColor);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(secondaryColor);
        
        JLabel title = new JLabel("Calendar");
        title.setForeground(Color.WHITE);
        title.setFont(roboto.deriveFont(Font.BOLD, 16));
        
        JLabel showAll = new JLabel("Show All");
        showAll.setForeground(primaryColor);
        showAll.setFont(openSans.deriveFont(14f));
        
        header.add(title, BorderLayout.WEST);
        header.add(showAll, BorderLayout.EAST);
        
        // Simple calendar placeholder
        JPanel calendar = new JPanel(new GridLayout(0, 7, 5, 5));
        calendar.setBackground(secondaryColor);
        
        // Day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(primaryColor);
            dayLabel.setFont(roboto.deriveFont(Font.BOLD, 12));
            calendar.add(dayLabel);
        }
        
        // Add day numbers
        for (int i = 1; i <= 31; i++) {
            JLabel dayNum = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            dayNum.setForeground(Color.WHITE);
            dayNum.setFont(openSans.deriveFont(12f));
            
            // Highlight current day
            if (i == 15) {
                dayNum.setOpaque(true);
                dayNum.setBackground(primaryColor);
                dayNum.setForeground(Color.WHITE);
            }
            
            calendar.add(dayNum);
        }
        
        calendarPanel.add(header, BorderLayout.NORTH);
        calendarPanel.add(calendar, BorderLayout.CENTER);
        
        return calendarPanel;
    }
    
    private JPanel createTodoWidget() {
        JPanel todoPanel = new JPanel(new BorderLayout());
        todoPanel.setBackground(secondaryColor);
        todoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(secondaryColor);
        
        JLabel title = new JLabel("To Do List");
        title.setForeground(Color.WHITE);
        title.setFont(roboto.deriveFont(Font.BOLD, 16));
        
        JLabel showAll = new JLabel("Show All");
        showAll.setForeground(primaryColor);
        showAll.setFont(openSans.deriveFont(14f));
        
        header.add(title, BorderLayout.WEST);
        header.add(showAll, BorderLayout.EAST);
        
        // Add task input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(secondaryColor);
        
        JTextField taskInput = new JTextField("Enter task");
        taskInput.setBackground(darkColor);
        taskInput.setForeground(Color.WHITE);
        taskInput.setCaretColor(Color.WHITE);
        
        JButton addButton = new JButton("Add");
        addButton.setBackground(primaryColor);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        // Todo list
        JPanel todoList = new JPanel();
        todoList.setLayout(new BoxLayout(todoList, BoxLayout.Y_AXIS));
        todoList.setBackground(secondaryColor);
        
        String[] tasks = {
            "Short task goes here...",
            "Short task goes here...",
            "Short task goes here...",
            "Short task goes here...",
            "Short task goes here..."
        };
        
        boolean[] completed = {false, false, true, false, false};
        
        for (int i = 0; i < tasks.length; i++) {
            JPanel taskItem = new JPanel(new BorderLayout(10, 0));
            taskItem.setBackground(secondaryColor);
            taskItem.setMaximumSize(new Dimension(300, 40));
            
            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(secondaryColor);
            checkBox.setSelected(completed[i]);
            
            JLabel taskText = new JLabel(tasks[i]);
            taskText.setForeground(Color.WHITE);
            if (completed[i]) {
                taskText.setText("<html><strike>" + tasks[i] + "</strike></html>");
                taskText.setForeground(lightColor);
            }
            
            JButton deleteBtn = new JButton("×");
            deleteBtn.setBackground(secondaryColor);
            deleteBtn.setForeground(primaryColor);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder());
            deleteBtn.setFocusPainted(false);
            
            taskItem.add(checkBox, BorderLayout.WEST);
            taskItem.add(taskText, BorderLayout.CENTER);
            taskItem.add(deleteBtn, BorderLayout.EAST);
            
            todoList.add(taskItem);
            
            // Add separator except for last item
            if (i < tasks.length - 1) {
                todoList.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }
        
        todoPanel.add(header, BorderLayout.NORTH);
        todoPanel.add(inputPanel, BorderLayout.CENTER);
        todoPanel.add(todoList, BorderLayout.SOUTH);
        
        return todoPanel;
    }
    
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(secondaryColor);
        footer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel leftText = new JLabel("© Your Site Name, All Right Reserved.");
        leftText.setForeground(lightColor);
        
        JLabel rightText = new JLabel("Designed By HTML Codex");
        rightText.setForeground(lightColor);
        
        footer.add(leftText, BorderLayout.WEST);
        footer.add(rightText, BorderLayout.EAST);
        
        return footer;
    }
    
    private void updateDashboard() {
        // 1. Update Stats Cards
        double monthTotal = 0;
        Map<String, Double> categoryTotals = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);

        for (Transaction t : transactions) {
            cal.setTime(t.date);
            if (cal.get(Calendar.MONTH) == currentMonth) {
                monthTotal += t.amount;
            }
            categoryTotals.put(t.category, categoryTotals.getOrDefault(t.category, 0.0) + t.amount);
        }

        monthlyExpenseLabel.setText(currencyFormat.format(monthTotal));
        transactionCountLabel.setText(String.valueOf(transactions.size()));

        // Find top category
        String topCategory = "N/A";
        double maxAmount = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }
        topCategoryLabel.setText(topCategory);

        // Last transaction
        if (!transactions.isEmpty()) {
            transactions.sort(Comparator.comparing((Transaction t) -> t.date).reversed());
            lastTransactionLabel.setText(currencyFormat.format(transactions.get(0).amount));
        } else {
            lastTransactionLabel.setText("N/A");
        }

        // 2. Update Transaction Table
        updateTransactionTable("");
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

    // Simple data class for a transaction
    private static class Transaction {
        Date date;
        String category;
        String description;
        double amount;

        public Transaction(Date date, String category, String description, double amount) {
            this.date = date;
            this.category = category;
            this.description = description;
            this.amount = amount;
        }
    }

    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            if (showLoginDialog()) {
                ExpenceTracker dashboard = new ExpenceTracker();
                dashboard.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}