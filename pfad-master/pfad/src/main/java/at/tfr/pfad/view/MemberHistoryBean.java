package at.tfr.pfad.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;

@Named
@SessionScoped
public class MemberHistoryBean implements Serializable {

	private Member example = new Member();
	private transient List<Result> pageItems = new ArrayList<>();
	private int maxResults = 20;

	@Inject
	private transient AuditReader auditReader;

	public String search() {
		AuditQuery q = auditReader.createQuery().forRevisionsOfEntity(Member.class, false, false);

		if (StringUtils.isNotBlank(example.getBVKey())) {
			q.add(AuditEntity.property(Member_.bvKey.getName()).eq(example.getBVKey()));
		}
		if (StringUtils.isNotBlank(example.getName())) {
			q.add(AuditEntity.property(Member_.name.getName()).eq(example.getName()));
		}
		if (StringUtils.isNotBlank(example.getVorname())) {
			q.add(AuditEntity.property(Member_.vorname.getName()).eq(example.getVorname()));
		}
		List<Object[]> res = q.addOrder(AuditEntity.id().desc()).setMaxResults(maxResults).getResultList();

		pageItems = res.stream().map(a -> new Result((Member) a[0], (DefaultRevisionEntity) a[1], (RevisionType) a[2]))
				.collect(Collectors.toList());
		return null;
	}

	public List<Result> getPageItems() {
		return pageItems;
	}

	public Member getExample() {
		return example;
	}

	public void setExample(Member example) {
		this.example = example;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public static class Result {
		public Result() {
		}

		public Result(Member member, DefaultRevisionEntity rev, RevisionType type) {
			super();
			this.member = member;
			this.rev = rev;
			this.type = type;
		}

		Member member;
		DefaultRevisionEntity rev;
		RevisionType type;

		public Member getMember() {
			return member;
		}

		public DefaultRevisionEntity getRev() {
			return rev;
		}

		public RevisionType getType() {
			return type;
		}
	}
}
