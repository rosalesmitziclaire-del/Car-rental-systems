package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Small helpers shared by the popup forms.
public class FormUtil {

    public static void addRow(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(l);
        p.add(field);
        p.add(Box.createVerticalStrut(12));
    }

    public static JPanel formPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_MAIN);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(26, 30, 26, 30));
        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_TITLE);
        t.setForeground(Theme.TEXT_WHITE);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        t.setBorder(new EmptyBorder(0, 0, 16, 0));
        p.add(t);
        return p;
    }

    public static JLabel errorLabel() {
        JLabel e = new JLabel(" ");
        e.setFont(Theme.FONT_SMALL);
        e.setForeground(new Color(230, 100, 100));
        e.setAlignmentX(Component.LEFT_ALIGNMENT);
        e.setBorder(new EmptyBorder(4, 0, 8, 0));
        return e;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(Theme.BG_FIELD);
        f.setForeground(Theme.TEXT_WHITE);
        f.setCaretColor(Theme.TEXT_WHITE);
        f.setFont(Theme.FONT_BODY);
        f.setBorder(new EmptyBorder(10, 12, 10, 12));
        return f;
    }

    public static void styleBox(JComboBox<?> box) {
        box.setBackground(Theme.BG_FIELD);
        box.setForeground(Theme.TEXT_WHITE);
        box.setFont(Theme.FONT_BODY);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? Theme.ACCENT_BLUE : Theme.BG_FIELD);
                setForeground(Theme.TEXT_WHITE);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }

    public static JButton submitButton(String text) {
        JButton b = Theme.createPrimaryButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    // Dark table styling used by every table in the app.
    public static void styleTable(JTable table, int rowHeight) {
        table.setBackground(Theme.BG_CARD);
        table.setForeground(Theme.TEXT_WHITE);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(rowHeight);
        table.setGridColor(Theme.BORDER);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(Theme.BG_CARD_HOVER);
        table.setSelectionForeground(Theme.TEXT_WHITE);
        table.getTableHeader().setBackground(Theme.BG_SIDEBAR);
        table.getTableHeader().setForeground(Theme.TEXT_GRAY);
        table.getTableHeader().setFont(Theme.FONT_SECTION);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Theme.BORDER));
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Cannot do that", JOptionPane.WARNING_MESSAGE);
    }
}
