package at.tfr.pfad.util;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class FacesExceptionHandlerFactory extends ExceptionHandlerFactory {

    private final jakarta.faces.context.ExceptionHandlerFactory parent;

    public FacesExceptionHandlerFactory(final ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new FacesExceptionHandler(parent.getExceptionHandler());
    }

}