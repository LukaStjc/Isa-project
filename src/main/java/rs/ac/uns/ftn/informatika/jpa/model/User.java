package rs.ac.uns.ftn.informatika.jpa.model;

import javax.persistence.*;

import static javax.persistence.InheritanceType.JOINED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

// POJO koji implementira Spring Security UserDetails interfejs koji specificira
// osnovne osobine Spring korisnika (koje role ima, da li je nalog zakljucan, istekao, da li su kredencijali istekli)
@Entity
@Inheritance(strategy=JOINED)
@Table(name="Userr")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="email")//(nullable = false, unique = true)
    private String email;

    @Column(name="firstName")//(nullable = false)
    private String firstName;

    @Column(name="lastName")//(nullable = false)
    private String lastName;

    @Column(name="password")//(nullable = false)
    private String password;

//    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT 'False'")
    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    public User(){
        super();
        enabled = false;    // za sada neka ostane kod user-a false
    }

    // todo: ali ako korisnik nije registered user, onda enabled treba da bude po default-u true? to su oni ucitani korisnici, ne ovi koji se registruju
    public User(String email, String firstName, String lastName, String password, boolean enabled) {
        super();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
    }

    public User(String email, String firstName, String lastName){
        super();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode("pass");
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Timestamp now = new Timestamp(new Date().getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // todo: ovde zezalo, jer u UserDetails imas samo za username metode
    @Override
    public String getUsername() {
        return email;
    }

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    // todo: ove tri metode mozda trebaju da se ispeglaju
    @JsonIgnore
    @Override   // logika?
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override   // ovo sigurno za sada na true, mozda implementirati dodatnu logiku
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override   // logika?
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
