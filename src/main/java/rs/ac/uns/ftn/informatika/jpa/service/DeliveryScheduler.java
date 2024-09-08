package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.repository.ContractRepository;

import java.util.Date;
import java.util.List;

@Service
public class DeliveryScheduler {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;  // Inject RabbitTemplate

    @Scheduled(cron = "0 0 0 * * *")  // Runs every day at midnight
    public void checkDeliveries() {
        // Fetch contracts from the database
        List<Contract> contracts = contractRepository.findAll();

        Date today = new Date();
        for (Contract contract : contracts) {

            if (contract.getDeliveryDay() == today.getDay()) {
                boolean deliverySuccess = attemptDelivery(contract);

                if (!deliverySuccess) {
                    sendDeliveryNotification(contract);
                }
            }
        }
    }

    private boolean attemptDelivery(Contract contract) {
        // Logic to check if the required quantity is available
        // Return true if successful, false otherwise
        return false; // Simulating failure
    }

    private void sendDeliveryNotification(Contract contract) {
        // Send a notification that the delivery can't be fulfilled
        String notificationMessage = "Cannot deliver " + contract.getQuantity() + " " +
                contract.getEquipment().getName() + " to " + contract.getHospital().getName();

        rabbitTemplate.convertAndSend("delivery-notifications", notificationMessage);
    }
}
