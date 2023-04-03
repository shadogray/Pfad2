package at.tfr.pfad.dao;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import at.tfr.pfad.model.MailTemplate;

@Repository
public abstract class MailTemplateRepository implements EntityRepository<MailTemplate, Long>{

}
