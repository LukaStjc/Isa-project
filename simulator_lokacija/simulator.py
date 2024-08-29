import asyncio
import nats
import json
from random import uniform
import time

async def run():
    # Connect to NATS server
    nc = await nats.connect("nats://localhost:4222")

    # Configurable frequency of updates
    # It will be received from the main application
    update_frequency = 0.1  # seconds

    routes = {
        # The first coordinante corresponds hospital, the second coordinante corresponds company!
        ((45.2503173, 19.8525275), (45.24096,19.82831)): [
            (45.2503173, 19.8525275),
            (45.25030, 19.85254),
            (45.2503,19.8517),
            (45.2494,19.8521),
            (45.2485,19.8517),
            (45.2468,19.8485),
            (45.2446,19.8444),
            (45.2431,19.8386),
            (45.2423,19.8352),
            (45.24140,19.83104),
            (45.24095, 19.82832),
            (45.24096,19.82831),
        ],
        ((45.25328,19.81433), (45.24096,19.82831)): [
            (45.25328,19.81433),
            (45.25302,19.81429),
            (45.25375,19.81317),
            (45.25571,19.81180),
            (45.25514,19.81194),
            (45.25328,19.81325),
            (45.25223,19.81398),
            (45.25051,19.81515),
            (45.24811,19.81681),
            (45.24629,19.81807),
            (45.24382,19.81975),
            (45.24227,19.82080),
            (45.24008,19.82230),
            (45.23950,19.82344),
            (45.23982,19.82542),
            (45.24119,19.82534),
            (45.24178,19.82686),
            (45.24219,19.82844),
            (45.24180,19.82865),
            (45.24075,19.82843),
            (45.24096,19.82831),
        ],
        ((45.24880,19.83055), (45.24096,19.82831)): [
            (45.24880,19.83055),
            (45.24879,19.83030),
            (45.24913,19.83028),
            (45.24921,19.83116),
            (45.24939,19.83282),
            (45.24886,19.83333),
            (45.24819,19.83272),
            (45.24717,19.83178),
            (45.24613,19.83079),
            (45.24576,19.83159),
            (45.24546,19.83222),
            (45.24472,19.83150),
            (45.24362,19.83042),
            (45.24262,19.82912),
            (45.24218,19.82845),
            (45.24149,19.82881),
            (45.24076,19.82845),
            (45.24096,19.82831),
        ]
    }

    # Message handler for incoming route requests
    async def message_handler(msg):
        data = json.loads(msg.data.decode())
        print('Data received:', data)
        
        # Extract coordinates from the data
        start = (data['startLatitude'], data['startLongitude'])
        end = (data['endLatitude'], data['endLongitude'])

        # Find route using tuple of start and end points
        route = routes.get((start, end))

        if route:
            for (latitude, longitude) in route:
                coordinates = {"latitude": latitude, "longitude": longitude}
                print(f"Publishing location: {coordinates}")
                await nc.publish("vehicle.location", json.dumps(coordinates).encode())
                await asyncio.sleep(update_frequency)
        else:
            print(f"No route found for: {start} to {end}")

    # Subscribe to the queue with the message handler
    await nc.subscribe("route.requests", cb=message_handler)

    # Keep the connection alive
    await asyncio.Future()  # This keeps the coroutine running indefinitely

if __name__ == '__main__':
    asyncio.run(run())
