package at.tfr.pfad.svc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import at.tfr.pfad.model.Function;

@Mapper(uses={BaseDaoMapper.class}, componentModel = MappingConstants.ComponentModel.CDI)
public interface FunctionMapper {

	FunctionMapper INSTANCE = Mappers.getMapper(FunctionMapper.class);
	
	@Mappings({
		@Mapping(target="shortName", source="shortString"),
		@Mapping(target="longName", source="longString"),
	})
	FunctionDao functionToDao(Function function);
	
}
