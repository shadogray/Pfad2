package at.tfr.pfad.dao;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import at.tfr.pfad.model.Training;

@Repository
public abstract class TrainingRepository implements EntityRepository<Training, Long>, CriteriaSupport<Training> {

}
