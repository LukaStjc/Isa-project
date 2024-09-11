import pika
import json
from datetime import datetime, timedelta

RABBITMQ_HOST = 'localhost'

def create_contract(company_id, hospital_name, equipment_data, contract_date):
    """Simulate the hospital creating a new contract by sending a message to RabbitMQ."""
    contract_data = {
        "companyName": company_name,
        "hospitalName": hospital_name,
        "equipment": equipment_data,
        "date": contract_date
    }

    # Convert contract data to JSON
    message = json.dumps(contract_data)

    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
    channel = connection.channel()

    # Declare the exchange as durable (matching the existing configuration)
    channel.exchange_declare(exchange='contract-exchange', exchange_type='direct', durable=True)

    # Publish the message to the exchange with the routing key
    channel.basic_publish(
        exchange='contract-exchange',
        routing_key='contract-routing-key',
        body=message,
        properties=pika.BasicProperties(
            delivery_mode=2  # Make message persistent
        )
    )

    print(f"Contract creation message sent for {hospital_name}.")
    connection.close()

def listen_for_notifications():
    """Listen to RabbitMQ for contract cancellation notifications."""
    def contract_callback(ch, method, properties, body):
        message = body.decode('utf-8')
        print(f"Contract Notification received: {message}")
        ch.basic_ack(delivery_tag=method.delivery_tag)  # Acknowledge the message

    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
    channel = connection.channel()

    # Declare the exchange
    channel.exchange_declare(exchange='contract-exchange', exchange_type='direct', durable=True)

    # Bind the queue to the exchange
    channel.queue_bind(exchange='contract-exchange', queue='contract-notifications', routing_key='notification-routing-key')

    # Consume messages from the queue
    try:
        channel.basic_consume(queue='contract-notifications', on_message_callback=contract_callback, auto_ack=False)
        print('Listening for contract cancellation notifications from RabbitMQ...')
        channel.start_consuming()
    except KeyboardInterrupt:
        print('Interrupted, stopping...')
    finally:
        connection.close()

if __name__ == "__main__":
    # Simulate hospital contract creation
    company_name = "beta"
    hospital_name = "Bolnica Novi Sad"
    equipment_data = {
        "name": "Stetoskop",
        "quantity": 1200
    }
    # contract_date = str(datetime.now().isoformat())  # Format the date to match your backend requirements
    contract_date = str((datetime.now() - timedelta(hours=2)).isoformat())
    # Create a contract by sending a message to RabbitMQ
    create_contract(company_name, hospital_name, equipment_data, contract_date)

    # Listen to RabbitMQ for notifications
    listen_for_notifications()
