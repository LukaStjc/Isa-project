package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.RegisteredUser;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

    RegisteredUser getByActivationCode(String activationCode);

    RegisteredUser getByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="0")}) // bitno je staviti 0
    public RegisteredUser save(RegisteredUser registeredUser);

    RegisteredUser findRegisteredUserById(Integer Id);

    RegisteredUser findRegisteredUserByEmail(String email);

}
