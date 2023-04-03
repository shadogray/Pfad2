package at.tfr.pfad;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * This annotation declares the annotated as for business use:<br>
 * - REST: use for getters, declares the designated property for REST update
 * 
 * @author thomas
 */

@Qualifier
@Target({ METHOD, CONSTRUCTOR, FIELD, TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Pfad {

}
