package ksk_car3;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// POS window: sidebar on the left, active screen on the right.
public class POSDashboard extends JFrame {

    private static final String CARD_NEW_RENTAL = "NEW_RENTAL";
    private static final String CARD_HISTORY = "HISTORY";

    private final CarRentalManager manager;
    private final User user;
    private JLabel avatarLbl, nameLbl, roleLbl;
    private JLabel revenueStat, overdueStat;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private NewRentalPanel newRentalPanel;
    private TransactionHistoryPanel transactionHistoryPanel;

    private JPanel navNewRental;
    private JPanel navHistory;
    private JLabel availableStat;
    private JLabel rentedStat;
    private JLabel todayTxnStat;

    public POSDashboard(CarRentalManager manager, User user) {
        this.manager = manager;
        this.user = user;

        setTitle("SKS CAR - Rental Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1170, 730);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        getContentPane().setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);

        showNewRental();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, Theme.BORDER));

        sidebar.add(buildLogoAndNav(), BorderLayout.NORTH);
        sidebar.add(buildStatsCard(), BorderLayout.CENTER);
        sidebar.add(buildUserFooter(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildLogoAndNav() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logo = new JLabel("SKS CAR");
        logo.setFont(Theme.FONT_LOGO);
        logo.setForeground(new Color(200, 40, 50));
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel consoleLabel = new JLabel("Rental Console");
        consoleLabel.setFont(Theme.FONT_SMALL);
        consoleLabel.setForeground(Theme.TEXT_GRAY);
        consoleLabel.setAlignmentX(LEFT_ALIGNMENT);
        consoleLabel.setBorder(new EmptyBorder(2, 0, 26, 0));

        navNewRental = buildNavItem("+  New Rental", true, this::showNewRental);
        navHistory = buildNavItem("\u2611  Transaction History", false, this::showHistory);
        navNewRental.setAlignmentX(LEFT_ALIGNMENT);
        navHistory.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(logo);
        panel.add(consoleLabel);
        panel.add(navNewRental);
        panel.add(Box.createVerticalStrut(8));
        panel.add(navHistory);
        return panel;
    }

    private JPanel buildNavItem(String label, boolean active, Runnable onClick) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(true);
        item.setBackground(active ? Theme.ACCENT_BLUE : Theme.BG_SIDEBAR);
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel text = new JLabel(label);
        text.setFont(Theme.FONT_BODY_BOLD);
        text.setForeground(Theme.TEXT_WHITE);
        item.add(text, BorderLayout.WEST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return item;
    }

    private void setActiveNav(JPanel active) {
        navNewRental.setBackground(navNewRental == active ? Theme.ACCENT_BLUE : Theme.BG_SIDEBAR);
        navHistory.setBackground(navHistory == active ? Theme.ACCENT_BLUE : Theme.BG_SIDEBAR);
    }

    private JPanel buildStatsCard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 0, 20));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel todayLabel = Theme.createSectionLabel("TODAY");
        todayLabel.setAlignmentX(LEFT_ALIGNMENT);
        todayLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        availableStat = buildStatRow("Available Cars", "0");
        rentedStat = buildStatRow("Active Rentals", "0");
        todayTxnStat = buildStatRow("Today's Transactions", "0");
        revenueStat = buildStatRow("Today's Revenue", "0");
        overdueStat = buildStatRow("Overdue Rentals", "0");

        card.add(todayLabel);
        card.add(availableStat.getParent());
        card.add(Box.createVerticalStrut(8));
        card.add(rentedStat.getParent());
        card.add(Box.createVerticalStrut(8));
        card.add(todayTxnStat.getParent());
        card.add(Box.createVerticalStrut(8));
        card.add(revenueStat.getParent());
        card.add(Box.createVerticalStrut(8));
        card.add(overdueStat.getParent());

        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }

    // Returns the value label; its parent is the row.
    private JLabel buildStatRow(String label, String initialValue) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.FONT_SMALL);
        labelComp.setForeground(Theme.TEXT_GRAY);

        JLabel valueComp = new JLabel(initialValue);
        valueComp.setFont(Theme.FONT_BODY_BOLD);
        valueComp.setForeground(Theme.TEXT_WHITE);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        return valueComp;
    }

    private JPanel buildUserFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER),
                new EmptyBorder(16, 20, 20, 20)));

        JPanel userRow = new JPanel(new BorderLayout(10, 0));
        userRow.setOpaque(false);
        userRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel avatar = new JLabel(initials(user.getFullName()), SwingConstants.CENTER);
        avatarLbl = avatar;
        avatar.setOpaque(true);
        avatar.setBackground(Theme.ACCENT_BLUE);
        avatar.setForeground(Theme.TEXT_WHITE);
        avatar.setFont(Theme.FONT_BODY_BOLD);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel nameStack = new JPanel();
        nameStack.setOpaque(false);
        nameStack.setLayout(new BoxLayout(nameStack, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(user.getFullName());
        nameLbl = nameLabel;
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_WHITE);
        JLabel roleLabel = new JLabel(user.getRole());
        roleLbl = roleLabel;
        roleLabel.setFont(Theme.FONT_SMALL);
        roleLabel.setForeground(Theme.TEXT_GRAY);
        nameStack.add(nameLabel);
        nameStack.add(roleLabel);

        userRow.add(avatar, BorderLayout.WEST);
        userRow.add(nameStack, BorderLayout.CENTER);

        // Click your name to open My Profile.
        userRow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userRow.setToolTipText("Edit profile");
        userRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProfileDialog(POSDashboard.this, user, POSDashboard.this::refreshProfile).setVisible(true);
            }
        });

        JPanel logoutItem = buildNavItem("\u2192  Log out", false, this::handleLogout);
        logoutItem.setBackground(Theme.BG_SIDEBAR);
        logoutItem.setBorder(new EmptyBorder(10, 0, 0, 0));
        logoutItem.setAlignmentX(LEFT_ALIGNMENT);

        footer.add(userRow);
        footer.add(Box.createVerticalStrut(14));
        footer.add(logoutItem);
        return footer;
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        return parts.length > 0 && !parts[0].isEmpty()
                ? String.valueOf(parts[0].charAt(0)).toUpperCase()
                : "?";
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Log out of the Rental Console?", "Confirm Log Out",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm(manager).setVisible(true);
        }
    }

    private JPanel buildContentArea() {
        contentPanel.setBackground(Theme.BG_MAIN);

        newRentalPanel = new NewRentalPanel(manager, user,
                this::handleRentalCompleted, this::refreshStats);
        transactionHistoryPanel = new TransactionHistoryPanel(manager, this::refreshStats);

        contentPanel.add(newRentalPanel, CARD_NEW_RENTAL);
        contentPanel.add(transactionHistoryPanel, CARD_HISTORY);
        return contentPanel;
    }

    private void handleRentalCompleted(RentalTransaction transaction) {
        refreshStats();
        new ReceiptDialog(this, transaction, this::showNewRental).setVisible(true);
    }

    private void showNewRental() {
        setActiveNav(navNewRental);
        newRentalPanel.refresh();
        refreshStats();
        cardLayout.show(contentPanel, CARD_NEW_RENTAL);
    }

    private void showHistory() {
        setActiveNav(navHistory);
        transactionHistoryPanel.refresh();
        refreshStats();
        cardLayout.show(contentPanel, CARD_HISTORY);
    }

    private void refreshStats() {
        availableStat.setText(String.valueOf(manager.countAvailable()));
        rentedStat.setText(String.valueOf(manager.countRented()));
        todayTxnStat.setText(String.valueOf(manager.countTransactionsToday()));
        revenueStat.setText("\u20B1" + java.text.NumberFormat.getNumberInstance(new java.util.Locale("en", "PH"))
                .format(manager.getRevenueToday()));
        overdueStat.setText(String.valueOf(manager.countOverdue()));
    }

    private void refreshProfile() {
        avatarLbl.setText(initials(user.getFullName()));
        nameLbl.setText(user.getFullName());
        roleLbl.setText(user.getRole());
    }
}
