package ksk_car3;

import javax.swing.*;

// RUN THIS CLASS. Opens the login page first.
public class KSK_Car {

    public static void main(String[] args) {
        CarRentalManager manager = new CarRentalManager();

        SwingUtilities.invokeLater(() -> {
            new LoginForm(manager).setVisible(true);
        });
    }
}
