package ksk_car3;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

// Admin window: sidebar on the left, screens on the right.
public class AdminDashboard extends JFrame {

    private final CarRentalManager mgr;
    private final User user;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private FleetInventoryPanel fleetPanel;
    private TransactionHistoryPanel txnPanel;
    private BrandsTypesPanel brandsPanel;
    private StaffManagementPanel staffPanel;

    private JPanel navHome, navFleet, navTxn, navBrands, navStaff;
    private JLabel avatarLbl, nameLbl, roleLbl;

    private final JLabel totalLbl = new JLabel("0");
    private final JLabel availLbl = new JLabel("0");
    private final JLabel ongoingLbl = new JLabel("0");
    private final JLabel maintLbl = new JLabel("0");
    private final JLabel revenueLbl = new JLabel("0");
    private final JLabel todayRevLbl = new JLabel("0");
    private final JLabel completedLbl = new JLabel("0");
    private final JLabel cancelledLbl = new JLabel("0");
    private final JPanel recentBox = new JPanel();

    public AdminDashboard(CarRentalManager mgr, User user) {
        this.mgr = mgr;
        this.user = user;

        setTitle("SKS CAR - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1170, 730);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        fleetPanel = new FleetInventoryPanel(mgr, this::refreshStats);
        txnPanel = new TransactionHistoryPanel(mgr, this::refreshStats);
        brandsPanel = new BrandsTypesPanel(mgr);
        staffPanel = new StaffManagementPanel(mgr, user, this::refreshProfile);

        content.setBackground(Theme.BG_MAIN);
        content.add(buildHomePanel(), "HOME");
        content.add(fleetPanel, "FLEET");
        content.add(txnPanel, "TXN");
        content.add(brandsPanel, "BRANDS");
        content.add(staffPanel, "STAFF");

        add(buildSidebar(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        showHome();
    }

    // ---- Sidebar ----

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(260, 0));
        side.setBackground(Theme.BG_SIDEBAR);
        side.setBorder(new MatteBorder(0, 0, 0, 1, Theme.BORDER));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logo = new JLabel("SKS CAR");
        logo.setFont(Theme.FONT_LOGO);
        logo.setForeground(new Color(200, 40, 50));
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Admin Dashboard");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(2, 0, 26, 0));

        navHome = navItem("\u25A6  Dashboard", this::showHome);
        navFleet = navItem("\u25A4  Fleet Inventory", this::showFleet);
        navTxn = navItem("\u2611  Transactions", this::showTxn);
        navBrands = navItem("\u25C8  Brands & Types", this::showBrands);
        navStaff = navItem("\u2699  Staff Management", this::showStaff);

        top.add(logo);
        top.add(sub);
        for (JPanel nav : new JPanel[]{navHome, navFleet, navTxn, navBrands, navStaff}) {
            top.add(nav);
            top.add(Box.createVerticalStrut(8));
        }

        side.add(top, BorderLayout.NORTH);
        side.add(buildFooter(), BorderLayout.SOUTH);
        return side;
    }

    private JPanel navItem(String text, Runnable onClick) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(true);
        item.setBackground(Theme.BG_SIDEBAR);
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setAlignmentX(LEFT_ALIGNMENT);

        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY_BOLD);
        l.setForeground(Theme.TEXT_WHITE);
        item.add(l, BorderLayout.WEST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return item;
    }

    private void setActive(JPanel active) {
        for (JPanel nav : new JPanel[]{navHome, navFleet, navTxn, navBrands, navStaff}) {
            nav.setBackground(nav == active ? Theme.ACCENT_RED : Theme.BG_SIDEBAR);
        }
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER),
                new EmptyBorder(16, 20, 20, 20)));

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        avatarLbl = new JLabel(user.getFullName().substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        avatarLbl.setOpaque(true);
        avatarLbl.setBackground(Theme.ACCENT_RED);
        avatarLbl.setForeground(Theme.TEXT_WHITE);
        avatarLbl.setFont(Theme.FONT_BODY_BOLD);
        avatarLbl.setPreferredSize(new Dimension(36, 36));

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        nameLbl = new JLabel(user.getFullName());
        nameLbl.setFont(Theme.FONT_BODY_BOLD);
        nameLbl.setForeground(Theme.TEXT_WHITE);
        roleLbl = new JLabel(user.getRole());
        roleLbl.setFont(Theme.FONT_SMALL);
        roleLbl.setForeground(Theme.TEXT_GRAY);
        names.add(nameLbl);
        names.add(roleLbl);

        row.add(avatarLbl, BorderLayout.WEST);
        row.add(names, BorderLayout.CENTER);

        // Click your name to open My Profile.
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.setToolTipText("Edit profile");
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProfileDialog(AdminDashboard.this, user, AdminDashboard.this::refreshProfile).setVisible(true);
            }
        });

        JPanel logout = navItem("\u2192  Log out", this::handleLogout);
        logout.setBorder(new EmptyBorder(10, 0, 0, 0));

        footer.add(row);
        footer.add(Box.createVerticalStrut(14));
        footer.add(logout);
        return footer;
    }

    private void refreshProfile() {
        avatarLbl.setText(user.getFullName().substring(0, 1).toUpperCase());
        nameLbl.setText(user.getFullName());
        roleLbl.setText(user.getRole());
        staffPanel.refresh();
    }

    private void handleLogout() {
        int ans = JOptionPane.showConfirmDialog(this, "Log out of the Admin Dashboard?",
                "Confirm Log Out", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm(mgr).setVisible(true);
        }
    }

    // ---- Dashboard home (stat cards + recent transactions) ----

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_MAIN);
        p.setBorder(new EmptyBorder(30, 34, 30, 34));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Overview of your fleet, rentals and revenue");
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(Theme.TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 24, 0));

        header.add(title);
        header.add(sub);

        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
        grid.setOpaque(false);
        grid.add(statCard("Total Cars", totalLbl));
        grid.add(statCard("Available", availLbl));
        grid.add(statCard("Ongoing Rentals", ongoingLbl));
        grid.add(statCard("Under Maintenance", maintLbl));
        grid.add(statCard("Total Revenue", revenueLbl));
        grid.add(statCard("Today's Revenue", todayRevLbl));
        grid.add(statCard("Completed", completedLbl));
        grid.add(statCard("Cancelled", cancelledLbl));

        JPanel recentCard = new JPanel(new BorderLayout());
        recentCard.setBackground(Theme.BG_CARD);
        recentCard.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(18, 20, 18, 20)));
        JLabel recentTitle = Theme.createSectionLabel("RECENT TRANSACTIONS");
        recentTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        recentBox.setOpaque(false);
        recentBox.setLayout(new BoxLayout(recentBox, BoxLayout.Y_AXIS));
        JPanel recentTop = new JPanel(new BorderLayout());
        recentTop.setOpaque(false);
        recentTop.add(recentBox, BorderLayout.NORTH);
        recentCard.add(recentTitle, BorderLayout.NORTH);
        recentCard.add(recentTop, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout(0, 20));
        wrap.setOpaque(false);
        wrap.add(grid, BorderLayout.NORTH);
        wrap.add(recentCard, BorderLayout.CENTER);

        p.add(header, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        return p;
    }

    private JPanel statCard(String label, JLabel value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel l = Theme.createSectionLabel(label.toUpperCase());
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));

        value.setFont(new Font("Segoe UI", Font.BOLD, 26));
        value.setForeground(Theme.TEXT_WHITE);
        value.setAlignmentX(LEFT_ALIGNMENT);

        card.add(l);
        card.add(value);
        return card;
    }

    private void refreshRecent() {
        recentBox.removeAll();
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));
        ArrayList<RentalTransaction> recent = mgr.getRecentTransactions(5);
        if (recent.isEmpty()) {
            JLabel none = new JLabel("No transactions yet.");
            none.setFont(Theme.FONT_BODY);
            none.setForeground(Theme.TEXT_GRAY);
            recentBox.add(none);
        }
        for (RentalTransaction t : recent) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(6, 0, 6, 0));
            JLabel left = new JLabel(t.getTransactionId() + "   \u2022   " + t.getCustomer().getFullName()
                    + "   \u2022   " + t.getVehicle().getFullName()
                    + "   \u2022   \u20B1" + peso.format(t.getTotalFee()));
            left.setFont(Theme.FONT_BODY);
            left.setForeground(Theme.TEXT_WHITE);
            JLabel right = new JLabel(t.getStatusLabel());
            right.setFont(Theme.FONT_BODY_BOLD);
            right.setForeground(Theme.statusColor(t.getStatusLabel()));
            row.add(left, BorderLayout.WEST);
            row.add(right, BorderLayout.EAST);
            recentBox.add(row);
        }
        recentBox.revalidate();
        recentBox.repaint();
    }

    // ---- Navigation ----

    private void showHome() {
        setActive(navHome);
        refreshStats();
        cards.show(content, "HOME");
    }

    private void showFleet() {
        setActive(navFleet);
        fleetPanel.refresh();
        cards.show(content, "FLEET");
    }

    private void showTxn() {
        setActive(navTxn);
        txnPanel.refresh();
        cards.show(content, "TXN");
    }

    private void showBrands() {
        setActive(navBrands);
        brandsPanel.refresh();
        cards.show(content, "BRANDS");
    }

    private void showStaff() {
        setActive(navStaff);
        staffPanel.refresh();
        cards.show(content, "STAFF");
    }

    private void refreshStats() {
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));
        totalLbl.setText(String.valueOf(mgr.getAllVehicles().size()));
        availLbl.setText(String.valueOf(mgr.countAvailable()));
        ongoingLbl.setText(String.valueOf(mgr.countByStatus(TransactionStatus.ONGOING)));
        maintLbl.setText(String.valueOf(mgr.countMaintenance()));
        revenueLbl.setText("\u20B1" + peso.format(mgr.getTotalRevenue()));
        todayRevLbl.setText("\u20B1" + peso.format(mgr.getRevenueToday()));
        completedLbl.setText(String.valueOf(mgr.countByStatus(TransactionStatus.COMPLETED)));
        cancelledLbl.setText(String.valueOf(mgr.countByStatus(TransactionStatus.CANCELLED)));
        refreshRecent();
    }
}
