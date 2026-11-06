package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

// Every rental with its status (Ongoing / Completed / Cancelled), search + filter,
// and the actions: View Receipt, Return Car, Cancel Rental. Used by both POS and Admin.
public class TransactionHistoryPanel extends JPanel {

    private static final String[] COLUMNS = {
            "Transaction ID", "Customer", "Vehicle", "Type",
            "Start Date", "End Date", "Days", "Total Fee", "Status", "Processed"
    };
    private static final int STATUS_COL = 8;

    private final CarRentalManager manager;
    private final Runnable onChange;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel countLabel = new JLabel("0 transactions");
    private final JTextField searchField = Theme.createTextField("Search by customer or transaction ID");
    private final JComboBox<String> statusBox =
            new JComboBox<>(new String[]{"All statuses", "Ongoing", "Completed", "Cancelled"});
    private ArrayList<RentalTransaction> shown = new ArrayList<>();

    public TransactionHistoryPanel(CarRentalManager manager, Runnable onChange) {
        this.manager = manager;
        this.onChange = onChange;

        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(30, 34, 30, 34));

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(buildHeader());
        north.add(buildToolbar());
        add(north, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Transaction History");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        countLabel.setFont(Theme.FONT_SUBTITLE);
        countLabel.setForeground(Theme.TEXT_GRAY);
        countLabel.setAlignmentX(LEFT_ALIGNMENT);
        countLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        left.add(title);
        left.add(countLabel);

        JButton receiptBtn = Theme.createSecondaryButton("View Receipt");
        receiptBtn.addActionListener(e -> viewReceipt());
        JButton returnBtn = Theme.createPrimaryButton("Return Car");
        returnBtn.addActionListener(e -> returnSelected());
        JButton cancelBtn = Theme.createDangerButton("Cancel Rental");
        cancelBtn.addActionListener(e -> cancelSelected());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(receiptBtn);
        right.add(returnBtn);
        right.add(cancelBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lbl = new JLabel("Search:");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_GRAY);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        FormUtil.styleBox(statusBox);
        statusBox.addActionListener(e -> refresh());

        bar.add(lbl, BorderLayout.WEST);
        bar.add(searchField, BorderLayout.CENTER);
        bar.add(statusBox, BorderLayout.EAST);
        return bar;
    }

    private void styleTable() {
        FormUtil.styleTable(table, 34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer plain = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
                setForeground(col == STATUS_COL ? Theme.statusColor(String.valueOf(v)) : Theme.TEXT_WHITE);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(plain);
        }
    }

    private RentalTransaction selected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= shown.size()) {
            FormUtil.error(this, "Select a transaction in the table first.");
            return null;
        }
        return shown.get(row);
    }

    private void viewReceipt() {
        RentalTransaction t = selected();
        if (t == null) return;
        new ReceiptDialog((Frame) SwingUtilities.getWindowAncestor(this), t).setVisible(true);
    }

    private void returnSelected() {
        RentalTransaction t = selected();
        if (t == null) return;
        if (t.getStatus() != TransactionStatus.ONGOING) {
            FormUtil.error(this, t.getTransactionId() + " is already " + t.getStatus().getDisplayName().toLowerCase() + ".");
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Mark " + t.getTransactionId() + " as returned? " + t.getVehicle().getFullName() + " becomes available again.",
                "Return Car", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;
        manager.returnCar(t);
        refresh();
        onChange.run();
    }

    private void cancelSelected() {
        RentalTransaction t = selected();
        if (t == null) return;
        if (t.getStatus() != TransactionStatus.ONGOING) {
            FormUtil.error(this, t.getTransactionId() + " is already " + t.getStatus().getDisplayName().toLowerCase() + ".");
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Cancel " + t.getTransactionId() + "? The rental will not count as revenue.",
                "Cancel Rental", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;
        manager.cancelRental(t);
        refresh();
        onChange.run();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        ArrayList<RentalTransaction> all = manager.getAllTransactions();

        String q = searchField.getText().trim().toLowerCase();
        int f = statusBox.getSelectedIndex(); // 0 = all, then Ongoing, Completed, Cancelled
        shown = new ArrayList<>();
        for (RentalTransaction t : all) {
            if (f > 0 && t.getStatus() != TransactionStatus.values()[f - 1]) continue;
            if (!q.isEmpty() && !t.getTransactionId().toLowerCase().contains(q)
                    && !t.getCustomer().getFullName().toLowerCase().contains(q)) continue;
            shown.add(t);
        }

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        DateTimeFormatter processedFmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));

        for (RentalTransaction t : shown) {
            tableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getCustomer().getFullName(),
                    t.getVehicle().getFullName(),
                    t.getVehicle().getVehicleType(),
                    t.getStartDate().format(dateFmt),
                    t.getEndDate().format(dateFmt),
                    t.getTotalDays(),
                    "\u20B1" + peso.format(t.getTotalFee()),
                    t.getStatusLabel(),
                    t.getDateProcessed().format(processedFmt)
            });
        }
        countLabel.setText("Showing " + shown.size() + " of " + all.size() + " transaction"
                + (all.size() == 1 ? "" : "s"));
    }
}
