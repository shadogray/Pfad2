/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.PrimaryKeyHolder;
import at.tfr.pfad.util.ColumnModel;
import at.tfr.pfad.util.SessionBean;

public abstract class BaseDataModel<T extends PrimaryKeyHolder, U extends T> extends ListDataModel<U> {

    private static final int ABSOLUTE_MAX_ROWS = 200;
	private Logger log = Logger.getLogger(getClass());
    protected Class<T> entityClass;
    protected Class<U> uiClass;

    @Inject
    protected SessionBean sessionBean;
    @Inject
    protected EntityManager entityManager;
    
    protected int rows = 20;
    protected int rowStart = 0;
    protected List<Long> keys;
    protected boolean useUniquResultTransformer = false;
    protected String entityIdProperty = "id";
    private boolean rowsSetToUnlimited;
    protected CriteriaBuilder cb;
    protected Root<T> root;
    private CriteriaQuery<Long> query;
    private CriteriaQuery<Long> countQuery;
    private List<ColumnModel> columns = Collections.emptyList();

    public BaseDataModel() {
    }

    public BaseDataModel(final Class<T> entityClass) {
        this.entityClass = entityClass;
        this.uiClass = (Class<U>) entityClass;
    }

    public BaseDataModel(final Class<U> uiClass, final Class<T> entityClass) {
        this.entityClass = entityClass;
        this.uiClass = uiClass;
    }

    public List<U> convertToUiBean(final List<Long> ids) {
		CriteriaQuery<T> critEntity = cb.createQuery(entityClass).where(root.<Long>get("id").in(ids));

        TypedQuery<T> query = entityManager.createQuery(critEntity);
        query.setFirstResult(rowStart);
        query.setMaxResults(getRows() > 0 ? getRows() : ABSOLUTE_MAX_ROWS);

        return (List<U>) query.getResultList();
    }

    @Override
    public int getRowIndex() {
    	return super.getRowIndex();
    }
    
    public int getRows() {
        return rows;
    }

    public void setRows(final int rows) {
        rowsSetToUnlimited = (rows == Integer.MAX_VALUE ? true : false);
        this.rows = rows;
    }

    protected CriteriaQuery<Long> createCriteria(boolean addOrder) {
        cb = entityManager.getCriteriaBuilder();
		query = cb.createQuery(Long.class);
        root = getRoot();
        query.select(root.<Long>get("id"));
        root.alias(entityClass.getSimpleName());
        return query;
    }

	protected Root<T> getRoot() {
		return query.from(entityClass);
	}

	protected Root<T> getCountRoot() {
		return countQuery.from(entityClass);
	}

	public Predicate getSplittedPredicateName(Join<?,Member> memberJoin, String val) {
		List<Predicate> ors = Stream.of(val.toLowerCase().split(" +")).map(v -> 
			cb.or(cb.like(cb.lower(memberJoin.get(Member_.name)), "%"+v+"%"),
				cb.like(cb.lower(memberJoin.get(Member_.vorname)), "%"+v+"%")))
		.collect(Collectors.toList());
		Predicate and = cb.and(ors.toArray(new Predicate[ors.size()]));
		return and;
	}

	public Predicate getSplittedPredicateName(Path<String> path, String val) {
		List<Predicate> preds = Stream.of(val.toLowerCase().split(" +")).map(v -> cb.like(cb.lower(path), "%"+v+"%"))
		.collect(Collectors.toList());
		Predicate and = cb.and(preds.toArray(new Predicate[preds.size()]));
		return and;
	}

    protected CriteriaQuery<Long> createSelectCriteriaQuery() {
        final CriteriaQuery<Long> criteria = createCriteria(true);

        final List<Predicate> filterCriteria = createFilterCriteria(criteria);
        if (filterCriteria != null) {
            criteria.where(filterCriteria.toArray(new Predicate[]{}));
        }

        return criteria;
    }

    protected List<Order> createOrders(Root<T> path) {
        final List<Order> orders = new ArrayList<>();

        final List<ColumnModel> sortFields = columns.stream().filter(c->c.getAscending()!=null).collect(Collectors.toList());
        if (sortFields != null && !sortFields.isEmpty()) {

            for (final ColumnModel sortField : sortFields) {
                final String propertyName = 
                		getEntitySortProperty((String) sortField.getKey());

                Order order;
                final Boolean sortOrder = sortField.getAscending();
                if (sortOrder) {
                    order = cb.asc(getPathForOrder(path, propertyName));
                } else {
                    order = cb.desc(getPathForOrder(path, propertyName));
                }

                orders.add(order);
            }
        }

        return orders;
    }

	protected Path<Object> getPathForOrder(Root<T> path, final String propertyName) {
		return path.get(propertyName);
	}
    
    protected String getEntitySortProperty(String sortField) {
    	return sortField;
    }

    protected Class<U> getEntityClass() {
        return uiClass;
    }

    protected Predicate createFilterCriteriaForField(final String propertyName, final Object filterValue, CriteriaQuery<?> q) {
        
    	if (filterValue == null || (filterValue instanceof String && StringUtils.isEmpty((String) filterValue))) {
            return null;
        }

        try {
            final Method method = entityClass.getMethod("get" + StringUtils.capitalize(propertyName), new Class[] {});
            if (method != null) {
                final Class<?> returnType = method.getReturnType();
                if (Date.class.isAssignableFrom(returnType)) {
                    final String value = filterValue.toString().trim();
                    final DateTime date = new DateTime(DateUtils.parseDate(value, "dd", "dd.MM", "dd.MM.yyyy", "dd HH", "dd.MM HH", "dd.MM.YYYY HH"));

                    if (value.contains(" ")) { // the hour was specified
                        return cb.between(root.get(propertyName), date.toDate(), date.plusHours(1).toDate());
                    }
                    return cb.between(root.get(propertyName), date.toDate(), date.plusDays(1).toDate());

                } else if (Integer.class.isAssignableFrom(method.getReturnType())) {
                    return cb.equal(getPathForOrder(root, propertyName), Integer.parseInt(filterValue.toString()));

                } else if (Long.class.isAssignableFrom(method.getReturnType())) {
                    return cb.equal(getPathForOrder(root, propertyName), Long.parseLong(filterValue.toString()));

                } else if (Float.class.isAssignableFrom(method.getReturnType())) {
                    return cb.equal(getPathForOrder(root, propertyName), Float.parseFloat(filterValue.toString()));

                } else if (Double.class.isAssignableFrom(method.getReturnType())) {
                    return cb.equal(getPathForOrder(root, propertyName), Double.parseDouble(filterValue.toString()));

                } else if (Boolean.class.isAssignableFrom(method.getReturnType())) {
                    return cb.equal(getPathForOrder(root, propertyName), Boolean.parseBoolean(filterValue.toString()));

                } else if (filterValue instanceof String) {
                    String stringValue = (String) filterValue;
                    stringValue = stringValue.toLowerCase();
                    return cb.like(cb.lower(root.get(propertyName)), "%"+stringValue+"%");
                }
            }
        } catch (final Exception e) {
            log.debug("cannot handle value=" + filterValue + " for property=" + propertyName, e);
        }

        return cb.like(root.get(propertyName), filterValue.toString());
    }
    
    private List<Predicate> createFilterCriteria(CriteriaQuery<?> criteriaQuery) {
        List<Predicate> filterCriteria = new ArrayList<>();

        if (sessionBean.isLeiter()) {
//            filterCriteria = Restrictions.conjunction();
//            filterCriteria.add(Restrictions.eq(getAccountProperty(), getAccountValue(smsUsers)));
        }

        if (null == columns || columns.size() == 0) {
            return filterCriteria;
        }

        final List<ColumnModel> filterFields = columns.stream().
        		filter(c->StringUtils.isNotBlank(c.getFilter())).collect(Collectors.toList());
        if (filterFields != null && !filterFields.isEmpty()) {
            final FacesContext facesContext = FacesContext.getCurrentInstance();

            for (final ColumnModel filterField : filterFields) {
                final String propertyName = (String) filterField.getKey();
                final Object filterValue = filterField.getFilter();

                final Predicate crit = createFilterCriteriaForField(propertyName, filterValue, criteriaQuery);

                if (crit == null) {
                    continue;
                }

                filterCriteria.add(crit);
            }
        }
        return filterCriteria;
    }

	public void filter(FacesEvent event) {
		boolean reload = false;
		String value = null;
		ELContext el = FacesContext.getCurrentInstance().getELContext();
		ValueExpression ve = event.getComponent().getValueExpression("column");
		if (ve != null) {
			Object compValue = ve.getValue(el);
			if (compValue instanceof ColumnModel) {
				ColumnModel column = (ColumnModel)compValue;
				value = column.getFilter();
				reload = value == null || value.length() >= column.getMinLength();
			}
		} else {
			reload = true;
		}
		if (reload) reloadRowData();
	}
	
	public void reload(FacesEvent event) {
		reloadRowData();
	}
	
    public List<U> reloadRowData() {
    	setWrappedData(null);
    	return loadRowData();
    }
    
    @SuppressWarnings("unchecked")
	protected List<U> loadRowData() {
    	if (getWrappedData() == null) {
    		List<U> uData = loadRowDataInternal();
    		keys = uData.stream().map(d -> d.getId()).collect(Collectors.toList());
    		setWrappedData(uData);
    	}
    	return (List<U>)getWrappedData();
    }

	private List<U> loadRowDataInternal() {
		final CriteriaQuery<Long> criteria = createSelectCriteriaQuery();
		//criteria.groupBy(getGroupByRoots());
		List<Long> ids = entityManager.createQuery(criteria.distinct(true))
				.setFirstResult(rowStart)
				.setMaxResults(rows)
				.getResultList();
        List<U> uData = convertToUiBean(ids);
        return uData;
	}

	protected Path<T>[] getGroupByRoots() {
		return new Path[] { root };
	}

    @SuppressWarnings("unchecked")
	public List<U> getData() {
		return (List<U>)getWrappedData();
	}
    
    public int getRowStart() {
		return rowStart;
	}
    
    public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}
    
    public int getCurrentRows() {
        return getRowCount();
    }

    public boolean isRenderScroller() {
        return getRowCount() == rows || rowStart > 0;
    }

    public void setRowsUnlimited() {
        setRows(Integer.MAX_VALUE);
    }

    public boolean isRowsSetToUnlimited() {
        return rowsSetToUnlimited;
    }

    public boolean isUseUniquResultTransformer() {
        return useUniquResultTransformer;
    }

    public void setUseUniquResultTransformer(final boolean useUniquResultTransformer) {
        this.useUniquResultTransformer = useUniquResultTransformer;
    }
    
    public List<ColumnModel> getColumns() {
		return columns;
	}
    
    public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

    public Class<U> getUiClass() {
        return uiClass;
    }

    public void clear() {
        setWrappedData(null);
    }

    public void clearSelection(AjaxBehaviorEvent event) {
    }
    
}
