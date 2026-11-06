package ksk_car3;

import javax.swing.JFrame;

// PILLAR: Abstraction - User is abstract; subclasses decide their role and dashboard.
// PILLAR: Encapsulation - private fields, validated setters, password has no getter.
public abstract class User {

    private String fullName;
    private String username;
    private String password;

    public User(String fullName, String username, String password) {
        setFullName(fullName);
        setUsername(username);
        setPassword(password);
    }

    public abstract String getRole();
    public abstract JFrame openDashboard(CarRentalManager mgr);

    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public boolean checkPassword(String attempt) { return password.equals(attempt); }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("Full name cannot be empty.");
        this.fullName = fullName.trim();
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters.");
        this.password = password;
    }

    // Same account, different role (a Java object can't change its class, so we make a new one).
    public User changeRole(boolean toAdmin) {
        return toAdmin ? new Admin(fullName, username, password) : new FrontDesk(fullName, username, password);
    }
}
