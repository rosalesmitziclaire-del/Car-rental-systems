package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

// Admin screen: table of every car + Add / Edit / Delete, search by plate, sort by rate.
public class FleetInventoryPanel extends JPanel {

    private static final String[] COLS = {"Plate", "Vehicle", "Type", "Color", "Rate / Day", "Status"};

    private final CarRentalManager mgr;
    private final Runnable onChange;
    private final DefaultTableModel model;
    private final JTable table;
    private JLabel countLbl;
    private JTextField searchField;
    private JComboBox<String> sortBox;
    private ArrayList<Vehicle> shown = new ArrayList<>();

    public FleetInventoryPanel(CarRentalManager mgr, Runnable onChange) {
        this.mgr = mgr;
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

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable();

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(sp, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Fleet Inventory");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        countLbl = new JLabel(" ");
        countLbl.setFont(Theme.FONT_SUBTITLE);
        countLbl.setForeground(Theme.TEXT_GRAY);
        countLbl.setAlignmentX(LEFT_ALIGNMENT);
        countLbl.setBorder(new EmptyBorder(4, 0, 0, 0));
        left.add(title);
        left.add(countLbl);

        JButton editBtn = Theme.createSecondaryButton("Edit");
        editBtn.addActionListener(e -> editSelected());
        JButton delBtn = Theme.createDangerButton("Delete");
        delBtn.addActionListener(e -> deleteSelected());
        JButton addBtn = Theme.createPrimaryButton("Add Car");
        addBtn.addActionListener(e -> openDialog(null));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(editBtn);
        right.add(delBtn);
        right.add(addBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // Search by plate (linear search) and sort by rate (hand-written insertion sort).
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lbl = new JLabel("Search plate:");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_GRAY);

        searchField = Theme.createTextField("Search by plate number");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        sortBox = new JComboBox<>(new String[]{"Sort: Default", "Rate: Low to High", "Rate: High to Low"});
        FormUtil.styleBox(sortBox);
        sortBox.addActionListener(e -> refresh());

        bar.add(lbl, BorderLayout.WEST);
        bar.add(searchField, BorderLayout.CENTER);
        bar.add(sortBox, BorderLayout.EAST);
        return bar;
    }

    private Vehicle selectedVehicle() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= shown.size()) {
            FormUtil.error(this, "Select a car in the table first.");
            return null;
        }
        return shown.get(row);
    }

    private void openDialog(Vehicle v) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        new CarDialog(owner, mgr, v, () -> {
            refresh();
            onChange.run();
        }).setVisible(true);
    }

    private void editSelected() {
        Vehicle v = selectedVehicle();
        if (v != null) openDialog(v);
    }

    private void deleteSelected() {
        Vehicle v = selectedVehicle();
        if (v == null) return;
        int ans = JOptionPane.showConfirmDialog(this,
                "Delete " + v + "? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;
        try {
            mgr.deleteVehicle(v);
        } catch (IllegalStateException ex) {
            FormUtil.error(this, ex.getMessage());
            return;
        }
        refresh();
        onChange.run();
    }

    private void styleTable() {
        FormUtil.styleTable(table, 38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer plain = new DefaultTableCellRenderer();
        plain.setBackground(Theme.BG_CARD);
        plain.setForeground(Theme.TEXT_WHITE);

        // Status column: green Available, red Rented, amber Under Maintenance.
        DefaultTableCellRenderer status = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
                setForeground(Theme.statusColor(String.valueOf(v)));
                return this;
            }
        };
        DefaultTableCellRenderer plainSel = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
                setForeground(Theme.TEXT_WHITE);
                return this;
            }
        };
        for (int i = 0; i < COLS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(i == 5 ? status : plainSel);
        }
    }

    public void refresh() {
        model.setRowCount(0);
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));

        shown = mgr.searchByPlate(searchField.getText());
        int s = sortBox.getSelectedIndex();
        if (s == 1) shown = mgr.sortByRate(shown, true);
        else if (s == 2) shown = mgr.sortByRate(shown, false);

        for (Vehicle v : shown) {
            model.addRow(new Object[]{
                v.getPlateNumber(), v.getFullName(), v.getVehicleType(),
                v.getColor().getDisplayName(),
                "\u20B1" + peso.format(v.getDailyRate()),
                v.getStatus().getDisplayName()
            });
        }
        countLbl.setText(mgr.getAllVehicles().size() + " cars, " + mgr.countAvailable() + " available, "
                + mgr.countRented() + " rented, " + mgr.countMaintenance() + " in maintenance");
    }
}
