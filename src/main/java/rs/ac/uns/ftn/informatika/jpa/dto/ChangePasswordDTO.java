package rs.ac.uns.ftn.informatika.jpa.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for changing a password")
public class ChangePasswordDTO {

    @Schema(description = "The ID of the user whose password is to be changed", example = "123")
    private Integer id;

    @Schema(description = "The new password for the user", example = "newSecurePassword123")
    private String password;
    public ChangePasswordDTO(Integer id, String password) {
        this.id = id;
        this.password = password;
    }

    public ChangePasswordDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
