package at.tfr.pfad;

import at.tfr.pfad.dao.MailMessageRepository;
import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.RegistrationRepository;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Registration;
import at.tfr.pfad.processing.ExecutionResult;
import at.tfr.pfad.processing.PfadCommandExecutor;
import at.tfr.pfad.processing.ReplacementResult;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(CdiTestRunner.class)
public class TestLargePersistence {

    @Inject
    private TestProvider provider;
    @Inject
    private PfadCommandExecutor exec;
    @Inject
    private MemberRepository memberRepo;
    @Inject
    private RegistrationRepository regRepo;
    @Inject
    private MailMessageRepository msgRepo;

    @Before
    public void before() throws Exception {
        provider.setLarge(true);
    }

    @Test
    public void testAccess() throws Exception {

        EntityManager em = provider.getExtendedEntityManagerLarge();
        Long count = em.createQuery("select count(m) from MailMessage m", Long.class).getSingleResult();
        assertNotNull("failed to find MailMessages", count);
        assertTrue("failed to find MailMessages?", count > 10);
    }

    @Test
    public void testExecuteCommand() throws Exception {
        /*
        3-BAD-16634	Neff	Barbara		false		0	Emil Kraft-Gasse 12/18	06644517941	barbara.neff@hotmail.com
        3-BAD-17180	Neff	Barbara		false		0	Emil Kraftgasse 12/18	06644517941	barbara.neff@hotmail.com
        */
        Member redNeff = memberRepo.findBy(17180L);
        final Member childToMove = redNeff.getSiblings().iterator().next();

        String CMD = PfadCommandExecutor.PFX + "moveToMember(16634,17180)";
        ExecutionResult result = exec.executeCommand(CMD);
        assertNotNull("failed to execute!", result);

        Member neff = memberRepo.findBy(16634L);
        assertNotNull("failed to find member", neff);

        redNeff = memberRepo.findBy(17180L);
        assertNotNull("failed to find member", redNeff);
        assertFalse("failed to assign siblings", neff.getSiblings().isEmpty());
        assertTrue("wrong parent assigned", neff.getSiblings().contains(childToMove));
        assertTrue("failed to DE-assign siblings", redNeff.getSiblings().isEmpty());
        List<Registration> regs = regRepo.findByParent(redNeff);
        assertTrue("failed to DE-assign Registrations", regs.isEmpty());
        // Verify reassignment of Registrations:
        regs = regRepo.findByParent(neff);
        assertFalse("failed to assign Registrations", regs.isEmpty());
        regs.forEach(r -> {
            assertEquals("wrong parent assigned", r.getMember().getParents().iterator().next(), neff);
            assertTrue("wrong parent assigned",
                    r.getMember().getParents().iterator().next().getSiblings().contains(childToMove));
        });
        memberRepo.remove(redNeff);
        memberRepo.flush();
        provider.getExtendedEntityManagerLarge().getTransaction().commit();
    }

    @Test
    public void testExecutorMoveRedundant() throws Exception {

        /*
        Macho Ilona:    3-BAD-16866, 3-BAD-17156
         */
        Long memberId = 16866L;
        Long redundantId = 17156L;

        // cleanup of old DB problems:
        Member Ilona = memberRepo.findBy(memberId);
        Member toBeReplaced = memberRepo.findBy(redundantId);
        Ilona.getFunktionen().addAll(toBeReplaced.getFunktionen());
        toBeReplaced.getFunktionen().clear();
        memberRepo.flush();

        ReplacementResult result = exec.moveToMember(memberId, redundantId);

        /*
        Rollinger Katharina (neue Obfrau ab 2024, bei beiden DB-Einträgen ist jeweils ein Kind eingetragen):
            3-BAD-17176
            3-BAD-118
         */
        memberId = 118L;
        redundantId = 17176L;

        result = exec.moveToMember(memberId, redundantId);

        assertFalse("failed to find Siblings!", result.getSiblings().isEmpty());
        assertFalse("failed to find Siblings?", result.getSiblings().isEmpty());
    }
}
