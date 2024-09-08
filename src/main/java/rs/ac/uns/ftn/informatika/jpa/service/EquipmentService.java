package rs.ac.uns.ftn.informatika.jpa.service;

import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentBasicDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.repository.EquipmentRepository;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

    private final Logger LOG = LoggerFactory.getLogger(Equipment.class);

    /*
     * Anotacijom @Cacheable i nazivom "product"
     * naznaceno je da se objekti tipa Product koji se dobave
     * metodom findOne smestaju u kes kolekciju "product"
     * kao i u ehcache.xml konfiguraciji
     */
    @Cacheable(value = "equipmentList", keyGenerator = "customKeyGenerator")
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> findByName(String text) {
        return equipmentRepository.findByNameStartingWith(text);
    }

    public List<Equipment> findByNameContainsAndCompany_AverageScoreGreaterThanEquals(String name, double score){
        return equipmentRepository.findByNameContainsAndCompany_AverageScoreGreaterThanEquals(name, score);
    }

    public List<Equipment> findByNameContainsAndTypeEquals(String name, EquipmentType type){
        return equipmentRepository.findByNameContainsAndTypeEquals(name, type);
    }

    public List<Equipment> findByNameContainsAndCompany_AverageScoreGreaterThanEqualsAndTypeEquals(
            String name, double score, int type){
        return equipmentRepository.
                findByNameContainsAndCompany_AverageScoreGreaterThanEqualsAndTypeEquals(name, score, type);
    }
    public Equipment save(Equipment equipment){
        return equipmentRepository.save(equipment);
    }

    // Nije kesirano, jer se ne koristi nigde gde bi imalo smisla
    public Equipment findBy(Integer id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    public void delete(Equipment equipment) {
        equipmentRepository.delete(equipment);
    }

    public void updateQuantity(Integer id, Integer quantity) throws DataAccessException {
        equipmentRepository.updateQuantity(id, quantity);
    }

    @Transactional
    public void saveAll(List<Equipment> equipmentList) {
        try {
            equipmentRepository.saveAll(equipmentList);
        } catch (StaleStateException e) {
            // Handle the stale state exception
            System.err.println("Stale state exception occurred: " + e.getMessage());
            // You can also log this exception or notify the user
//            throw new CustomException("One or more equipment items have been modified by another user. Please refresh and try again.");
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("An error occurred: " + e.getMessage());
//            throw new CustomException("An error occurred while saving the equipment list. Please try again.");
        }
    }

    @Transactional
    public Equipment findByIdAndLock(Integer id) {
        return equipmentRepository.findByIdAndLock(id);
    }


    public Equipment findByNameMQ(String name) {

        return equipmentRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with name: " + name));
    }
}
