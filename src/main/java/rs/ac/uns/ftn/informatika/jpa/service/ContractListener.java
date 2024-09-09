package rs.ac.uns.ftn.informatika.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.controller.ContractController;
import rs.ac.uns.ftn.informatika.jpa.dto.ContractDTO;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;

@Service
public class ContractListener {
    @Autowired
    ContractController contractController;
    @RabbitListener(queues = "contract-creation")
    public void receiveContract(String message) {
        // Parse message JSON PARSING
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Contract contract = objectMapper.readValue(message, Contract.class);
//            // Process contract logic: Store contract in DB, invalidate old contracts, etc.
//            System.out.println("Received Contract: " + contract);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        // Just print the message since it's plain text
//        System.out.println("Received Contract Notification: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the JSON message to a Contract object
            ContractDTO dto = objectMapper.readValue(message, ContractDTO.class);
            contractController.create(dto);

    } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
                    System.out.println("Error processing contract message: " + message);
        }
    }
}
