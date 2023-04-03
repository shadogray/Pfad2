package at.tfr.pfad.svc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.dao.Members;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Membera;

@Stateless
public class MemberService {

	@Inject
	private MemberMapper mm;
	@Inject
	private MemberRepository memberRepo;
	@Inject
	private Members members;
	
	public MemberDao findBy(Long id) {
		return mm.memberToDao(memberRepo.fetchBy(id));
	}
	
	public List<MemberDao> filtered(final String filter, final Long truppId) {
		List<Member> list = members.filtered(filter, truppId);
		return map(list);
	}
	
	
	public MemberDao update(MemberDao dao) {
		Member m = memberRepo.findBy(dao.getId());
		mm.updateMember(dao, m);
		return mm.memberToDao(m);
	}
	
	public MemberDao save(MemberDao dao) {
		Member m = new Member();
		mm.updateMember(dao, m);
		memberRepo.save(m);
		return mm.memberToDao(m);
	}
	
	public List<MemberDao> map(Collection<Member> members) { 
		return members.stream().map(m -> mm.memberToDao(m)).collect(Collectors.toList());
	}
}
