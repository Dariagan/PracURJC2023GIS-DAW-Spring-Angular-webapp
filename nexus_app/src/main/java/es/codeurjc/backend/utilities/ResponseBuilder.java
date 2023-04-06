package es.codeurjc.backend.utilities;

import org.springframework.http.ResponseEntity;

public class ResponseBuilder
{
    public static ResponseEntity<?> badReq(String info)
    {
        return ResponseEntity
            .badRequest()
            .body(info);
    }

}
