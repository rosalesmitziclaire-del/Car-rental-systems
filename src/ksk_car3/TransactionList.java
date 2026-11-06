package ksk_car3;

import java.time.LocalDate;
import java.util.ArrayList;

// DATA STRUCTURE: hand-written singly linked list. New rentals go to the head (O(1)),
// so traversing from the head is already newest-first.
public class TransactionList {

    private static class Node {
        RentalTransaction data;
        Node next;
        Node(RentalTransaction data) { this.data = data; }
    }

    private Node head;
    private int size;

    public void addFirst(RentalTransaction t) {
        Node n = new Node(t);
        n.next = head;
        head = n;
        size++;
    }

    public int size() { return size; }

    public ArrayList<RentalTransaction> toArrayList() {
        ArrayList<RentalTransaction> out = new ArrayList<>();
        for (Node cur = head; cur != null; cur = cur.next) out.add(cur.data);
        return out;
    }

    public ArrayList<RentalTransaction> recent(int n) {
        ArrayList<RentalTransaction> out = new ArrayList<>();
        for (Node cur = head; cur != null && out.size() < n; cur = cur.next) out.add(cur.data);
        return out;
    }

    public int countProcessedOn(LocalDate day) {
        int count = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data.getDateProcessed().toLocalDate().isEqual(day)) count++;
        }
        return count;
    }

    public int countByStatus(TransactionStatus s) {
        int count = 0;
        for (Node cur = head; cur != null; cur = cur.next) if (cur.data.getStatus() == s) count++;
        return count;
    }

    public int countOverdue() {
        int count = 0;
        for (Node cur = head; cur != null; cur = cur.next) if (cur.data.isOverdue()) count++;
        return count;
    }

    // Cancelled rentals do not count as revenue.
    public double revenue() {
        double sum = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data.getStatus() != TransactionStatus.CANCELLED) sum += cur.data.getTotalFee();
        }
        return sum;
    }

    public double revenueOn(LocalDate day) {
        double sum = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            RentalTransaction t = cur.data;
            if (t.getStatus() != TransactionStatus.CANCELLED && t.getDateProcessed().toLocalDate().isEqual(day)) sum += t.getTotalFee();
        }
        return sum;
    }
}
