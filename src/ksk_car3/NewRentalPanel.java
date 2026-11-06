package ksk_car3;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// POS screen: customer info, pick a vehicle, pick dates, process the rental.
public class NewRentalPanel extends JPanel {

    private final CarRentalManager manager;
    private final User currentStaff;
    private final Consumer<RentalTransaction> onRentalCompleted;
    private final Runnable onFleetChanged;

    private JTextField vehicleSearch;
    private JComboBox<String> typeFilter;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactField;
    private JPanel vehicleListPanel;
    private JLabel errorLabel;
    private JLabel estimatedTotalLabel;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    private VehicleCard selectedCard;
    private Vehicle selectedVehicle;

    public NewRentalPanel(CarRentalManager manager, User currentStaff,
                           Consumer<RentalTransaction> onRentalCompleted, Runnable onFleetChanged) {
        this.manager = manager;
        this.currentStaff = currentStaff;
        this.onRentalCompleted = onRentalCompleted;
        this.onFleetChanged = onFleetChanged;

        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(30, 34, 30, 34));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildScrollableBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("New Rental");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Process a car rental for a customer");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 20, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JScrollPane buildScrollableBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(buildCustomerInfoCard());
        body.add(Box.createVerticalStrut(20));
        body.add(buildVehicleCard());
        body.add(Box.createVerticalStrut(20));
        body.add(buildRentalPeriodCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildErrorLabel());
        body.add(Box.createVerticalStrut(8));
        body.add(buildProcessButton());

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel buildCustomerInfoCard() {
        JPanel card = sectionCard("CUSTOMER INFORMATION");

        JLabel nameLabel = fieldLabel("Full Name");
        nameField = Theme.createTextField("e.g. Steven De Castro");

        JLabel emailLabel = fieldLabel("Email Address");
        emailField = Theme.createTextField("customer@gmail.com");

        JLabel contactLabel = fieldLabel("Contact No.");
        contactField = Theme.createTextField("09XXXXXXXXX");

        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.add(stack(emailLabel, emailField));
        row.add(stack(contactLabel, contactField));
        row.setAlignmentX(LEFT_ALIGNMENT);

        card.add(stack(nameLabel, nameField));
        card.add(Box.createVerticalStrut(14));
        card.add(row);
        return card;
    }

    private JPanel buildVehicleCard() {
        JPanel card = sectionCard("VEHICLE");

        // Filter: search by brand / model / plate, and by type.
        vehicleSearch = Theme.createTextField("Search brand, model or plate");
        typeFilter = new JComboBox<>(new String[]{"All types", "Sedan", "SUV"});
        FormUtil.styleBox(typeFilter);
        JPanel filterRow = new JPanel(new BorderLayout(12, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        filterRow.add(vehicleSearch, BorderLayout.CENTER);
        filterRow.add(typeFilter, BorderLayout.EAST);
        vehicleSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshVehicleList(card); }
            public void removeUpdate(DocumentEvent e) { refreshVehicleList(card); }
            public void changedUpdate(DocumentEvent e) { refreshVehicleList(card); }
        });
        typeFilter.addActionListener(e -> refreshVehicleList(card));
        card.add(filterRow);

        vehicleListPanel = new JPanel();
        vehicleListPanel.setOpaque(false);
        vehicleListPanel.setLayout(new BoxLayout(vehicleListPanel, BoxLayout.Y_AXIS));
        vehicleListPanel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(vehicleListPanel);
        refreshVehicleList(card);
        return card;
    }

    private void refreshVehicleList(JPanel card) {
        vehicleListPanel.removeAll();
        selectedCard = null;
        selectedVehicle = null;

        ArrayList<Vehicle> available = new ArrayList<>();
        String q = vehicleSearch.getText().trim().toLowerCase();
        String type = (String) typeFilter.getSelectedItem();
        for (Vehicle v : manager.getAvailableVehicles()) {
            boolean typeOk = type.equals("All types") || v.getVehicleType().equals(type);
            boolean textOk = q.isEmpty() || v.getFullName().toLowerCase().contains(q)
                    || v.getPlateNumber().toLowerCase().contains(q);
            if (typeOk && textOk) available.add(v);
        }
        if (available.isEmpty()) {
            JLabel none = new JLabel("No matching vehicles available.");
            none.setFont(Theme.FONT_BODY);
            none.setForeground(Theme.TEXT_GRAY);
            vehicleListPanel.add(none);
        } else {
            for (Vehicle v : available) {
                VehicleCard vc = new VehicleCard(v, this::selectVehicleCard);
                vc.setAlignmentX(LEFT_ALIGNMENT);
                vehicleListPanel.add(vc);
                vehicleListPanel.add(Box.createVerticalStrut(10));
            }
        }
        card.revalidate();
        card.repaint();
    }

    private void selectVehicleCard(VehicleCard card) {
        if (selectedCard != null) {
            selectedCard.setSelected(false);
        }
        selectedCard = card;
        selectedCard.setSelected(true);
        selectedVehicle = card.getVehicle();
        updateEstimatedTotal();
    }

    private JPanel buildRentalPeriodCard() {
        JPanel card = sectionCard("RENTAL PERIOD");

        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + 24L * 60 * 60 * 1000);

        startDateSpinner = createDateSpinner(today);
        endDateSpinner = createDateSpinner(tomorrow);
        startDateSpinner.addChangeListener(e -> updateEstimatedTotal());
        endDateSpinner.addChangeListener(e -> updateEstimatedTotal());

        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(stack(fieldLabel("Start Date"), startDateSpinner));
        row.add(stack(fieldLabel("End Date"), endDateSpinner));

        estimatedTotalLabel = new JLabel("Estimated Total: \u2014");
        estimatedTotalLabel.setFont(Theme.FONT_BODY_BOLD);
        estimatedTotalLabel.setForeground(Theme.TEXT_WHITE);
        estimatedTotalLabel.setAlignmentX(LEFT_ALIGNMENT);
        estimatedTotalLabel.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(row);
        card.add(estimatedTotalLabel);
        return card;
    }

    private JSpinner createDateSpinner(Date initialValue) {
        SpinnerDateModel model = new SpinnerDateModel(initialValue, null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        spinner.setFont(Theme.FONT_BODY);
        spinner.getEditor().getComponent(0).setBackground(Theme.BG_FIELD);
        spinner.getEditor().getComponent(0).setForeground(Theme.TEXT_WHITE);
        spinner.setBorder(new LineBorder(Theme.BORDER));
        return spinner;
    }

    private void updateEstimatedTotal() {
        LocalDate start = spinnerToLocalDate(startDateSpinner);
        LocalDate end = spinnerToLocalDate(endDateSpinner);

        if (selectedVehicle == null || !end.isAfter(start)) {
            estimatedTotalLabel.setText("Estimated Total: \u2014");
            return;
        }
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end);
        double fee = selectedVehicle.calculateRentalFee(days);
        NumberFormat peso = NumberFormat.getNumberInstance(new Locale("en", "PH"));
        estimatedTotalLabel.setText("Estimated Total (" + days + " day"
                + (days == 1 ? "" : "s") + "): \u20B1" + peso.format(fee));
    }

    private LocalDate spinnerToLocalDate(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private JLabel buildErrorLabel() {
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(new Color(230, 100, 100));
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        return errorLabel;
    }

    private JButton buildProcessButton() {
        JButton button = Theme.createPrimaryButton("Process Rental");
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.addActionListener(e -> handleProcessRental());
        return button;
    }

    private void handleProcessRental() {
        errorLabel.setText(" ");
        try {
            Customer customer = new Customer(
                    nameField.getText(), emailField.getText(), contactField.getText());

            if (selectedVehicle == null) {
                throw new IllegalArgumentException("Please select a vehicle.");
            }

            LocalDate start = spinnerToLocalDate(startDateSpinner);
            LocalDate end = spinnerToLocalDate(endDateSpinner);

            RentalTransaction transaction = manager.processRental(
                    customer, selectedVehicle, start, end, currentStaff.getFullName());

            resetForm();
            onFleetChanged.run();
            onRentalCompleted.accept(transaction);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        contactField.setText("");
        startDateSpinner.setValue(new Date());
        endDateSpinner.setValue(new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000));
        refreshVehicleList((JPanel) vehicleListPanel.getParent());
        estimatedTotalLabel.setText("Estimated Total: \u2014");
    }

    // Called by the dashboard whenever this tab is shown again.
    public void refresh() {
        refreshVehicleList((JPanel) vehicleListPanel.getParent());
        updateEstimatedTotal();
    }

    private JPanel sectionCard(String sectionTitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER),
                new EmptyBorder(20, 20, 20, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel label = Theme.createSectionLabel(sectionTitle);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 14, 0));
        card.add(label);
        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_SMALL);
        label.setForeground(Theme.TEXT_GRAY);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));
        return label;
    }

    private JPanel stack(JLabel label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        panel.add(label);
        panel.add(field);
        return panel;
    }
}
