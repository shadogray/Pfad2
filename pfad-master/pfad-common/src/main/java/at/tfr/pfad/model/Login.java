package at.tfr.pfad.model;

import at.tfr.pfad.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "LOGIN")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, insertable = false, updatable = false)
    private Long member_id;

    @ManyToOne(optional = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "groups", nullable = false)
    private Role group;

    public Login() {
    }

    public Login(Long id) {
        this.id = id;
    }

    public Login(Member member, Role group) {
        this.member = member;
        if (member != null) {
            this.member_id = member.getId();
        } else {
            this.member_id = null;
        }
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Role getGroup() {
        return group;
    }

    public void setGroup(Role group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Login[" + id + ", mId=" + member_id + ", group=" + group + ']';
    }
}
