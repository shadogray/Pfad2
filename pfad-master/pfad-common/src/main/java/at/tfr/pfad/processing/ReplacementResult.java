package at.tfr.pfad.processing;

import at.tfr.pfad.model.*;

import java.util.ArrayList;
import java.util.List;

public class ReplacementResult extends ExecutionResult {

    private List<IdAndReplaced> siblings = new ArrayList<>();
    private List<IdAndReplaced> registrations = new ArrayList<>();
    private List<IdAndReplaced> mailMessages = new ArrayList<>();
    private List<IdAndReplaced> bookings = new ArrayList<>();
    private List<IdAndReplaced> payments = new ArrayList<>();


    public void addSibling(Member sibling, Member redundant) {
        siblings.add(new IdAndReplaced(sibling, redundant));
    }

    public void addRegistration(Registration registration, Member redundant) {
        registrations.add(new IdAndReplaced(registration, redundant));
    }
    public void addMailMessage(MailMessage mail, Member redundant) {
        mailMessages.add(new IdAndReplaced(mail, redundant));
    }

    public void addBooking(Booking booking, Member redundant) {
        bookings.add(new IdAndReplaced(booking, redundant));
    }

    public void addPayment(Payment payment, Member redundant) {
        payments.add(new IdAndReplaced(payment, redundant));
    }

    public List<IdAndReplaced> getSiblings() {
        return siblings;
    }

    public List<IdAndReplaced> getRegistrations() {
        return registrations;
    }

    public List<IdAndReplaced> getMailMessages() {
        return mailMessages;
    }

    public List<IdAndReplaced> getBookings() {
        return bookings;
    }

    public List<IdAndReplaced> getPayments() {
        return payments;
    }

    public static class IdAndReplaced{
        private final BaseEntity entity;
        private final Member replaced;

        public IdAndReplaced(BaseEntity entity, Member replaced) {
            this.entity = entity;
            this.replaced = replaced;
        }

        public BaseEntity getEntity() {
            return entity;
        }

        public Long getId() {
            return entity.getId();
        }

        public Member getReplaced() {
            return replaced;
        }
    }

    @Override
    public String toString() {
        return "ReplacementResult[" + "sibs=" + siblings + ", regs=" + registrations + ", mMss=" + mailMessages +
                ", bkgs=" + bookings + ", pays=" + payments + ']';
    }
}

