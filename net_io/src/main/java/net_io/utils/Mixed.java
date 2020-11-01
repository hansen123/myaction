package net_io.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;



import org.w3c.dom.Document;


public class Mixed {
	public static enum ENTITY_TYPE{NULL, STRING, LIST, MAP, MIXED, BYTES, PRIMITIVE};
	private Object data = null;
	private List<String> sequence = null;
	
	public Mixed() {
		
	}
	public Mixed(Object value) {
		set(value);
	}
	public ENTITY_TYPE type() {
		if(data == null) {
			return ENTITY_TYPE.NULL;
		} else if(data instanceof Boolean || data instanceof Short || data instanceof Integer
				|| data instanceof Long || data instanceof Float || data instanceof Double) {
			return ENTITY_TYPE.PRIMITIVE;
		} else if(data instanceof List) {
			return ENTITY_TYPE.LIST;
		} else if(data instanceof Map) {
			return ENTITY_TYPE.MAP;
		} else if(data instanceof TYPE) {
			return ENTITY_TYPE.MIXED;
		} else if(data instanceof byte[]) {
			return ENTITY_TYPE.BYTES;
		} else {
			return ENTITY_TYPE.STRING;
		}
		
	}
	
//	public String getString() {
//		return toString();
//	}
	
	/**
	 * 获取字符串的值。默认为空字符串
	 * @param key
	 * @return String
	 */
	public String getString(String key) {
		return getString(key, "");
	}
	/**
	 * 获取字符串的值
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public String getString(String key, String defaultValue) {
		Mixed result = _get(key);
		if(result == null || result.data == null) {
			return defaultValue;
		}
		return result.data.toString();
	}
	
	public byte[] getBytes(String key) {
		Mixed ret = _get(key);
		if(ret == null || ret.data == null) {
			return null;
		}
		if(ret.data instanceof byte[]) {
			return (byte[]) ret.data;
		}
		return ret.data.toString().getBytes();
	}
	public short getShort(String key) {
		return MixedUtils.parseShort(getString(key));
	}
	
	public int getInt(String key) {
		return MixedUtils.parseInt(getString(key));
	}
	
	public double getDouble(String key) {
		return MixedUtils.parseDouble(getString(key));
	}

	public float getFloat(String key) {
		return MixedUtils.parseFloat(getString(key));
	}

	/**
	 * 当前对象的内部数据，是否null
	 * @return boolean
	 */
	public boolean isSelfNull() {
		if(data == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 当前对象的内部数据，是否空值（范围：null或空值）
	 * @return boolean
	 */
	public boolean isSelfEmpty() {
		if(data == null) {
			return true;
		}
		if(data instanceof String) {
			return ((String)data).length() == 0;
		} else {
			return false;
		}
	}

	/**
	 * 当前对象的内部数据，是否true
	 *     排除范围：null、空字符串、0（含String类型）、false(大小写不敏感)
	 * @return boolean
	 */
	public boolean isSelfTrue() {
		if(data == null) {
			return false;
		}
		String str;
		switch(type()) {
			case STRING:
				str = (String) data;
				break;
			case PRIMITIVE:
				str = data.toString();
				break;
			default:
				return true;
		}
		if(str.length() == 0 || str.equals("0") || str.equalsIgnoreCase("false")) {
			return false;
		}
		return true;
	}

	/**
	 * 当前对象的内部数据，是否数字
	 * @return boolean
	 */
	public boolean isSelfNumeric() {
		if(data == null) {
			return false;
		}
		boolean ret = false;
		switch(type()) {
			case STRING:
				ret = MixedUtils.isNumeric((String) data);
				break;
			case PRIMITIVE:
				if(!(data instanceof Boolean)) {
					ret = true; //排除布尔型，都是数字
				}
				break;
		}
		return ret;
	}

	/**
	 * 指定KEY，是否null（KEY不存在，也按null处理）
	 * @param key
	 * @return boolean
	 */
	public boolean isNull(String key) {
		Mixed result = _get(key);
		if(result == null) {
			return true;
		}
		return result.isSelfNull();
	}

	/**
	 * 指定KEY，是否为空字符串
	 * @param key
	 */
	public boolean isEmpty(String key) {
		Mixed result = _get(key);
		if(result == null) {
			return true;
		}
		return result.isSelfEmpty();
	}

	/**
	 * 指定KEY，是否为TRUE
	 * @return 返回false的范围：null、空字符串、0（含String类型）、false(大小写不敏感)；除此之外，都返回true
	 */
	public boolean isTrue(String key) {
		Mixed result = _get(key);
		if(result == null) {
			return false;
		}
		return result.isSelfTrue();
	}
	
	/**
	 * 指KEY，是否为数字
	 * @param key
	 */
	public boolean isNumeric(String key) {
		Mixed result = _get(key);
		if(result == null) {
			return false;
		}
		return result.isSelfNumeric();
	}

	/**
	 * 是否存在key
	 * @param key
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean containsKey(String key) {
		if(data != null && data instanceof Map<?, ?>) {
			return ((Map<String, Mixed>)data).containsKey(key);
		}
		return false;
	}
	
	public String getString(int index) {
		Mixed result = get(index);
		if(result == null) {
			return "";
		}
		return result.toString();
	}
	
	public int size() {
		if(data == null) {
			return 0;
		} else if(data instanceof List) {
			return ((List<?>)data).size();
		} else if(data instanceof Map) {
			return ((Map<?, ?>)data).size();
		} else {
			return 0;
		}
	}
	
	public String[] keys() {
		if(data == null) {
			return new String[0];
		}
		int size = size();
		String[] keys = new String[size];
		switch(type()) {
		case MAP:
			for(int i=0; i<size; i++) {
				keys[i] = sequence.get(i);
			}
			break;
		case LIST:
			for(int i=0; i<size; i++) {
				keys[i] = String.valueOf(i);
			}
		default:
			break;
		}
		return keys;
	}
	
//	public Object get() {
//		return data;
//	}
	
	/**
	 * 获取Mixed对象中的核心Object对象
	 * @return 原始的Object对象（可为null）
	 */
	public Object getCoreObject() {
		return data;
	}
	public Mixed get(String key) {
		Mixed ret = _get(key);
		if(ret == null) ret = new Mixed();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private Mixed _get(String key) {
		if(data == null) {
			return null;
		}
		if(data instanceof Map<?, ?>) {
			return ((Map<String, Mixed>)data).get(key);
		} else if(data instanceof List<?>) {
			if(MixedUtils.isNumeric(key) == false) {
				return null;
			}
			int index = Integer.parseInt(key); 
			List<Mixed> list = (List<Mixed>) data;
			if(index < 0 || index >= list.size()) {
				return null;
			}
			return list.get(index);
		}
		return null;
	}
	
	public Mixed get(int index) {
		Mixed ret = _get(index);
		if(ret == null) ret = new Mixed();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private Mixed _get(int index) {
		Mixed ret = null;
		if(data != null && data instanceof List<?>) {
			if(((List<Mixed>)data).size() > index) {
				ret = ((List<Mixed>)data).get(index);
			}
		}
		return ret;
	}
	public Mixed find(String path) {
		String[] arr = path.split("/");
		Mixed result = this;
		for(String key : arr) {
			key = key.trim();
			if(key.length() == 0) continue; //ignore the empty key.
			result = findMe(result, key);
			if(result == null) return new Mixed(); //can not find the ActionResult
		}
		return result;
	}
	
	protected Mixed findMe(Mixed result, String key) {
		if(result.data == null) {
			return new Mixed();
		} else if(result.data instanceof List) {
			return result.get(MixedUtils.parseInt(key));
		} else {
			return result.get(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Mixed set(Object value) {
		if(value == null) {
			data = null;
			sequence = null;
		} else if(value instanceof Boolean || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			data = value;
			sequence = null;
		} else if(value instanceof byte[]) {
			data = value;
			sequence = null;
		} else if(value instanceof Object[]) {
			data = new ArrayList<Mixed>();
			sequence = null;
			for(Object v : (Object[])value) {
				((ArrayList<Mixed>)data).add(new Mixed(v));
			}
		} else if(value instanceof List) {
			data = new ArrayList<Mixed>();
			sequence = null;
			for(Object v : (List<?>)value) {
				((ArrayList<Mixed>)data).add(new Mixed(v));
			}
		} else if(value instanceof Mixed) {
			data = ((Mixed)value).data;
			sequence = ((Mixed)value).sequence;
		} else if(value instanceof TYPE) {
			Mixed tmp = ((TYPE) value).toMixed();
			data = tmp.data;
			sequence = tmp.sequence;
		} else if(value instanceof Map) {
			data = new HashMap<String, Mixed>();
			sequence = new ArrayList<String>();
			for(Object k : ((Map<?, ?>)value).keySet()) {
				String key = k.toString();
				((HashMap<String, Mixed>)data).put(
						key, new Mixed(((Map<?, ?>)value).get(k)));
				sequence.add(key);
			}
		} else {
			data = value.toString();
			sequence = null;
		}
		return this;
	}
	
	public Mixed put(String key, Object value) {
		return set(key, value);
	}
	@SuppressWarnings("unchecked")
	public Mixed set(String key, Object value) {
		//null也占KEY键。if(value == null) return this;
		if(data == null || !(data instanceof Map<?, ?>)) {
			data = new HashMap<String, Mixed>();
			sequence = new ArrayList<String>();
		}
		if(value instanceof Mixed) { //已经是ActionResult的对象了，不用转换
			if(((Map<String, Mixed>)data).containsKey(key) == false) { //先检查key是否已存在
				sequence.add(key);
			}
			((Map<String, Mixed>)data).put(key, (Mixed)value);
//		} else if(value instanceof Integer) { //已经是ActionResult的对象了，不用转换
//			if(((Map<String, Mixed>)data).containsKey(key) == false) { //先检查key是否已存在
//				sequence.add(key);
//			}
//			((Map<String, Mixed>)data).put(key, (Mixed)value);
		} else {
			if(((Map<String, Mixed>)data).containsKey(key) == false) { //先检查key是否已存在
				sequence.add(key);
			}
			((Map<String, Mixed>)data).put(key, new Mixed(value));
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Mixed set(int index, Object value) {
		if(data == null || !(data instanceof List<?>)) {
			data = new ArrayList<Mixed>();
			sequence = null;
		}
		((List<Mixed>)data).set(index, new Mixed(value));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Mixed add(Object value) {
		if(data == null || !(data instanceof List<?>)) {
			data = new ArrayList<Mixed>();
			sequence = null;
		}
		((List<Mixed>)data).add(new Mixed(value));
		return this;
	}
	
	public String toString() {
		if(data == null) {
			return "";
		}
		if(data instanceof Mixed) {
			Mixed mdata = (Mixed) data;
			if(mdata.data == null) {
				return "";
			}
			if(mdata == mdata.data) {
				throw new RuntimeException("Mixed data is self.");
			}
			return mdata.data.toString();
		} else {
			return data.toString();
		}
	}
	
	public String toJSON() {
		return JSONUtils.toJSON(this);
	}
	public Document toDOM() throws ParserConfigurationException {
		return JSONUtils.toDOM(this);
	}
	
	public static interface TYPE {
		public Mixed toMixed();
	}
}
