package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

    @Query("SELECT e FROM Equipment e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Equipment> findByNameStartingWith(@Param("text") String text);

    @Query(value = "SELECT * FROM equipment e " +
            "JOIN company c ON e.company_id = c.id " +
            "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.average_score >= :score", nativeQuery = true)
    public List<Equipment> findByNameContainsAndCompany_AverageScoreGreaterThanEquals(
            @Param("name") String name, @Param("score") double score);

    public List<Equipment> findByNameContainsAndTypeEquals(String name, EquipmentType type);

    @Query(value = "SELECT * FROM equipment e " +
            "JOIN company c ON e.company_id = c.id " +
            "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.average_score >= :score " +
            "AND e.type = :type", nativeQuery = true)
    public List<Equipment> findByNameContainsAndCompany_AverageScoreGreaterThanEqualsAndTypeEquals(
            @Param("name") String name, @Param("score") double score, @Param("type") int type);

    @Modifying
    @Transactional
    @Query(value =  "UPDATE Equipment e " +
                    "SET e.quantity = :quantity " +
                    "WHERE e.id = :id")
    void updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity) throws DataAccessException;


    @Query(value = "select * from equipment e join company_admin ca on e.company_id = ca.company_id " +
            "where lower(e.name) like LOWER(CONCAT('%', :text, '%')) and ca.id = :companyAdminId", nativeQuery = true)
    public List<Equipment> findByName(String text, int companyAdminId);


    @Query(value = "select * from equipment e join company_admin ca on e.company_id = ca.company_id " +
            "where ca.id = :companyAdminId", nativeQuery = true)
    public List<Equipment> findAll(int companyAdminId);
}
