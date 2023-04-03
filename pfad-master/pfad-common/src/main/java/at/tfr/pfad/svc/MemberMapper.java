package at.tfr.pfad.svc;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Membera;
import at.tfr.pfad.model.Squad;

@Mapper(uses={BaseDaoMapper.class})
public interface MemberMapper {

	MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);
	
	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
		@Mapping(target="vollzahler", expression="java(baseDaoMapper.toReference(member.getVollzahler()))"),
		@Mapping(target="siblings", expression="java(baseDaoMapper.toReferences(member.getSiblings()))"),
		@Mapping(target="parents", expression="java(baseDaoMapper.toReferences(member.getParents()))"),
		@Mapping(target="funktionen", expression="java(baseDaoMapper.toReferences(member.getFunktionen()))"),
		@Mapping(target="geburtstag", expression="java(baseDaoMapper.memberGeburtstag(member))"),
		@Mapping(target="assisting", expression="java(baseDaoMapper.memberAssisting(member))"),
		@Mapping(target="responsible", expression="java(baseDaoMapper.memberResponsible(member))"),
		@Mapping(target="contacts", expression="java(baseDaoMapper.getContacts(member))"),
		@Mapping(target="uiString", expression="java(member.toUiString())"),
	})
	MemberDao memberToDao(Member member);

	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
	})
	BaseDao mapSquad(Squad squad);
	
	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
	})
	BaseDao mapFunction(Function function);
	
	@Mappings({
//		@Mapping(target="gebJahr", expression="java(baseDaoMapper.memberGebJahr(dao.getGeburtstag()))"),
//		@Mapping(target="gebMonat", expression="java(baseDaoMapper.memberGebMonat(dao.getGeburtstag()))"),
//		@Mapping(target="gebTag", expression="java(baseDaoMapper.memberGebTag(dao.getGeburtstag()))"),
		@Mapping(target="created", ignore=true),
		@Mapping(target="createdBy", ignore=true),
		@Mapping(target="changed", ignore=true),
		@Mapping(target="changedBy", ignore=true),
		@Mapping(target="payments", ignore=true),
		@Mapping(target="bookings", ignore=true),
		@Mapping(target="paymentsIds", ignore=true),
		@Mapping(target="bookingsIds", ignore=true),
	})
	void updateMember(MemberDao dao, @MappingTarget Member member);
	Set<Member> mapDaos(Set<MemberDao> members);

}
