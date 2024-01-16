package at.tfr.pfad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jakarta.inject.Inject;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.validation.ValidationException;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.util.EmailValidator;
import at.tfr.pfad.util.QueryExecutor;
import at.tfr.pfad.util.TemplateUtils;

@RunWith(CdiTestRunner.class)
public class TestMailTools {

	@Inject
	private EntityManager em;
	@Inject
	private QueryExecutor qe;
	@Inject
	private MemberRepository memberRepo;
	@Inject
	private TemplateUtils tu;
	@Inject
	private EmailValidator emailValidator;
	
	private Member member;
	
	@Test
	public void testExecutor() throws Exception {
		List<List<Entry<String, Object>>> list = qe.execute("select m.name as Name, m.vorname as Vorname, m.email as Email from Member m", false);
		Assert.assertFalse(list.isEmpty());
	}
	
	@Test
	public void testExecutorWithLimit() throws Exception {
		
		em.getTransaction().begin();
		for (int i=0; i<10; i++) {
			Member m = new Member();
			m.setName("Name"+i);
			m.setVorname("Vorname"+i);
			m.setEmail(i+"test@test.at");
			em.persist(m);
			em.flush();
		}
		
		List<List<Entry<String, Object>>> list = qe.execute("select m.name as Name, m.vorname as Vorname, m.email as Email from Member m order by id", false);
		Assert.assertFalse(list.isEmpty());
		Assert.assertTrue(list.size() >= 4);
		System.out.println("members: " + list);

		List<List<Entry<String, Object>>> listLimit = qe.execute("select m.name as Name, m.vorname as Vorname, m.email as Email from Member m order by id limit 2", false);
		Assert.assertFalse(listLimit.isEmpty());
		Assert.assertEquals("LIMIT not working: "+listLimit, 2, listLimit.size());
		Assert.assertEquals("LIMIT produces wrong sublist: "+listLimit, list.subList(0,2), listLimit);

		listLimit = qe.execute("select m.name as Name, m.vorname as Vorname, m.email as Email from Member m order by id \t \n limit \t 2, \t \n 2", false);
		Assert.assertFalse(listLimit.isEmpty());
		Assert.assertEquals("FirstResult not working: "+listLimit, 2, listLimit.size());
		Assert.assertEquals("FirstResult produces wrong sublist: "+listLimit,list.subList(2,4), listLimit);
		em.getTransaction().rollback();
	}
	
	@Test
	public void testExecutorNoNames() throws Exception {
		List<List<Entry<String, Object>>> list = qe.execute("select m.name, m.vorname, m.email from Member m", false);
		Assert.assertFalse(list.isEmpty());
	}
	
	@Test
	public void testMailTemplate() throws Exception {
		
		String template = "Das ist eine Nachricht an ${Email}.";
		
		List<List<Entry<String, Object>>> list = qe.execute("select m.name as Name, m.vorname as Vorname, m.email as Email "
				+ " from Member m where m.id = "+member.getId(), false);

		List<Entry<String,Object>> map = list.get(0);
		String res = tu.replace(template, map);
		Assert.assertEquals("Replacement failed.", "Das ist eine Nachricht an email.", res);
	}
	
	@Test
	public void testMailTemplatePropNav() throws Exception {
		
		String template = "Das ist eine Nachricht an ${Member.email}.";
		
		List<List<Entry<String, Object>>> list = qe.execute("select m as Member, m.name as Name, m.vorname as Vorname, m.email as Email "
				+ " from Member m where m.id = "+member.getId(), false);

		List<Entry<String,Object>> map = list.get(0);
		String res = tu.replace(template, map);
		Assert.assertEquals("Replacement failed.", "Das ist eine Nachricht an email.", res);
	}
	
	@Test(expected = AddressException.class)
	public void testMailAddresses() throws Exception {
		String addr = "test@test@test.at;";
		MimeMessage mail = new MimeMessage(Session.getDefaultInstance(new Properties()));
		mail.setRecipients(RecipientType.TO, InternetAddress.parse(addr));
	}
	
	@Test
	public void testStringSubstitutor() throws Exception {
		String template = "Das ist ein ${test:+PositiveContent}";
		Map<String,Object> map = new HashMap<>();
		map.put("test", "REPLACEMENT");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein PositiveContent", result);
	}
	
	@Test
	public void testStringSubstitutorRecursive() throws Exception {
		String template = "Das ist ein ${test:+${bla:-PositiveContent}}";
		Map<String,Object> map = new HashMap<>();
		map.put("test", "REPLACEMENT");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein PositiveContent", result);
	}
	
	@Test
	public void testStringSubstitutorRecursiveReverse() throws Exception {
		String template = "Das ist ein ${bla:-${test:+PositiveContent}}";
		Map<String,Object> map = new HashMap<>();
		map.put("test", "REPLACEMENT");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein PositiveContent", result);
	}
	
	@Test
	public void testStringSubstitutorNormal() throws Exception {
		String template = "Das ist ein ${test:-PositiveContent}";
		Map<String,Object> map = new HashMap<>();
		map.put("test", "REPLACEMENT");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein REPLACEMENT", result);
	}
	
	@Test
	public void testStringSubstitutorNormalRecursive() throws Exception {
		String template = "Das ist ein ${${test:-PositiveContent}}";
		Map<String,Object> map = new HashMap<>();
		map.put("repl", "REPLACEMENT");
		map.put("test", "repl");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein REPLACEMENT", result);
	}
	
	@Test
	public void testStringSubstitutorNormalRecursiveDefault() throws Exception {
		String template = "Das ist ein ${${${bla:-test}}}";
		Map<String,Object> map = new HashMap<>();
		map.put("repl", "REPLACEMENT");
		map.put("test", "repl");
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein REPLACEMENT", result);
	}
	
	@Test
	public void testScriptedStringSubstitutor() throws Exception {
		Map<String,Object> map = new HashMap<>();
		map.put("repl", "REPLACEMENT");
		map.put("test", "repl");
		map.put("checked", true);
		map.put("unchecked", false);
		String template = "Das ist ein ${js:-if (checked) repl; else test;}";
		String result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein REPLACEMENT", result);
		
		template = "Das ist ein ${js:-if (unchecked) repl; else test;}";
		result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein repl", result);

		template = "Das ist ein ${${js:-if (unchecked) repl; else test;}}";
		result = tu.replace(template, map);
		Assert.assertEquals("positive replacement failed: " + result, "Das ist ein REPLACEMENT", result);
	}
	
	@Test
	public void testEmailValidator() throws Exception {
		String mails = " test@test.at; test@test.at , test@test.at, , , ; ;";
		Assert.assertNull("cleanup failed on null", EmailValidator.cleanEmail(null));
		Assert.assertEquals("cleanup failed", "test@test.at", EmailValidator.cleanEmail("   test@test.at  ,"));
		Assert.assertEquals("cleanup failed 2", "test@test.at,test@test.at,test@test.at", EmailValidator.cleanEmail(mails));
		emailValidator.validate(null, null, "m-test.test@test.at");
		emailValidator.validate(null, null, "m-elisabeth.resch@gmx.at");
	}

	@Test(expected = ValidationException.class)
	public void testEmailValidatorForInvalid() throws Exception {
		emailValidator.validate(null, null, "m-test.test@");
	}

	EntityTransaction tx;
	@Before
	public void init() {

		tx = memberRepo.getTransaction();
		member = memberRepo.findBy(1L);
		if (member == null) {
			member = new Member();
			//member.setId(1L); pretty useless.. 
			member = memberRepo.merge(member);
		}
		member.setName("name");
		member.setVorname("vorname");
		member.setEmail("email");
		memberRepo.saveAndFlush(member);
		tx.commit();
		tx = memberRepo.getTransaction();
	}
	
	@After
	public void tearDown() {
		if (tx != null && tx.isActive())
			tx.rollback();
	}

	
}
