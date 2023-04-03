package at.tfr.pfad.dao;

import java.util.List;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.model.Configuration;

@Repository
public abstract class ConfigurationRepository implements EntityRepository<Configuration, Long>{

	public abstract Configuration findOptionalByCkey(String key);
	
	public String getValue(String key, String defaultValue) {
		Configuration cfg = findOptionalByCkey(key);
		if (cfg != null) {
			return cfg.getCvalue();
		}
		return defaultValue;
	}
	
	public abstract List<Configuration> findByTypeOrderByCkeyAsc(ConfigurationType type);
}
