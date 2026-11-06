package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Admin screen: table of staff accounts + Add / Edit / Delete.
public class StaffManagementPanel extends JPanel {

    private static final String[] COLS = {"Full Name", "Username", "Role"};
    private final CarRentalManager mgr;
    private final User current;
    private final Runnable onChange;
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel countLbl = new JLabel(" ");

    public StaffManagementPanel(CarRentalManager mgr, User current, Runnable onChange) {
        this.mgr = mgr;
        this.current = current;
        this.onChange = onChange;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(30, 34, 30, 34));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Staff Management");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        countLbl.setFont(Theme.FONT_SUBTITLE);
        countLbl.setForeground(Theme.TEXT_GRAY);
        countLbl.setBorder(new EmptyBorder(4, 0, 0, 0));
        left.add(title);
        left.add(countLbl);

        JButton editBtn = Theme.createSecondaryButton("Edit");
        editBtn.addActionListener(e -> { User u = selected(); if (u != null) openDialog(u); });
        JButton delBtn = Theme.createDangerButton("Delete");
        delBtn.addActionListener(e -> deleteSelected());
        JButton addBtn = Theme.createPrimaryButton("Add Staff");
        addBtn.addActionListener(e -> openDialog(null));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(editBtn);
        right.add(delBtn);
        right.add(addBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        FormUtil.styleTable(table, 38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
                setForeground(Theme.TEXT_WHITE);
                return this;
            }
        };
        for (int i = 0; i < COLS.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(r);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(sp, BorderLayout.CENTER);
        refresh();
    }

    private User selected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= mgr.getStaff().size()) {
            FormUtil.error(this, "Select a staff member in the table first.");
            return null;
        }
        return mgr.getStaff().get(row);
    }

    private void openDialog(User target) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        new StaffDialog(owner, mgr, target, current, () -> {
            refresh();
            onChange.run();
        }).setVisible(true);
    }

    private void deleteSelected() {
        User u = selected();
        if (u == null) return;
        int ans = JOptionPane.showConfirmDialog(this,
                "Delete the account of " + u.getFullName() + " (" + u.getUsername() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;
        try {
            mgr.deleteStaff(u, current);
        } catch (IllegalStateException ex) {
            FormUtil.error(this, ex.getMessage());
            return;
        }
        refresh();
        onChange.run();
    }

    public void refresh() {
        model.setRowCount(0);
        for (User u : mgr.getStaff()) {
            model.addRow(new Object[]{
                u.getFullName() + (u == current ? "  (you)" : ""),
                u.getUsername(),
                u.getRole()  // polymorphic getRole()
            });
        }
        countLbl.setText(mgr.getStaff().size() + " staff accounts");
    }
}
