package rs.ac.uns.ftn.informatika.jpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.model.User;


/*
 * Primer repozitorijuma u kojem su navedene metode koje po
 * odredjenoj konstrukciji naziva prave upit u bazu.
 * Metode za pretragu pocinju sa find*By* dok u nastavku sadrze
 * nazive atributa iz modela, konkatenirane sa And, Or, Between, LessThan, GreaterThan, Like, itd.
 * uz dodavanje pomocnih uslova poput Containing, AllIgnoringCase, itd.
 */
/*
 * Pri startovanju Spring kontejnera trigerovace se Spring Data
 * infrastruktura koja ce kreirati binove za repozitorijume.
 * Proci se kroz metode navedene u svakom repozitorijumu i
 * pokusati da konstruise upite koji ce se pozivati pri
 * pozivu metoda.
 */

/*
 * Repository je interfejs koji dozvoljava Spring Data infrastrukturi da
 * prepozna korisnicki definisane repozitorijume (alternativa je
 * da se sam interfejs anotira sa @Repository)
 * CrudRepository dodaje osnovne metode poput cuvanja, brisanja i pronalazenja entiteta
 * PagingAndSortingRepository nasledjuje CrudRepository i dodaje metode
 * za pristup entitetima stranicu po stranicu i njihovo soritiranje
 * JpaRepository nasledjuje PagingAndSortingRepository i dodaje JPA
 * specificne funkcionalnosti poput flush i deleteInBatch.
 *
 * Razliciti interfejsi koji se mogu iskoristiti dozvoljavaju manipulaciju razlicitim
 * vrstama metoda koje trebaju biti podrzane - npr. repozitorijum treba
 * da bude samo readonly ili treba da ima findAll metodu koja pritom treba
 * da vraca samo deo rezultata ogranicen pomocu Pageable.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
}
