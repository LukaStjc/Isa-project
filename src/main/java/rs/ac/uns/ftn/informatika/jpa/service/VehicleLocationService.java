package rs.ac.uns.ftn.informatika.jpa.service;

import com.google.gson.Gson;
import io.nats.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.informatika.jpa.dto.RouteRequestDTO;

import java.nio.charset.StandardCharsets;

@Service
public class VehicleLocationService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Connection natsConnection;

    public VehicleLocationService() {
        try {
            this.natsConnection = Nats.connect("nats://localhost:4222");
            subscribeToVehicleUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRouteRequest(RouteRequestDTO routeRequest) throws Exception {
        String json = new Gson().toJson(routeRequest);
        natsConnection.publish("route.requests", json.getBytes(StandardCharsets.UTF_8));
    }

    private void subscribeToVehicleUpdates() {
        try {
            Dispatcher dispatcher = natsConnection.createDispatcher((msg) -> {
                String message = new String(msg.getData(), StandardCharsets.UTF_8);
                System.out.println("Received coordinates: " + message);
                // Additional logic for processing the message can be implemented here

                simpMessagingTemplate.convertAndSend("/topic/vehicleLocation", message);
            });

            dispatcher.subscribe("vehicle.location");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
