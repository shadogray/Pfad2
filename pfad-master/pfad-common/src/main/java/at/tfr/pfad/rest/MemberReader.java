package at.tfr.pfad.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.model.Member;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class MemberReader implements MessageBodyReader<Member> {

	@Inject
	private MemberRepository memberRepo;
	@Context
	private Providers providers;

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Member.class.isAssignableFrom(type);
	}

	@Override
	public Member readFrom(Class<Member> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		Member member;
		Member tmp = new ObjectMapper().readValue(entityStream, Member.class);

		if (tmp.getId() != null) {
			member = memberRepo.findBy(tmp.getId());
			assert member.getVersion() == tmp.getVersion();
			RestUtil.update(member, tmp);
		} else {
			member = tmp;
		}
		return member;
	}
}
