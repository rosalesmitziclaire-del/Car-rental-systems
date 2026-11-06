package ksk_car3;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

// One clickable, text-only row for a vehicle (no photos).
public class VehicleCard extends JPanel {

    private final Vehicle vehicle;
    private boolean selected = false;

    public VehicleCard(Vehicle vehicle, Consumer<VehicleCard> onSelect) {
        this.vehicle = vehicle;

        setLayout(new BorderLayout());
        setBackground(Theme.BG_CARD);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyBorder();

        JLabel nameLabel = new JLabel(vehicle.getFullName());
        nameLabel.setFont(Theme.FONT_BODY_BOLD);
        nameLabel.setForeground(Theme.TEXT_WHITE);

        String detail = vehicle.getVehicleType() + "  \u2022  " + vehicle.getColor().getDisplayName()
                + "  \u2022  " + vehicle.getPlateNumber();
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(Theme.FONT_SMALL);
        detailLabel.setForeground(Theme.TEXT_GRAY);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        detailLabel.setAlignmentX(LEFT_ALIGNMENT);
        textStack.add(nameLabel);
        textStack.add(Box.createVerticalStrut(3));
        textStack.add(detailLabel);

        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));
        JLabel priceLabel = new JLabel("\u20B1" + peso.format(vehicle.getDailyRate()));
        JLabel perDayLabel = new JLabel("per day");
        priceLabel.setFont(Theme.FONT_BODY_BOLD);
        priceLabel.setForeground(Theme.TEXT_WHITE);
        perDayLabel.setFont(Theme.FONT_SMALL);
        perDayLabel.setForeground(Theme.TEXT_GRAY);

        JPanel priceStack = new JPanel();
        priceStack.setOpaque(false);
        priceStack.setLayout(new BoxLayout(priceStack, BoxLayout.Y_AXIS));
        priceLabel.setAlignmentX(RIGHT_ALIGNMENT);
        perDayLabel.setAlignmentX(RIGHT_ALIGNMENT);
        priceStack.add(priceLabel);
        priceStack.add(perDayLabel);

        add(textStack, BorderLayout.WEST);
        add(priceStack, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(VehicleCard.this);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) setBackground(Theme.BG_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) setBackground(Theme.BG_CARD);
            }
        });
    }

    private void applyBorder() {
        Color borderColor = selected ? Theme.ACCENT_RED : Theme.BORDER;
        setBorder(new CompoundBorder(
                new LineBorder(borderColor, selected ? 2 : 1, true),
                new EmptyBorder(12, 14, 12, 14)));
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
        applyBorder();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        return new Dimension(Integer.MAX_VALUE, pref.height);
    }
}
