package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.repository.CourseRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.EquipmentRepository;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

}
