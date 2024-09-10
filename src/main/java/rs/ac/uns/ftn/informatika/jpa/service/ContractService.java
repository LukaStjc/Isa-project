package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.ContractDTO;
import rs.ac.uns.ftn.informatika.jpa.dto.EquipmentBasicDTO;
import rs.ac.uns.ftn.informatika.jpa.enumeration.ContractStatus;
import rs.ac.uns.ftn.informatika.jpa.enumeration.DeliveryStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.model.Hospital;
import rs.ac.uns.ftn.informatika.jpa.repository.ContractRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;

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

//        Boolean hasActiveContract = checkIfActiveContractExists(hospital, company);
        Contract contract = getActiveContract(hospital, company);
        if (contract != null){
            contract.setStatus(ContractStatus.Cancelled);
            contractRepository.save(contract);
        }
        Contract newContract = new Contract();
        newContract.setCompany(company);
        newContract.setHospital(hospital);
        newContract.setEquipment(equipment);
        newContract.setDate(dto.getTime());
        newContract.setQuantity(dto.getEquipment().getQuantity());
        newContract.setStatus(ContractStatus.Active);

        contractRepository.save(newContract);
        return ResponseEntity.ok("Contract created successfully");
    }

    private Contract getActiveContract(Hospital hospital, Company company) {
        return contractRepository.getActiveContract(hospital, company, ContractStatus.Active);
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

    public List<Contract> findByDayOfMonthAndTime(int day, int hours, int minutes) {
        Date date = new Date();
        return contractRepository.findByDayOfMonthAndTime(day, hours, minutes);
    }

    public ResponseEntity<?> cancelThisMonthsDelivery(Integer id) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id =  " + id));
        contract.setThisMonthsDeliveryStatus(DeliveryStatus.CANCELLED);
        contractRepository.save(contract);

        String message = String.format("This months delivery with company %s, defined with contract with id %d, has been cancelled by the company.",
                contract.getCompany().getName(), contract.getId() );
        rabbitTemplate.convertAndSend("contract-exchange", "notification-routing-key", message);

        return ResponseEntity.ok("This months delivery successfully cancelled");
    }

    public List<Contract> findActiveByDayAndTime(Date date) {
//        return contractRepository.findByDayAndTime(date);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Define the start and end of the time range
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.set(Calendar.DAY_OF_MONTH, day);
        startCal.set(Calendar.HOUR_OF_DAY, hour);
        startCal.set(Calendar.MINUTE, minute);
        endCal.set(Calendar.DAY_OF_MONTH, day);
        endCal.set(Calendar.HOUR_OF_DAY, hour);
        endCal.set(Calendar.MINUTE, minute + 1); // End 1 minute later

        // Print the time range being checked
        System.out.println("Checking contracts between " + startCal.getTime() + " and " + endCal.getTime());

        // Retrieve contracts within the time range
        List<Contract> contracts = contractRepository.findByTimeRange(startCal.getTime(), endCal.getTime(), ContractStatus.Active);

        // Print the number of contracts found
        System.out.println("Number of contracts found: " + contracts.size());

        // Print details of each contract
        for (Contract contract : contracts) {
            System.out.println("Contract ID: " + contract.getId() + ", Date: " + contract.getDate());
        }


        return contracts;
    }

    public void markDeliveryAsSent(Contract contract) {
        contract.setThisMonthsDeliveryStatus(DeliveryStatus.SENT);
        contractRepository.save(contract);
    }

    public void save(Contract contract) {
        contractRepository.save(contract);
    }


    public List<ContractDTO> findAllActiveContractsByCompany(Integer id) {
        List<Contract> contracts= contractRepository.findAllActiveContractsByCompany(id, ContractStatus.Active);
        List<ContractDTO> dtos = new ArrayList<>();
        for (Contract contract: contracts) {
            ContractDTO dto = new ContractDTO();
            dto.setId(contract.getId());
            EquipmentBasicDTO equipmentDTO = new EquipmentBasicDTO(contract.getEquipment());
            dto.setEquipment(equipmentDTO);
            dto.setQuantity(contract.getQuantity());
            dto.setHospitalName(contract.getHospital().getName());
            dto.setStatus(contract.getThisMonthsDeliveryStatus());
            dtos.add(dto);
        }
        return dtos;
    }
}
