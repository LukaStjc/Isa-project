package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentBasicDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.EquipmentType;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.repository.EquipmentRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

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

    public Equipment findBy(Integer id) {
        return equipmentRepository.findById(id).orElseGet(null);
    }

    public void delete(Equipment equipment) {
        equipmentRepository.delete(equipment);
    }

    public void updateQuantity(Integer id, Integer quantity) throws DataAccessException {
        equipmentRepository.updateQuantity(id, quantity);
    }

    public void saveAll(List<Equipment> equipmentList) {
        equipmentRepository.saveAll(equipmentList);
    }
}
