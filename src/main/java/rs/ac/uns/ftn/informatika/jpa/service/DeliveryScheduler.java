package rs.ac.uns.ftn.informatika.jpa.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.controller.ContractController;
import rs.ac.uns.ftn.informatika.jpa.enumeration.DeliveryStatus;
import rs.ac.uns.ftn.informatika.jpa.model.Company;
import rs.ac.uns.ftn.informatika.jpa.model.Contract;
import rs.ac.uns.ftn.informatika.jpa.model.Equipment;
import rs.ac.uns.ftn.informatika.jpa.repository.ContractRepository;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DeliveryScheduler {
    @Autowired
    private ContractController contractController;

    @Autowired
    private RabbitTemplate rabbitTemplate;  // Inject RabbitTemplate

    @Scheduled(cron = "0 * * * * *")  // Runs every minute
    public void sendNotificationsDuringBusinessHours() {

        LocalTime now = LocalTime.now();
        List<Contract> contracts = contractController.findActiveByDayAndTime(new Date());
        // Assuming you have the company object or its opening and closing times.

        // Check if current time is within business hours

        for (Contract contract : contracts) {
            Company company = contract.getCompany();
            // Convert Date to LocalTime
            LocalTime openingTime = convertDateToLocalTime(company.getOpeningTime());
            LocalTime closingTime = convertDateToLocalTime(company.getClosingTime());



            // Check if the current time is within the company's business hours
            if (now.isAfter(openingTime) && now.isBefore(closingTime)) {

                // Calculate 3 days before delivery date
                Integer deliveryDay = contract.getDeliveryDay();
                Calendar deliveryCal = Calendar.getInstance();
                deliveryCal.setTime(new Date());
                deliveryCal.set(Calendar.DAY_OF_MONTH, deliveryDay);  // Set the calendar to the delivery day of the current month

                // Subtract 3 days from the delivery date
                deliveryCal.add(Calendar.DAY_OF_MONTH, -3);
                Date threeDaysBeforeDelivery = deliveryCal.getTime();

                // Check if today's date is after or equal to 3 days before the delivery
                Calendar todayCal = Calendar.getInstance();
                todayCal.setTime(new Date());

            if (todayCal.get(Calendar.DAY_OF_MONTH) > deliveryDay && !contract.getThisMonthsDeliveryStatus().equals(DeliveryStatus.PENDING) ) {
                    // Reset the delivery status to PENDING for next month
                    contract.setThisMonthsDeliveryStatus(DeliveryStatus.PENDING);
                    contractController.save(contract);
                    System.out.println("Delivery status reset to PENDING for contract ID: " + contract.getId());
                    continue;
            }


                // If it's 3 days before the delivery, check the equipment quantity
                if (new Date().after(threeDaysBeforeDelivery) && new Date().getDay() < (deliveryDay)) {
                boolean sufficientEquipment = checkCompanyEquipment(company, contract);

                if (!sufficientEquipment) {
                    // Mark delivery as cancelled
                    contract.setThisMonthsDeliveryStatus(DeliveryStatus.CANCELLED);
                    contractController.save(contract);
                    System.out.println("Insufficient equipment. Delivery cancelled for contract ID: " + contract.getId());

                    // Create the notification message
                    String message = String.format("Delivery for contract ID %d to hospital %s by company %s has been cancelled due to shortage of %s. Requested: %d, In stock: %d",
                            contract.getId(), contract.getHospital().getName(), company.getName(), contract.getEquipment().getName(), contract.getQuantity(), contract.getEquipment().getAvailableQuantity());

                    // Send the notification via RabbitMQ
                    rabbitTemplate.convertAndSend("contract-exchange", "notification-routing-key", message);


                    continue;  // Skip the rest of the loop for this contract
                }
                }
                if (contract.getThisMonthsDeliveryStatus() == DeliveryStatus.CANCELLED) {
                    continue;  // Skip if the contract has been cancelled
                }
                if(todayCal.get(Calendar.DAY_OF_MONTH) > deliveryDay){
//                    System.out.println("Today's day: " + todayCal.get(Calendar.DAY_OF_MONTH) + ", Delivery day: " + deliveryDay);
//                    System.out.println("wtf");
                    continue;
                }
                // Create the notification message
                String message = String.format("Delivery sent for contract ID %d to hospital %s by company %s.",
                        contract.getId(), contract.getHospital().getName(), company.getName());

                // Send the notification via RabbitMQ
                rabbitTemplate.convertAndSend("contract-exchange", "notification-routing-key", message);

                // Mark the delivery as sent
                contractController.markDeliveryAsSent(contract);
            } else {
                System.out.println("Company is closed. Skipping notifications.");
            }
        }
    }
    private LocalTime convertDateToLocalTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }
    private boolean checkCompanyEquipment(Company company, Contract contract) {
        // Get the required equipment quantity from the contract
        int requiredQuantity = contract.getQuantity();

        // Query the company's available equipment (this depends on your existing service/repository)
        int availableQuantity = contract.getEquipment().getAvailableQuantity();  // Assume this method exists

        // Return whether the available quantity is enough
        return availableQuantity >= requiredQuantity;
    }
}
