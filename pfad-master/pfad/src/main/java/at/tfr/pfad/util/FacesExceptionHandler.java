package at.tfr.pfad.util;

import java.util.Iterator;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.faces.FacesException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.event.ExceptionQueuedEvent;

public class FacesExceptionHandler extends ExceptionHandlerWrapper {

    private final BeanManager beanManager;

    private final ExceptionHandler wrapped;

    public FacesExceptionHandler(final ExceptionHandler wrapped) {
        this.wrapped = wrapped;
        this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();
        while (it.hasNext()) {
            try {
                ExceptionQueuedEvent evt = it.next();
                // Fires the Event with the Exception (with expected Qualifier) to be handled
                //beanManager.fireEvent(evt);
            } finally {
                it.remove();
            }

        }
        getWrapped().handle();
    }
}