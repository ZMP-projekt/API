package controller;

import dto.MembershipDTO;
import lombok.RequiredArgsConstructor;
import model.MembershipType;
import service.MembershipService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/purchase")
    public void buy(@RequestParam MembershipType type, Authentication auth) {
        membershipService.purchaseMembership(auth.getName(), type);
    }

    @GetMapping("/me")
    public MembershipDTO getMyStatus(Authentication auth) {
        return membershipService.getUserMembership(auth.getName());
    }
}