package com.example.sb.sftp.rest.config;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
  @Override
  public Response toResponse(NotFoundException e) {
    return Response.status(Response.Status.NOT_FOUND)
      .entity(Optional.ofNullable(e.getMessage()).orElse("The resource you've requested, has not been found!"))
      .type(MediaType.TEXT_PLAIN)
      .build();
  }
}
