package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ContractDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ContractStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;
import rs.ac.uns.ftn.informatika.jpa.repository.CompanyRepository;
import rs.ac.uns.ftn.informatika.jpa.repository.ContractRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

    ContractRepository contractRepository;

    @Autowired
    public ContractService(RabbitTemplate rabbitTemplate, ContractRepository contractRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.contractRepository = contractRepository;
    }
    private  RabbitTemplate rabbitTemplate;

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    public ResponseEntity<?> create(ContractDTO dto, Company company, Hospital hospital, Equipment equipment) {

        Boolean hasActiveContract = checkIfActiveContractExists(hospital, company);
        if (hasActiveContract){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An active contract between this hospital and company already exists.");

        }
        Contract contract = new Contract();
        contract.setCompany(company);
        contract.setHospital(hospital);
        contract.setEquipment(equipment);
        contract.setDate(dto.getDate());
        contract.setQuantity(dto.getEquipment().getQuantity());
        contract.setStatus(ContractStatus.Active);

        contractRepository.save(contract);
        return ResponseEntity.ok("Contract created successfully");
    }

    private Boolean checkIfActiveContractExists(Hospital hospital, Company company) {
        return contractRepository.existsActiveContract(hospital, company, ContractStatus.Active);
    }


    public ResponseEntity<?> cancelContract(Integer id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));
        contract.setStatus(ContractStatus.Cancelled);
        contractRepository.save(contract);

        // Create a message for RabbitMQ
        String message = String.format("Contract with id %d between hospital %s and company %s has been cancelled.",
                contract.getId(), contract.getHospital().getName(), contract.getCompany().getName());

        // Send the cancellation message to the queue
//        rabbitTemplate.convertAndSend("contract-exchange", "contract-routing-key", message);
        rabbitTemplate.convertAndSend("contract-exchange", "notification-routing-key", message);
        return ResponseEntity.ok("Contract cancelled successfully");
    }
}
