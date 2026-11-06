package ksk_car3;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

// Admin screen (read-only): fixed types, fixed colors, and brands currently in the fleet.
public class BrandsTypesPanel extends JPanel {

    private final CarRentalManager mgr;
    private final JPanel body = new JPanel();

    public BrandsTypesPanel(CarRentalManager mgr) {
        this.mgr = mgr;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(30, 34, 30, 34));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Brands & Types");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Read-only reference of what the fleet can contain");
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(Theme.TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 20, 0));
        header.add(title);
        header.add(sub);

        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(body) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        body.removeAll();
        ArrayList<String> types = new ArrayList<>();
        types.add("Sedan");
        types.add("SUV");
        ArrayList<String> typeRows = new ArrayList<>();
        for (String t : types) {
            int n = 0;
            for (Vehicle v : mgr.getAllVehicles()) if (v.getVehicleType().equals(t)) n++;
            typeRows.add(t + "   \u2022   " + n + " in fleet");
        }

        ArrayList<String> colorRows = new ArrayList<>();
        for (CarColor c : CarColor.values()) colorRows.add(c.getDisplayName());

        ArrayList<String> brandNames = new ArrayList<>();
        ArrayList<Integer> brandCounts = new ArrayList<>();
        for (Vehicle v : mgr.getAllVehicles()) {
            int idx = brandNames.indexOf(v.getBrand());
            if (idx == -1) { brandNames.add(v.getBrand()); brandCounts.add(1); }
            else brandCounts.set(idx, brandCounts.get(idx) + 1);
        }
        ArrayList<String> brandRows = new ArrayList<>();
        for (int i = 0; i < brandNames.size(); i++) brandRows.add(brandNames.get(i) + "   \u2022   " + brandCounts.get(i) + " car(s)");

        body.add(section("VEHICLE TYPES", typeRows));
        body.add(Box.createVerticalStrut(16));
        body.add(section("COLORS", colorRows));
        body.add(Box.createVerticalStrut(16));
        body.add(section("BRANDS IN FLEET", brandRows));
        body.revalidate();
        body.repaint();
    }

    private JPanel section(String titleText, ArrayList<String> rows) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(18, 20, 18, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel l = Theme.createSectionLabel(titleText);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(l);
        for (String r : rows) {
            JLabel row = new JLabel(r);
            row.setFont(Theme.FONT_BODY);
            row.setForeground(Theme.TEXT_WHITE);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            card.add(row);
        }
        return card;
    }
}
