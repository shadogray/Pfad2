package at.tfr.pfad.dao;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import at.tfr.pfad.model.MailMessage;

@Repository
public abstract class MailMessageRepository implements EntityRepository<MailMessage, Long>{

}
