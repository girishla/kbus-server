package com.bigmantra.kbus.security;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Converter
public class StringEncodingConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		// not converted back as it is a one-way hash
		return dbData;
	}

}
