package at.tfr.pfad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.RegistrationRepository;
import at.tfr.pfad.model.Member;

@RunWith(CdiTestRunner.class)
public class TestPfadRepositories {

	@Inject
	private MemberRepository memberRepo;
	@Inject
	private RegistrationRepository regRepo;
	@Inject
	private EntityManager em;
	
	@Test
	public void testMemberRepository() throws Exception {
		
		Member c = new Member();
		c.setName("Child");
		c = memberRepo.saveAndFlush(c);
		Member m = new Member();
		m.setName("Name");
		m.getSiblings().add(c);
		Member saved = memberRepo.saveAndFlush(m);
		
		assertTrue("save failed", saved.getId() != null);
		assertTrue("entity not found", em.find(Member.class, saved.getId()) != null);

		assertTrue("created not set", saved.getCreated() != null);
		
		List<Member> test = memberRepo.fetchAll(1);
		assertFalse("fetch failed", test.isEmpty());
		
		assertFalse("EntityGraph failed", saved.getSiblingIds().isEmpty());
		
	}
	
	@Test
	public void testRegistrationRepo() throws Exception {
		regRepo.findByNameVornameAndGeburtJahrMonatTag("name", "vorname", 2000, 1, 1);
	}
	
}
