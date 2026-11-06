package ksk_car3;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public class Theme {
    public static final Color BG_MAIN = new Color(10, 13, 22);
    public static final Color BG_SIDEBAR = new Color(9, 12, 20);
    public static final Color BG_CARD = new Color(24, 29, 43);
    public static final Color BG_CARD_HOVER = new Color(32, 38, 54);
    public static final Color BG_FIELD = new Color(31, 37, 53);
    public static final Color ACCENT_RED = new Color(139, 27, 38);
    public static final Color ACCENT_RED_HOVER = new Color(160, 33, 45);
    public static final Color ACCENT_BLUE = new Color(28, 55, 92);
    public static final Color ACCENT_GREEN = new Color(46, 160, 90);
    public static final Color TEXT_WHITE = new Color(240, 242, 246);
    public static final Color TEXT_GRAY = new Color(148, 157, 176);
    public static final Color BORDER = new Color(38, 44, 60);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(BG_FIELD); f.setForeground(TEXT_WHITE); f.setCaretColor(TEXT_WHITE);
        f.setFont(FONT_BODY); f.setBorder(new EmptyBorder(10, 12, 10, 12)); f.setToolTipText(placeholder);
        return f;
    }
    public static JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT_RED); b.setForeground(TEXT_WHITE); b.setFont(FONT_BUTTON);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setOpaque(true);
        b.getModel().addChangeListener(e -> b.setBackground(b.getModel().isRollover() ? ACCENT_RED_HOVER : ACCENT_RED));
        return b;
    }
    public static JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION); l.setForeground(TEXT_GRAY);
        return l;
    }
    public static final Color ACCENT_AMBER = new Color(230, 160, 40);
    public static final Color ACCENT_SKY = new Color(90, 160, 255);
    public static JButton createButton(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(TEXT_WHITE); b.setFont(FONT_BUTTON);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setOpaque(true);
        b.getModel().addChangeListener(e -> b.setBackground(b.getModel().isRollover() ? hover : bg));
        return b;
    }
    public static JButton createSecondaryButton(String text) {
        return createButton(text, ACCENT_BLUE, new Color(38, 72, 118));
    }
    public static JButton createDangerButton(String text) {
        return createButton(text, new Color(70, 22, 30), ACCENT_RED);
    }
    // Color for a status label: Ongoing, Overdue, Completed, Cancelled, Available, Rented, Under Maintenance.
    public static Color statusColor(String s) {
        if (s == null) return TEXT_WHITE;
        switch (s) {
            case "Ongoing": return ACCENT_SKY;
            case "Completed": case "Available": return ACCENT_GREEN;
            case "Overdue": case "Rented": return new Color(230, 100, 100);
            case "Under Maintenance": return ACCENT_AMBER;
            case "Cancelled": return TEXT_GRAY;
            default: return TEXT_WHITE;
        }
    }
}
