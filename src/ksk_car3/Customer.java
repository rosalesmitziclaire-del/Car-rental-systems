package ksk_car3;

// PILLAR: Encapsulation - private fields, setters validate the data.
public class Customer {

    private String fullName;
    private String email;
    private String contactNumber;

    public Customer(String fullName, String email, String contactNumber) {
        setFullName(fullName);
        setEmail(email);
        setContactNumber(contactNumber);
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        this.email = email.trim();
    }

    public void setContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().length() < 7) {
            throw new IllegalArgumentException("Please enter a valid contact number.");
        }
        this.contactNumber = contactNumber.trim();
    }

    @Override
    public String toString() {
        return fullName;
    }
}
