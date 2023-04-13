package es.codeurjc.backend.service;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class UserQuerierService
{
    private final UserRepository userRepository;

    public ResponseEntity<Page<User>> getUsers(
        @RequestParam int page, @RequestParam int size
    ) {
        if (size > 10) return ResponseEntity.badRequest().build();
        Page<User> users = userRepository
            .findAllByOrderByNameDesc(PageRequest.of(page, size));

        return ResponseEntity
            .ok()
            .header("X-Total-Count", String.valueOf(users.getTotalElements()))
            .body(users);

    }
}
