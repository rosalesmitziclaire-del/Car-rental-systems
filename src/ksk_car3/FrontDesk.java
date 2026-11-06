package ksk_car3;

import javax.swing.JFrame;

// PILLAR: Inheritance - FrontDesk extends User.
public class FrontDesk extends User {
    public FrontDesk(String fullName, String username, String password) { super(fullName, username, password); }

    // PILLAR: Polymorphism - opens the POS instead of the Admin dashboard.
    @Override public String getRole() { return "Front Desk Staff"; }
    @Override public JFrame openDashboard(CarRentalManager mgr) { return new POSDashboard(mgr, this); }
}
