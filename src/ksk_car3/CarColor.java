package ksk_car3;
public enum CarColor {
    BLACK("Black"), SILVER("Silver"), RED("Red");
    private final String n;
    CarColor(String n) { this.n = n; }
    public String getDisplayName() { return n; }
    @Override public String toString() { return n; }
}
