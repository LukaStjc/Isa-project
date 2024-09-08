package rs.ac.uns.ftn.informatika.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;

@Service
public class ContractListener {
    @RabbitListener(queues = "contract-creation")
    public void receiveContract(String message) {
        // Parse message
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Contract contract = objectMapper.readValue(message, Contract.class);
            // Process contract logic: Store contract in DB, invalidate old contracts, etc.
            System.out.println("Received Contract: " + contract);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
