package es.codeurjc.backend.service;

import es.codeurjc.backend.utilities.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LoginService
{
    // TODO
    public ResponseEntity<?> doLoginFor(String username, String password)
    {
        return ResponseBuilder.badReq("TODO");
    }
}
