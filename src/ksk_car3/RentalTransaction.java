package ksk_car3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// One rental: customer + vehicle + dates + fee + status (Ongoing / Completed / Cancelled).
public class RentalTransaction {

    private static int counter = 1000;

    private final String transactionId;
    private final Customer customer;
    private final Vehicle vehicle;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int totalDays;
    private final double totalFee;
    private final LocalDateTime dateProcessed;
    private final String processedBy;
    private TransactionStatus status = TransactionStatus.ONGOING;

    public RentalTransaction(Customer customer, Vehicle vehicle,
                              LocalDate startDate, LocalDate endDate,
                              String processedBy) {
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after the start date.");
        }
        this.transactionId = "TXN-" + (++counter);
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        this.totalFee = vehicle.calculateRentalFee(totalDays); // PILLAR: Polymorphism - Vehicle reference, Sedan/SUV fee decided at runtime
        this.dateProcessed = LocalDateTime.now();
        this.processedBy = processedBy;
    }

    public String getTransactionId() { return transactionId; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTotalDays() { return totalDays; }
    public double getTotalFee() { return totalFee; }
    public LocalDateTime getDateProcessed() { return dateProcessed; }
    public String getProcessedBy() { return processedBy; }
    public TransactionStatus getStatus() { return status; }

    public boolean isOverdue() {
        return status == TransactionStatus.ONGOING && LocalDate.now().isAfter(endDate);
    }

    // What the tables show: "Overdue" replaces "Ongoing" when the end date has passed.
    public String getStatusLabel() {
        return isOverdue() ? "Overdue" : status.getDisplayName();
    }

    // Only the manager changes status (it also frees the car).
    void markCompleted() {
        if (status != TransactionStatus.ONGOING) throw new IllegalStateException("Only ongoing rentals can be returned.");
        status = TransactionStatus.COMPLETED;
    }

    void markCancelled() {
        if (status != TransactionStatus.ONGOING) throw new IllegalStateException("Only ongoing rentals can be cancelled.");
        status = TransactionStatus.CANCELLED;
    }
}
