package controller;

import dto.AccessResponseDTO;
import lombok.RequiredArgsConstructor;
import service.MembershipService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final MembershipService membershipService;

    @PostMapping("/check")
    public AccessResponseDTO validateAccess(Authentication auth) {
        return membershipService.checkAccess(auth.getName());
    }
}