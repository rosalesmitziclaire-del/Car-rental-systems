package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Receipt popup shown right after a rental is processed.
public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Frame owner, RentalTransaction transaction, Runnable onDone) {
        this(owner, transaction, onDone, "Rental Confirmed", "New Rental");
    }

    // View-only receipt for past transactions (History screens).
    public ReceiptDialog(Frame owner, RentalTransaction transaction) {
        this(owner, transaction, null, "Receipt", "Close");
    }

    private ReceiptDialog(Frame owner, RentalTransaction transaction, Runnable onDone,
                          String heading, String buttonText) {
        super(owner, heading, true);
        setSize(440, 610);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Theme.BG_MAIN);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel check = new JLabel("\u2713");
        check.setFont(new Font("Segoe UI", Font.BOLD, 34));
        check.setForeground(Theme.ACCENT_GREEN);
        check.setAlignmentX(CENTER_ALIGNMENT);
        check.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel(heading);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(transaction.getTransactionId());
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 24, 0));

        content.add(check);
        content.add(title);
        content.add(subtitle);

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(Theme.BG_CARD);
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(18, 18, 18, 18)));
        detailsCard.setAlignmentX(CENTER_ALIGNMENT);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));

        addRow(detailsCard, "Customer", transaction.getCustomer().getFullName());
        addRow(detailsCard, "Vehicle", transaction.getVehicle().getFullName()
                + " (" + transaction.getVehicle().getVehicleType() + ")");
        addRow(detailsCard, "Plate Number", transaction.getVehicle().getPlateNumber());
        addRow(detailsCard, "Rental Period", transaction.getStartDate().format(dateFmt)
                + "  \u2192  " + transaction.getEndDate().format(dateFmt));
        addRow(detailsCard, "Total Days", String.valueOf(transaction.getTotalDays()));
        addRow(detailsCard, "Processed By", transaction.getProcessedBy());
        addRow(detailsCard, "Status", transaction.getStatusLabel());

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER), new EmptyBorder(14, 0, 0, 0)));

        JLabel totalLabel = new JLabel("Total Amount");
        totalLabel.setFont(Theme.FONT_BODY_BOLD);
        totalLabel.setForeground(Theme.TEXT_WHITE);

        JLabel totalValue = new JLabel("\u20B1" + peso.format(transaction.getTotalFee()));
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalValue.setForeground(Theme.TEXT_WHITE);

        totalRow.add(totalLabel, BorderLayout.WEST);
        totalRow.add(totalValue, BorderLayout.EAST);
        detailsCard.add(Box.createVerticalStrut(6));
        detailsCard.add(totalRow);

        content.add(Box.createVerticalStrut(20));
        content.add(detailsCard);
        content.add(Box.createVerticalGlue());

        JButton doneButton = Theme.createPrimaryButton(buttonText);
        doneButton.setAlignmentX(CENTER_ALIGNMENT);
        doneButton.addActionListener(e -> {
            dispose();
            if (onDone != null) onDone.run();
        });

        content.add(Box.createVerticalStrut(24));
        content.add(doneButton);

        setContentPane(content);
    }

    private void addRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.FONT_SMALL);
        labelComp.setForeground(Theme.TEXT_GRAY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(Theme.FONT_BODY);
        valueComp.setForeground(Theme.TEXT_WHITE);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        parent.add(row);
    }
}
