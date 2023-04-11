package es.codeurjc.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.utilities.responseentity.ResponseBuilder;

@Service
public final class LoginService
{
    // TODO
    public ResponseEntity<?> doLoginFor(String username, String password)
    {
        return ResponseBuilder.badReq("TODO");
    }
}
