package ksk_car3;

import javax.swing.*;
import java.awt.*;

// "My Profile": change your own name and password (used by Admin and POS).
public class ProfileDialog extends JDialog {

    private final User user;
    private final Runnable onSaved;
    private final JTextField nameF = Theme.createTextField("Full name");
    private final JPasswordField curPass = FormUtil.passwordField();
    private final JPasswordField newPass = FormUtil.passwordField();
    private final JPasswordField confPass = FormUtil.passwordField();
    private final JLabel errorLbl = FormUtil.errorLabel();

    public ProfileDialog(Frame owner, User user, Runnable onSaved) {
        super(owner, "My Profile", true);
        this.user = user;
        this.onSaved = onSaved;
        setSize(440, 640);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel p = FormUtil.formPanel("My Profile");

        JLabel info = new JLabel(user.getRole() + "  \u2022  @" + user.getUsername());
        info.setFont(Theme.FONT_SUBTITLE);
        info.setForeground(Theme.TEXT_GRAY);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        p.add(info);

        nameF.setText(user.getFullName());
        FormUtil.addRow(p, "Full Name", nameF);
        FormUtil.addRow(p, "Current Password (only to change it)", curPass);
        FormUtil.addRow(p, "New Password (leave blank to keep)", newPass);
        FormUtil.addRow(p, "Confirm New Password", confPass);

        p.add(errorLbl);
        JButton save = FormUtil.submitButton("Save Changes");
        save.addActionListener(e -> handleSave());
        p.add(save);
        setContentPane(p);
    }

    private void handleSave() {
        try {
            if (nameF.getText().trim().isEmpty()) throw new IllegalArgumentException("Full name cannot be empty.");
            String np = new String(newPass.getPassword());
            if (!np.isEmpty()) {
                if (!user.checkPassword(new String(curPass.getPassword()))) {
                    throw new IllegalArgumentException("Current password is incorrect.");
                }
                if (!np.equals(new String(confPass.getPassword()))) {
                    throw new IllegalArgumentException("New passwords do not match.");
                }
                user.setPassword(np);
            }
            user.setFullName(nameF.getText());
        } catch (IllegalArgumentException ex) {
            errorLbl.setText(ex.getMessage());
            return;
        }
        dispose();
        onSaved.run();
    }
}
