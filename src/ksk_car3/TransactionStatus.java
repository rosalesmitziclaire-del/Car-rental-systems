package ksk_car3;
public enum TransactionStatus {
    ONGOING("Ongoing"), COMPLETED("Completed"), CANCELLED("Cancelled");
    private final String n;
    TransactionStatus(String n) { this.n = n; }
    public String getDisplayName() { return n; }
    @Override public String toString() { return n; }
}
