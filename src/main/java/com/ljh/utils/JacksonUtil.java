package com.ljh.utils;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.List;

public class JacksonUtil {
	private static ObjectMapper mapper = new ObjectMapper();
	 
	public static String bean2Json(Object obj)  {
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
			mapper.writeValue(gen, obj);
			gen.close();
			return sw.toString();
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}
 
	public static <T> T json2Bean(String jsonStr, Class<T> objClass)
			throws Exception {
		return mapper.readValue(jsonStr, objClass);
	}

	/**
	 * Json 转 List, Class 集合中泛型的类型，非集合本身
	 * @param text json
	 * @param <T>  对象类型
	 * @return List
	 */
	public static <T> List<T> toList(String text) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		try {
			return toObject(text, new TypeReference<List<T>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Json 转 Object
	 * @param text          json
	 * @param typeReference TypeReference
	 * @param <T>           类型
	 * @return T
	 */
	public static <T> T toObject(String text, TypeReference<T> typeReference) {
		try {
			if (StringUtils.isEmpty(text) || typeReference == null) {
				return null;
			}
			return (T) (typeReference.getType().equals(String.class) ? text : mapper.readValue(text, typeReference));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
