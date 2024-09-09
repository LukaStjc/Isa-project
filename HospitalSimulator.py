import requests
import pika
import json
from datetime import datetime

# Configuration for the Spring backend and RabbitMQ
SPRING_BACKEND_URL = "http://localhost:8082/api/contracts"  # Adjust the URL to your Spring backend endpoint
RABBITMQ_HOST = 'localhost'

def create_contract(company_id, hospital_name, equipment_data, contract_date):
    """Simulate the hospital creating a new contract."""
    contract_data = {
        "companyId": company_id,
        "hospitalName": hospital_name,
        "equipment": equipment_data,
        "date": contract_date
    }

    try:
        response = requests.post(SPRING_BACKEND_URL, json=contract_data)
        if response.status_code == 201:
            print(f"Contract created successfully for {hospital_name}.")
        else:
            print(f"Failed to create contract: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"Error communicating with the backend: {e}")

def listen_for_notifications():
    """Listen to RabbitMQ for delivery and contract cancellation notifications."""
    def delivery_callback(ch, method, properties, body):
        message = body.decode('utf-8')
        print(f"Delivery Notification received: {message}")

    def contract_callback(ch, method, properties, body):
        # message = body.decode('utf-8')
        # print(f"Contract Notification received: {message}")
        message = body.decode('utf-8')
        print(f"Contract Notification received: {message}")
        ch.basic_ack(delivery_tag=method.delivery_tag)  # Acknowledge the message

    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
    channel = connection.channel()

    # Declare the exchange as durable (matching the existing configuration)
    channel.exchange_declare(exchange='contract-exchange', exchange_type='direct', durable=True)

    # Declare the queue and bind it to the exchange with the correct routing key
    channel.queue_declare(queue='contract-creation')
    channel.queue_bind(exchange='contract-exchange', queue='contract-creation', routing_key='contract-routing-key')

    # Consume messages from the queue
    # channel.basic_consume(queue='contract-creation', on_message_callback=contract_callback, auto_ack=True)
    channel.basic_consume(queue='contract-creation', on_message_callback=contract_callback, auto_ack=False)

    print('Listening for delivery and contract messages from RabbitMQ...')
    channel.start_consuming()

if __name__ == "__main__":
    # Simulate hospital contract creation
    company_id = 1
    hospital_name = "Bolnica Novi Sad"
    equipment_data = {
        "name": "Stetoskop",
        "companyId": 1,
        "equipmentType": 1,  # Assuming this is an enum value, make sure it matches your backend
        "price": 299.99,
        "quantity": 5
    }
    contract_date = str(datetime.now().isoformat())  # Format the date to match your backend requirements

    # Create a contract by sending a POST request to the Spring backend
    create_contract(company_id, hospital_name, equipment_data, contract_date)

    # Listen to RabbitMQ for notifications
    listen_for_notifications()
