package ksk_car3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// First screen. Checks the staff list: POS / POS123 (Front Desk), Admin / admin123 (Admin), plus any staff added by the Admin.
public class LoginForm extends JFrame {

    private final CarRentalManager manager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginForm(CarRentalManager manager) {
        this.manager = manager;

        setTitle("SKS CAR - Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1170, 730);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        setContentPane(buildFormPanel());
    }

    // ---- Centered login form ----

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Theme.BG_MAIN);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 470));
        form.setMaximumSize(new Dimension(340, 470));

        JLabel logo = new JLabel("SKS CAR");
        logo.setFont(Theme.FONT_LOGO);
        logo.setForeground(new Color(200, 40, 50));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("Sign in");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your staff credentials to continue");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 28, 0));

        JLabel userLabel = fieldLabel("Username");
        usernameField = Theme.createTextField("Username");
        usernameField.setAlignmentX(LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel passLabel = fieldLabel("Password");
        passLabel.setBorder(new EmptyBorder(16, 0, 6, 0));
        passwordField = new JPasswordField();
        passwordField.setBackground(Theme.BG_FIELD);
        passwordField.setForeground(Theme.TEXT_WHITE);
        passwordField.setCaretColor(Theme.TEXT_WHITE);
        passwordField.setFont(Theme.FONT_BODY);
        passwordField.setBorder(new EmptyBorder(10, 12, 10, 12));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.addActionListener(e -> handleLogin());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(new Color(230, 100, 100));
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        errorLabel.setBorder(new EmptyBorder(14, 0, 6, 0));

        JButton signInButton = Theme.createPrimaryButton("Sign in");
        signInButton.setAlignmentX(LEFT_ALIGNMENT);
        signInButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        signInButton.addActionListener(e -> handleLogin());

        form.add(logo);
        form.add(title);
        form.add(subtitle);
        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);
        form.add(errorLabel);
        form.add(signInButton);

        outer.add(form);
        return outer;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_SMALL);
        label.setForeground(Theme.TEXT_GRAY);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));
        return label;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // PILLAR: Polymorphism - we don't care which subclass logged in; openDashboard() does the right thing.
        User user = manager.authenticate(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            return;
        }
        errorLabel.setText(" ");
        dispose();
        user.openDashboard(manager).setVisible(true);
    }

}
