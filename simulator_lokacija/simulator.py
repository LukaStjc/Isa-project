import asyncio
import nats
import json
from random import uniform
import time

async def run():
    # Connect to NATS server
    nc = await nats.connect("nats://localhost:4222")

    # Configurable frequency of updates
    update_frequency = 5  # seconds

    while True:
        # Generate random coordinates for demonstration (use actual logic for real coordinates)
        coordinates = {
            "latitude": uniform(-90, 90),
            "longitude": uniform(-180, 180)
        }

        # Publish coordinates to the NATS server
        await nc.publish("vehicle.location", json.dumps(coordinates).encode())

        # Wait for the next update
        await asyncio.sleep(update_frequency)

    # Gracefully close the connection
    await nc.close()

if __name__ == '__main__':
    asyncio.run(run())
