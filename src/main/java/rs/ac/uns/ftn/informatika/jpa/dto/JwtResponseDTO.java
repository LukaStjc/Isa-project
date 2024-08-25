package rs.ac.uns.ftn.informatika.jpa.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class JwtResponseDTO {
  @NotNull(message = "Token cannot be null")
  private String token;

  @NotNull(message = "Token type cannot be null")  // Static value!
  private String type = "Bearer";

  @NotNull(message = "User ID cannot be null")
  private Integer id;

  @NotNull(message = "Email cannot be null")
  @Email(message = "Invalid email format")
  private String email;

  @NotNull(message = "Email cannot be null")
  private int discount_rate;

  @NotEmpty(message = "Roles list cannot be empty")
  private List<String> roles;
  boolean passwordChangeRequired;

  public JwtResponseDTO(String accessToken, Integer id, String email, int discount_rate, List<String> roles, boolean passwordChangeRequired) {
    this.token = accessToken;
    this.id = id;
    this.email = email;
    this.discount_rate = discount_rate;
    this.roles = roles;
    this.passwordChangeRequired = passwordChangeRequired;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public int getDiscount_rate() {
    return discount_rate;
  }

  public void setDiscount_rate(int discount_rate) {
    this.discount_rate = discount_rate;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getRoles() {
    return roles;
  }

  public boolean isPasswordChangeRequired() {
    return passwordChangeRequired;
  }

  public void setPasswordChangeRequired(boolean passwordChangeRequired) {
    this.passwordChangeRequired = passwordChangeRequired;
  }
}
