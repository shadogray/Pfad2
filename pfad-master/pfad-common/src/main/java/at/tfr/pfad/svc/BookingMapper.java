package at.tfr.pfad.svc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import at.tfr.pfad.model.Booking;
import at.tfr.pfad.model.Member;

@Mapper(uses={BaseDaoMapper.class})
public interface BookingMapper {

	BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);
	
	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
	})
	BookingDao bookingToDao(Booking booking);
	
	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
	})
	BaseDao memberToDao(Member member);

}
