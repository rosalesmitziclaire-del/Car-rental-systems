package ksk_car3;

import javax.swing.*;
import java.awt.*;

// Popup form for adding OR editing a staff account.
public class StaffDialog extends JDialog {

    private final CarRentalManager mgr;
    private final User editing;   // null = adding
    private final Runnable onDone;

    private final JTextField nameF = Theme.createTextField("Full name");
    private final JTextField userF = Theme.createTextField("Username");
    private final JPasswordField passF = FormUtil.passwordField();
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"Front Desk", "Admin"});
    private final JLabel errorLbl = FormUtil.errorLabel();

    public StaffDialog(Frame owner, CarRentalManager mgr, User editing, User current, Runnable onDone) {
        super(owner, editing == null ? "Add Staff" : "Edit Staff", true);
        this.mgr = mgr;
        this.editing = editing;
        this.onDone = onDone;

        setSize(440, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        FormUtil.styleBox(roleBox);

        JPanel p = FormUtil.formPanel(editing == null ? "Add Staff" : "Edit Staff");
        FormUtil.addRow(p, "Full Name", nameF);
        FormUtil.addRow(p, "Username", userF);
        FormUtil.addRow(p, editing == null ? "Password (min 4 characters)" : "New Password (leave blank to keep)", passF);
        FormUtil.addRow(p, "Role", roleBox);

        if (editing != null) {
            nameF.setText(editing.getFullName());
            userF.setText(editing.getUsername());
            roleBox.setSelectedItem(editing instanceof Admin ? "Admin" : "Front Desk");
            if (editing == current) roleBox.setEnabled(false); // you can't change your own role
        }

        p.add(errorLbl);
        JButton save = FormUtil.submitButton(editing == null ? "Add Staff" : "Save Changes");
        save.addActionListener(e -> handleSave());
        p.add(save);
        setContentPane(p);
    }

    private void handleSave() {
        String pw = new String(passF.getPassword());
        boolean admin = "Admin".equals(roleBox.getSelectedItem());
        try {
            if (editing == null) {
                // PILLAR: Polymorphism - stored as a plain User either way.
                User u = admin ? new Admin(nameF.getText(), userF.getText(), pw)
                               : new FrontDesk(nameF.getText(), userF.getText(), pw);
                mgr.addStaff(u);
            } else {
                mgr.updateStaff(editing, nameF.getText(), userF.getText(), admin, pw);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            errorLbl.setText(ex.getMessage());
            return;
        }
        dispose();
        onDone.run();
    }
}
