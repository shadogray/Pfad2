package at.tfr.pfad.model;
// Generated Feb 2, 2021, 9:27:45 PM by Hibernate Tools 5.2.11.Final


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * MailtemplateAudId generated by hbm2java
 */
@Embeddable
public class MailtemplateAudId  implements java.io.Serializable {


     private long id;
     private int rev;

    public MailtemplateAudId() {
    }

    public MailtemplateAudId(long id, int rev) {
       this.id = id;
       this.rev = rev;
    }
   


    @Column(name="ID", nullable=false)
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }


    @Column(name="REV", nullable=false)
    public int getRev() {
        return this.rev;
    }
    
    public void setRev(int rev) {
        this.rev = rev;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof MailtemplateAudId) ) return false;
		 MailtemplateAudId castOther = ( MailtemplateAudId ) other; 
         
		 return (this.getId()==castOther.getId())
 && (this.getRev()==castOther.getRev());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + (int) this.getId();
         result = 37 * result + this.getRev();
         return result;
   }   


}


