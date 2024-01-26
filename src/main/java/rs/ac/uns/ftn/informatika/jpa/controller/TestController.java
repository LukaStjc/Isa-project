package rs.ac.uns.ftn.informatika.jpa.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('REGISTERED_USER') or hasRole('COMPANY_ADMIN') or hasRole('SYSTEM_ADMIN')")
  public String userAccess() {
    return "Registered User Content.";
  }

  @GetMapping("/company-admin")
  @PreAuthorize("hasRole('COMPANY_ADMIN')")
  public String companyAdminAccess() {
    return "Company Admin Board.";
  }

  @GetMapping("/system-admin")
  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  public String systemAdminAccess() {
    return "System Admin Board.";
  }
}
