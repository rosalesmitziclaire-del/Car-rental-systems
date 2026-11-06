package ksk_car3;

import javax.swing.JFrame;

// PILLAR: Inheritance - Admin extends User.
public class Admin extends User {
    public Admin(String fullName, String username, String password) { super(fullName, username, password); }

    // PILLAR: Polymorphism - same methods as FrontDesk, different behavior.
    @Override public String getRole() { return "Administrator"; }
    @Override public JFrame openDashboard(CarRentalManager mgr) { return new AdminDashboard(mgr, this); }
}
