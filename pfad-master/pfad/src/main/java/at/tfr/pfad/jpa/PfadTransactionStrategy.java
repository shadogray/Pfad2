package at.tfr.pfad.jpa;

import org.apache.deltaspike.jpa.spi.transaction.TransactionStrategy;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/**
 * @see deltaspike.ContanierBasedTransactionStrategy
 */
@Alternative
//@Priority(Interceptor.Priority.APPLICATION+1)
public class PfadTransactionStrategy implements TransactionStrategy {

	@Override
    public Object execute(InvocationContext invocationContext) throws Exception
    {
        return invocationContext.proceed();
    }
}
