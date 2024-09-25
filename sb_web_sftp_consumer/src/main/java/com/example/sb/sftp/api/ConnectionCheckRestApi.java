package com.example.sb.sftp.api;

import com.example.sb.sftp.api.dto.ConnectionsCheck;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path(ConnectionCheckRestApi.REST_PATH)
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ConnectionCheckRestApi {

  String REST_PATH = "/connections";

  @GET
  ConnectionsCheck connectionsCheck();
}
