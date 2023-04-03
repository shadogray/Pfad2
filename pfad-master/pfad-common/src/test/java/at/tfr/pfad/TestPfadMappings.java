package at.tfr.pfad;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.SquadRepository;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Membera;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.svc.BaseDao;
import at.tfr.pfad.svc.BaseDaoMapper;
import at.tfr.pfad.svc.SquadDao;
import at.tfr.pfad.svc.SquadMapper;

@RunWith(CdiTestRunner.class)
public class TestPfadMappings {

	@Inject
	private SquadMapper sm;
	@Inject
	private BaseDaoMapper bdm;
	@Inject
	private MemberRepository memberRepo;
	@Inject
	private SquadRepository squadRepo;
	
	@Test
	public void testSquadMapperNotModifiesScouts() throws Exception {
		
		final int NUM_SCOUTS = 5;
		Squad s = new Squad();
		s.setName("Squad");
		s.setType(SquadType.WIWO);
		final Squad saved = squadRepo.saveAndFlush(s);
		
		IntStream.range(0,NUM_SCOUTS).forEach(i->{
			Member m = new Member();
			m.setName("Scout"+i);
			m.setTrupp(saved);
			m = memberRepo.saveAndFlush(m);
		});

		squadRepo.clear();
		s = squadRepo.findBy(saved.getId());
		
		SquadDao dao = sm.squadToDao(s);
		assertTrue("Squad has no scouts", dao.getScouts().size() > 0);
		assertTrue("Squad has wrong number of scouts", dao.getScouts().size() == NUM_SCOUTS);
		Collection<Membera> scouts = Collections.unmodifiableCollection(new TreeSet<Membera>(s.getScouts()));
		
		sm.updateSquad(dao, s);
		assertTrue("Scouts have been manipulated", s.getScouts().size() == scouts.size());
		s.getScouts().stream().forEach(m ->
				assertTrue("Scouts have been manipulated", scouts.contains(m)));
		
	}

	@Test
	public void testSquadMapperModifiesAssistants() throws Exception {
		
		final int NUM_SCOUTS = 5;
		final int NUM_ASSISTANTS = 3;
		Squad s = new Squad();
		s.setName("Squad");
		s.setType(SquadType.WIWO);
		final Squad saved = squadRepo.saveAndFlush(s);
		
		IntStream.range(0,NUM_SCOUTS).forEach(i->{
			Member m = new Member();
			m.setName("Scout"+i);
			m.setTrupp(saved);
			m = memberRepo.saveAndFlush(m);
		});
		
		// Given: an unassociated Member for later addition to Squad.assistants
		Member theAssistant = new Member();
		theAssistant.setName("TheAssistant");
		theAssistant = memberRepo.saveAndFlush(theAssistant);

		IntStream.range(0,NUM_ASSISTANTS).forEach(i->{
			Member m = new Member();
			m.setName("Assistant"+i);
			m = memberRepo.saveAndFlush(m);
			Squad sq = squadRepo.findBy(saved.getId());
			sq.getAssistants().add(m);
			squadRepo.saveAndFlush(sq);
			squadRepo.flush();
		});

		squadRepo.clear();
		final Squad sCheck = squadRepo.findBy(saved.getId());
		
		// Given: an external representation of Squad
		SquadDao dao = sm.squadToDao(sCheck);
		assertTrue("Squad has no scouts", dao.getScouts().size() > 0);
		assertTrue("Squad has wrong number of scouts", dao.getScouts().size() == NUM_SCOUTS);
		Collection<Membera> scouts = Collections.unmodifiableCollection(new TreeSet<Membera>(sCheck.getScouts()));

		assertTrue("Squad has no assistants", dao.getAssistants().size() > 0);
		assertTrue("Squad has wrong number of scouts", dao.getAssistants().size() == NUM_ASSISTANTS);
		

		// When: we remove the first and add "theAssistant"
		dao.getAssistants().remove(dao.getAssistants().iterator().next());
		dao.getAssistants().add(bdm.toReference(theAssistant));
		Collection<BaseDao> assistants = Collections.unmodifiableCollection(dao.getAssistants());
		
		// When: the DAO is mapped to persistent Squad
		sm.updateSquad(dao, sCheck);
		
		// Then: the scouts have not changed:
		assertTrue("Scouts have been manipulated", sCheck.getScouts().size() == scouts.size());
		sCheck.getScouts().stream().forEach(m ->
				assertTrue("Scouts have been manipulated", scouts.contains(m)));
		
		// Then: the assistants have changed:
		sCheck.getAssistants().stream().forEach(m ->
				assertTrue("Assistants have NOT been changed", assistants.stream().anyMatch(d -> d.getId() == m.getId())));
		
	}

}
