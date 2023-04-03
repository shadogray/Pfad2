package at.tfr.pfad.util;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class FacesExceptionHandlerFactory extends ExceptionHandlerFactory {

    private final javax.faces.context.ExceptionHandlerFactory parent;

    public FacesExceptionHandlerFactory(final ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new FacesExceptionHandler(parent.getExceptionHandler());
    }

}