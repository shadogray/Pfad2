package at.tfr.pfad.util;

import java.io.Serializable;

public class ExecutorContext implements Serializable {

    private final boolean admin;

    public ExecutorContext(SessionBean sessionBean) {
        admin = sessionBean.isAdmin();
    }

    public boolean isAdmin() {
        return admin;
    }
}
