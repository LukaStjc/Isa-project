package rs.ac.uns.ftn.informatika.jpa.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;

import javax.persistence.LockModeType;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Equipment e WHERE e.id = :id")
    Equipment findByIdAndLock(@Param("id") Integer id);
}
