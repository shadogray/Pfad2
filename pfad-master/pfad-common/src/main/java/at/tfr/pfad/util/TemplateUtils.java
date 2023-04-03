package at.tfr.pfad.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;


public class TemplateUtils implements Serializable {

	public static final Pattern pFilter = Pattern.compile("<p[^>/]*?>");
	public static final Pattern pEndFilter = Pattern.compile("<(/p|p/)>");
	public static final Pattern liFilter = Pattern.compile("<li>");
	public static final Pattern imgFilter = Pattern.compile("<img.+?\">");
	public static final Pattern tagFilter = Pattern.compile("<[^>]+>");
	public static final Pattern nbspFilter = Pattern.compile("&nbsp;");
	public static final Pattern entityFilter = Pattern.compile("&\\w+;");
	
    private PropertyUtilsBean pub = new PropertyUtilsBean();

	public String replace(String template, Map<String, Object> map) {
		return replace(template, map, null);
	}

	public String replace(String template, Map<String, Object> map, String defaultValue) {
		return replace(template, map.entrySet(), defaultValue);
	}

	public String replace(String template, Collection<Entry<String, Object>> map, String defaultValue) {
		String val = replace(template, map);
		if (StringUtils.isBlank(val))
			return defaultValue;
		return val;
	}

	public String replace(String template, Collection<Entry<String, Object>> map) {
		Map<String, Object> vals = new HashMap<>();
		map.forEach(e -> vals.put(e.getKey(), e.getValue()));
		StrSubstitutor strSub = new PositiveStrSubstitutor(new BeansStrLookup(map)).withValues(vals);
		strSub.setEnableSubstitutionInVariables(true);
		return strSub.replace(template);
	}

	public class BeansStrLookup extends StrLookup<String> {

		private Collection<Entry<String, Object>> map;

		public BeansStrLookup(Collection<Entry<String, Object>> map) {
			this.map = map;
		}

		@Override
		public String lookup(final String key) {
			Optional<Entry<String, Object>> opt = map.stream().filter(e -> key.toLowerCase().startsWith(e.getKey().toLowerCase())).findFirst();
			if (opt.isPresent()) {
				if (opt.get().getKey().equalsIgnoreCase(key)) {
					return "" + opt.get().getValue();
				}
				if (key.toLowerCase().startsWith(opt.get().getKey().toLowerCase() + ".")) {
					try {
						return "" + pub.getProperty(opt.get().getValue(), key.substring(opt.get().getKey().length() + 1));
					} catch (Exception e) {}
				}
			}
			return "";
		}
	}
	
	public static String htmlToText(String html) {
		if (StringUtils.isBlank(html))
				return html;
		html.replaceAll(imgFilter.pattern(), "");
		html = html.replaceAll(liFilter.pattern(), "\t");
		html = html.replaceAll(pEndFilter.pattern(), "\n");
		html = html.replaceAll(tagFilter.pattern(), "");
		html = html.replaceAll(nbspFilter.pattern(), " ");
		html = html.replaceAll(entityFilter.pattern(), " ");
		return html;
	}

}
