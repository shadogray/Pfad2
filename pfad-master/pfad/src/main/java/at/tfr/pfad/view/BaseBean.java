/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.util.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author u0x27vo
 *
 */
public abstract class BaseBean<T,D> extends BaseBaseBean implements Serializable {

	protected String menuItem = "base";

    @Resource
    protected transient SessionContext sessionContext;

    protected T entity;

    @PostConstruct
    public void init() {
        log.debug("creating: " + sessionContext + " : " + Thread.currentThread() + " : " + this);
    }

    public void paginate(int page) {
        this.page = page;
        paginate();
    }

    public void updateAndClose() {
        update();
        close();
    }

    public void retrieve(Long id) {
        this.id = id;
        retrieve();
    }

    public void close() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String pfdlgcid = params.get(Constants.DialogFramework.CONVERSATION_PARAM);
        if (pfdlgcid != null) {
            PrimeFaces.current().dialog().closeDynamic(entity);
        }
    }

    public abstract List<D> getPageItems();
    public abstract String update();
    public abstract boolean isUpdateAllowed();
    abstract public void paginate();
    public abstract void retrieve();


    public int getPageItemsSize() {
        return getPageItems() != null ? getPageItems().size() : 0;
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

}