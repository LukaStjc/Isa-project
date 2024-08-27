package rs.ac.uns.ftn.informatika.jpa.dto;

public class ChangePasswordDto {
    private Integer id;
    private String password;

    public ChangePasswordDto(Integer id, String password) {
        this.id = id;
        this.password = password;
    }

    public ChangePasswordDto() {
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