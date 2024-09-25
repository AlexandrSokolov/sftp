package com.example.sb.sftp.rest.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

@Component
@Provider
public class CustomJacksonMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper mapper;

  public CustomJacksonMapperProvider() {

    mapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT) // enable pretty print
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) //do not fail on unknown fields
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());
  }

  @Override
  public ObjectMapper getContext(Class<?> aClass) {
    return mapper;
  }
}
