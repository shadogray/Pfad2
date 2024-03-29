package at.tfr.pfad.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Consumes({"application/*+json", "text/json"})
@Produces({"application/*+json", "text/json"})
public class ConfigurationProvider extends ResteasyJackson2Provider {

	public ConfigurationProvider() {
		super(); // call basic configuration
		configure(SerializationFeature.INDENT_OUTPUT, true);
		configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
}
