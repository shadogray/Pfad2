package at.tfr.pfad.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.faces.convert.Converter;

import org.apache.commons.lang3.StringUtils;

public class ColumnModel {

	protected String key; 
	protected String value; 
	protected int index;
	protected boolean search = true;
	protected Boolean ascending;
	protected String filter;
	protected Map<String,?> items;
	protected Object selected;
	protected boolean autoComplete;
	protected int minLength;
	protected int maxLength;
	protected String headerStyle;
	protected String fieldStyle;
	protected String headerStyleValue;
	protected String fieldStyleValue;
	protected boolean headerStyleNotEmpty;
	protected boolean fieldStyleNotEmpty;
	protected Converter converter;
	
	public ColumnModel(String key, String value, int index) {
		this(key, value, index, null, null, null);
	}

	public ColumnModel(String key, String value, int index, Boolean ascending) {
		this(key, value, index, ascending, null, null);
	}

	public ColumnModel(String key, String value, int index, Boolean ascending, Map<String,Object> items) {
		this(key, value, index, ascending, null, items);
	}

	public ColumnModel(String key, String value, int index, Map<String,Object> items) {
		this(key, value, index, null, null, items);
	}

	public ColumnModel(String key, String value, int index, Boolean ascending, String filter, Map<String,Object> items) {
		this.key = key;
		this.value = value;
		this.index = index;
		this.ascending = ascending;
		this.filter = filter;
		this.items = items;
	}
	
	
	@Override
	public String toString() {
		return "ColumnModel [key=" + key + ", value=" + value + ", index=" + index + "]";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Boolean getAscending() {
		return ascending;
	}

	public void setAscending(Boolean ascending) {
		this.ascending = ascending;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public Map<String,?> getItems() {
		return items;
	}
	
	public List<Entry<String,?>> getItemEntries() {
		return items != null ? new ArrayList<>(items.entrySet()) : null;
	}
	
	public void setItems(Map<String,?> items) {
		this.items = items;
	}
	
	public Object getSelected() {
		return selected;
	}
	
	public void setSelected(Object selected) {
		this.selected = selected;
	}
	
	public boolean isAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}
	
	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String evalHeaderStyle(String value) {
		if (headerStyleNotEmpty) {
			return StringUtils.isNotEmpty(value) ? headerStyle : ""; 
		}
		if (headerStyleValue != null) {
			return headerStyleValue.equals(value) ? headerStyle : "";
		}
		return headerStyle;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public String getFieldStyle() {
		return fieldStyle;
	}

	public String evalFieldStyle(String value) {
		if (fieldStyleNotEmpty) {
			return StringUtils.isNotEmpty(value) ? fieldStyle : "";
		}
		if (fieldStyleValue != null) {
			return fieldStyleValue.equals(value) ? fieldStyle : "";
		}
		return fieldStyle;
	}

	public void setFieldStyle(String fieldStyle) {
		this.fieldStyle = fieldStyle;
	}
	
	public String getHeaderStyleValue() {
		return headerStyleValue;
	}

	public void setHeaderStyleValue(String headerStyleValue) {
		this.headerStyleValue = headerStyleValue;
	}
	
	public String getFieldStyleValue() {
		return fieldStyleValue;
	}
	
	public void setFieldStyleValue(String fieldStyleValue) {
		this.fieldStyleValue = fieldStyleValue;
	}
	
	public boolean isHeaderStyleNotEmpty() {
		return headerStyleNotEmpty;
	}

	public void setHeaderStyleNotEmpty(boolean headerStyleNotEmpty) {
		this.headerStyleNotEmpty = headerStyleNotEmpty;
	}

	public boolean isFieldStyleNotEmpty() {
		return fieldStyleNotEmpty;
	}

	public void setFieldStyleNotEmpty(boolean fieldStyleNotEmpty) {
		this.fieldStyleNotEmpty = fieldStyleNotEmpty;
	}

	public ColumnModel filter(String filter) {
		setFilter(filter);
		return this;
	}
	
	public ColumnModel items(Enum[] enm) {
		setItems(Arrays.stream(enm).collect(Collectors.toMap(e -> e.name(), e -> e.name())));
		return this;
	}
	
	public ColumnModel items(List<String> items) {
		setItems(items.stream().collect(Collectors.toMap(s -> s, s -> s)));
		return this;
	}
	
	public ColumnModel items(Map<String,?> items) {
		setItems(items);
		return this;
	}
	
	public ColumnModel ascending(Boolean asc) {
		this.ascending = asc;
		return this;
	}
	
	public ColumnModel autoComplete(boolean autoComplete) {
		setAutoComplete(autoComplete);
		return this;
	}

	public ColumnModel search(boolean search) {
		setSearch(search);
		return this;
	}
	
	public ColumnModel minLength(int minLength) {
		setMinLength(minLength);
		return this;
	}

	public ColumnModel maxLength(int maxLength) {
		setMaxLength(maxLength);
		return this;
	}

	public ColumnModel headerStyle(String headerStyle) {
		this.headerStyle = headerStyle;
		return this;
	}

	public ColumnModel fieldStyle(String fieldStyle) {
		this.fieldStyle = fieldStyle;
		return this;
	}

	public ColumnModel headerStyleValue(String headerStyleValue) {
		this.headerStyleValue = headerStyleValue;
		return this;
	}

	public ColumnModel fieldStyleValue(String fieldStyleValue) {
		this.fieldStyleValue = fieldStyleValue;
		return this;
	}

	public ColumnModel headerStyleNotEmpty(boolean headerStyleNotEmpty) {
		this.headerStyleNotEmpty = headerStyleNotEmpty;
		return this;
	}

	public ColumnModel fieldStyleNotEmpty(boolean fieldStyleNotEmpty) {
		this.fieldStyleNotEmpty = fieldStyleNotEmpty;
		return this;
	}
	
	public Object converted(Object value) {
		if (converter == null) return value;
		return converter.getAsString(null, null, value);
	}
	
	public Converter getConverter() {
		return converter;
	}
	
	public void setConverter(Converter converter) {
		this.converter = converter;
	}
	
	public ColumnModel colConverter(Converter converter) {
		setConverter(converter);
		return this;
	}
}
