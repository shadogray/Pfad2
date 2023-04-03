package at.tfr.pfad.rest;

import java.lang.reflect.Method;

import javax.validation.ValidationException;

import org.jboss.logging.Logger;

import at.tfr.pfad.Pfad;

public class RestUtil {

	private static Logger log = Logger.getLogger(RestUtil.class);
	
	public static <T> T update(T model, T update) {

		try {
			for (Method m : model.getClass().getDeclaredMethods()) {
				if (m.isAnnotationPresent(Pfad.class)) {
					if (m.getParameters().length == 0) { // getter - should be
						Method mutator = null;
						if (m.getName().startsWith("is") && m.getReturnType() == boolean.class) {
							mutator = model.getClass().getMethod("set" + m.getName().substring(2), m.getReturnType());
						} else {
							mutator = model.getClass().getMethod("set" + m.getName().substring(3),
								m.getReturnType());
						}
						if (mutator != null) {
							mutator.invoke(model, m.invoke(update));
						}
					}
				}
			}
		} catch (Exception e) {
			String message = "Model: " + model + "," + update + " err: " + e.getMessage();
			log.info(message, e);
			throw new ValidationException(message, e);
		}
		return model;
	}

}
