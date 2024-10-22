package at.tfr.pfad.processing;

import at.tfr.pfad.dao.*;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Participation;
import at.tfr.pfad.util.EngineUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.util.*;

@ApplicationScoped
public class PfadCommandExecutor {

    final Logger log = Logger.getLogger(getClass());

    public static final String PFX = "cmd:";

    @Inject
    private MemberRepository memberRepo;
    @Inject
    private RegistrationRepository registrationRepo;
    @Inject
    private MailMessageRepository mailMessageRepo;
    @Inject
    private PaymentRepository paymentRepo;
    @Inject
    private BookingRepository bookingRepo;
    @Inject
    private SquadRepository squadRepo;
    @Inject
    private ParticipationRepository participationRepo;

    public ExecutionResult executeCommand(String command) throws Exception {
        if (StringUtils.isBlank(command)) {
            return new ExecutionResult();
        }

        if (command.startsWith(PFX)) {
            command = command.replaceAll(PFX, "cmdExec.");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("cmdExec", this);
        return (ExecutionResult) EngineUtil.evalObject(command, params);
    }

    public ReplacementResult moveToMember(Long oldMemberId, Long redundantMemberId) throws Exception {

        ReplacementResult result = new ReplacementResult();

        Member existing = memberRepo.fetchBy(oldMemberId);
        Member redundant = memberRepo.fetchBy(redundantMemberId);

        Objects.requireNonNull(existing, "no existing Member with Id: " + oldMemberId);
        Objects.requireNonNull(redundant, "no redundant Member with Id: " + redundantMemberId);
        if (existing.equals(redundant)) {
            throw new Exception("Members may not be equal! " + existing + " / " + redundant);
        }
        if (existing.getTrupp() != null || redundant.getTrupp() != null) {
            throw new Exception("Members may not be active! " + existing + " / " + redundant);
        }
        if (!redundant.getFunktionen().isEmpty()) {
            throw new Exception("RedMember may not be in Functions! " + redundant + " / " + redundant.getFunktionen());
        }
        if (!squadRepo.findByAssistant(redundant).isEmpty()) {
            throw new Exception("RedMember may not be Assistant! " + redundant);
        }
        List<Participation> participations = participationRepo.findByMember(redundant);
        if (!participations.isEmpty()) {
            throw new Exception("RedMember may not be in Participation! " + participations + " / " + redundant);
        }

        Iterator<Member> siblings = redundant.getSiblings().iterator();
        while (siblings.hasNext()) {
            Member sibling = siblings.next();
            existing.getSiblings().add(sibling);
            siblings.remove();
            memberRepo.flush();
            result.addSibling(sibling, redundant);
            log.info("moved: " + sibling + " to " + existing);
        }
        log.info("moved to " + existing + ": " + existing.getSiblings() + " from " + redundant);

        registrationRepo.findByParent(redundant).forEach(r -> {
            r.setParent(existing);
            registrationRepo.flush();
            result.addRegistration(r, redundant);
            log.info("moved: " + r + " to " + existing);
        });

        mailMessageRepo.findByMember(redundant).forEach(mm -> {
            mm.setMember(existing);
            mailMessageRepo.flush();
            result.addMailMessage(mm, redundant);
            log.info("moved: " + mm + " to " + existing);
        });

        bookingRepo.findByMember(redundant).forEach(b -> {
            b.setMember(existing);
            bookingRepo.flush();
            result.addBooking(b, redundant);
            log.info("moved: " + b + " to " + existing);
        });

        paymentRepo.findByPayer(redundant).forEach(p -> {
            p.setPayer(existing);
            paymentRepo.flush();
            result.addPayment(p, redundant);
            log.info("moved: " + p + " to " + existing);
        });

        return result;
    }

}
