package es.codeurjc.backend.utilities.responseentity;

import org.springframework.http.ResponseEntity;

// 13-A
public class ResponseBuilder
{
    public static ResponseEntity<?> badReq(String info)
    {
        return ResponseEntity
            .badRequest()
            .body(info);
    }
    public static ResponseEntity<?> badReq()
    {
        return ResponseEntity.badRequest().build();
    }

}
