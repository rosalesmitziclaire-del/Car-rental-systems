package ksk_car3;
public enum VehicleStatus {
    AVAILABLE("Available"), RENTED("Rented"), MAINTENANCE("Under Maintenance");
    private final String n;
    VehicleStatus(String n) { this.n = n; }
    public String getDisplayName() { return n; }
    @Override public String toString() { return n; }
}
