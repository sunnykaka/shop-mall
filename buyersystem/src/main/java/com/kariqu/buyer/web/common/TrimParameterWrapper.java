package com.kariqu.buyer.web.common;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 去参数的左右空格
 */
public class TrimParameterWrapper extends HttpServletRequestWrapper {

    private static final Pattern PATTERN = Pattern.compile("^\\s+|\\s+$");

    private Map<String, Object> map = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public TrimParameterWrapper(HttpServletRequest request) {
        super(request);
        map.putAll(request.getParameterMap());
        modifyParameterValues();
    }

    private void modifyParameterValues() {
        boolean change;
        Object obj;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            change = false;
            obj = entry.getValue();
            if (obj == null) continue;

            if (obj instanceof String[]) {
                String[] values = (String[]) obj;
                for (int i = 0; i < values.length; i++) {
                    // 若(参数为空)或(没有前后空格)则跳过
                    if (StringUtils.isEmpty(values[i]) || !PATTERN.matcher(values[i]).find())
                        continue;

                    values[i] = values[i].trim();
                    change = true;
                }
                obj = values;
            } else if (obj instanceof String) {
                String value = (String) obj;
                if (StringUtils.isNotEmpty(value) || PATTERN.matcher(value).find()) {
                    obj = value.trim();
                    change = true;
                }
            }
            if (change) {
                map.put(entry.getKey(), obj);
            }
        }
    }

    @Override
    public String getParameter(String name) {
        Object value = map.get(name);
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return ((String[]) value)[0];
        }
        return HtmlUtils.htmlEscape((String) value);
    }

    @Override
    public String[] getParameterValues(String name) {
        Object value = map.get(name);
        if (value == null)
            return null;
        if (value.getClass().isArray()) {
            String[] str = (String[]) value;
            String[] st = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                st[i] = HtmlUtils.htmlEscape(str[i]);
            }
            return st;
        }
        return new String[] { HtmlUtils.htmlEscape((String) value) };
    }

    @Override
    public Map getParameterMap() {
        return map;
    }

}
