package ksk_car3;

import javax.swing.*;
import java.awt.*;

// Popup form for adding OR editing a car. In edit mode the plate and type are locked.
public class CarDialog extends JDialog {

    private final CarRentalManager mgr;
    private final Vehicle editing;     // null = adding a new car
    private final Runnable onDone;

    private final JTextField brandF = Theme.createTextField("Toyota");
    private final JTextField modelF = Theme.createTextField("Vios");
    private final JTextField plateF = Theme.createTextField("ABC 1234");
    private final JTextField rateF = Theme.createTextField("2000");
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Sedan", "SUV"});
    private final JComboBox<CarColor> colorBox = new JComboBox<>(CarColor.values());
    private final JComboBox<String> statusBox = new JComboBox<>();
    private final JLabel errorLbl = FormUtil.errorLabel();

    public CarDialog(Frame owner, CarRentalManager mgr, Vehicle editing, Runnable onDone) {
        super(owner, editing == null ? "Add Car" : "Edit Car", true);
        this.mgr = mgr;
        this.editing = editing;
        this.onDone = onDone;

        setSize(440, editing == null ? 600 : 680);
        setLocationRelativeTo(owner);
        setResizable(false);

        FormUtil.styleBox(typeBox);
        FormUtil.styleBox(colorBox);
        FormUtil.styleBox(statusBox);

        JPanel p = FormUtil.formPanel(editing == null ? "Add Car" : "Edit Car");
        FormUtil.addRow(p, "Brand", brandF);
        FormUtil.addRow(p, "Model", modelF);
        FormUtil.addRow(p, "Plate Number", plateF);
        FormUtil.addRow(p, "Car Type", typeBox);
        FormUtil.addRow(p, "Color", colorBox);
        FormUtil.addRow(p, "Daily Rate (PHP)", rateF);

        if (editing != null) {
            brandF.setText(editing.getBrand());
            modelF.setText(editing.getModel());
            plateF.setText(editing.getPlateNumber());
            plateF.setEditable(false);
            plateF.setForeground(Theme.TEXT_GRAY);
            typeBox.setSelectedItem(editing.getVehicleType());
            typeBox.setEnabled(false);
            colorBox.setSelectedItem(editing.getColor());
            double r = editing.getDailyRate();
            rateF.setText(r == Math.floor(r) ? String.valueOf((long) r) : String.valueOf(r));

            if (editing.getStatus() == VehicleStatus.RENTED) {
                statusBox.addItem("Rented");
                statusBox.setEnabled(false);
            } else {
                statusBox.addItem("Available");
                statusBox.addItem("Under Maintenance");
                statusBox.setSelectedItem(editing.getStatus().getDisplayName());
            }
            FormUtil.addRow(p, "Status", statusBox);
        }

        p.add(errorLbl);
        JButton save = FormUtil.submitButton(editing == null ? "Add Car" : "Save Changes");
        save.addActionListener(e -> handleSave());
        p.add(save);
        setContentPane(p);
    }

    private void handleSave() {
        String brand = brandF.getText().trim();
        String model = modelF.getText().trim();
        String plate = plateF.getText().trim().toUpperCase();

        if (brand.isEmpty() || model.isEmpty() || plate.isEmpty()) {
            errorLbl.setText("Please fill in brand, model and plate number.");
            return;
        }
        double rate;
        try {
            rate = Double.parseDouble(rateF.getText().trim());
        } catch (NumberFormatException ex) {
            errorLbl.setText("Daily rate must be a number.");
            return;
        }
        if (rate <= 0) {
            errorLbl.setText("Daily rate must be more than 0.");
            return;
        }
        CarColor color = (CarColor) colorBox.getSelectedItem();

        try {
            if (editing == null) {
                // Polymorphism: both are stored as plain Vehicle.
                Vehicle v = typeBox.getSelectedItem().equals("SUV")
                        ? new SUV(plate, brand, model, color, rate)
                        : new Sedan(plate, brand, model, color, rate);
                mgr.addVehicle(v);
            } else {
                boolean maintenance = "Under Maintenance".equals(statusBox.getSelectedItem());
                mgr.updateVehicle(editing, brand, model, color, rate, maintenance);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            errorLbl.setText(ex.getMessage());
            return;
        }
        dispose();
        onDone.run();
    }
}
