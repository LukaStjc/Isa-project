package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
@Tag(name = "Test Controllers", description = "Handles access to different levels of content based on user roles. It is used for testing purposes.")
public class TestController {
  @Operation(summary = "Retrieve public content.")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved public content!")
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @Operation(summary = "Access content for registered users.", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponse(responseCode = "200", description = "Access granted for registered user content!")
  @GetMapping("/user")
  @PreAuthorize("hasRole('REGISTERED_USER') or hasRole('COMPANY_ADMIN') or hasRole('SYSTEM_ADMIN')")
  public String userAccess() {
    return "Registered User Content.";
  }

  @Operation(summary = "Access content for company administrators.", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponse(responseCode = "200", description = "Access granted for company admin content!")
  @GetMapping("/company-admin")
  @PreAuthorize("hasRole('COMPANY_ADMIN')")
  public String companyAdminAccess() {
    return "Company Admin Board.";
  }

  @Operation(summary = "Access content for system administrators.", security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponse(responseCode = "200", description = "Access granted for system admin content!")
  @GetMapping("/system-admin")
  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  public String systemAdminAccess() {
    return "System Admin Board.";
  }


  //  @GetMapping("/all")
//  public String allAccess() {
//    return "Public Content.";
//  }
//
//  @GetMapping("/user")
//  @PreAuthorize("hasRole('REGISTERED_USER') or hasRole('COMPANY_ADMIN') or hasRole('SYSTEM_ADMIN')")
//  public String userAccess() {
//    return "Registered User Content.";
//  }
//
//  @GetMapping("/company-admin")
//  @PreAuthorize("hasRole('COMPANY_ADMIN')")
//  public String companyAdminAccess() {
//    return "Company Admin Board.";
//  }
//
//  @GetMapping("/system-admin")
//  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
//  public String systemAdminAccess() {
//    return "System Admin Board.";
//  }
}
