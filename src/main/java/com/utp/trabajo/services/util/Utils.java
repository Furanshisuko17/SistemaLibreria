package com.utp.trabajo.services.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utils {
    
    public static <T> void prettyPrintObject(T object) {
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
		String s = "Error when parsing!";
		try {
			s = om.writeValueAsString(object);
		} catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
		}
		System.out.println(s);
	}

}
