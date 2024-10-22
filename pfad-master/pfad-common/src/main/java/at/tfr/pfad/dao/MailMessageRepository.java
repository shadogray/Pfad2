package at.tfr.pfad.dao;

import at.tfr.pfad.model.MailMessage;
import at.tfr.pfad.model.Member;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public abstract class MailMessageRepository implements EntityRepository<MailMessage, Long>{

    //@Query("select mm from MailMessage mm where mm.member = ?")
    public abstract List<MailMessage> findByMember(Member member);


}
