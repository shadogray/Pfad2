package at.tfr.pfad.dao;

import at.tfr.pfad.model.Login;
import at.tfr.pfad.model.Training;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

@Repository
public abstract class LoginRepository implements EntityRepository<Login, Long>, CriteriaSupport<Login> {

}
