class Network:
    def __init__(self):
        self.graph = {}
        self.routing_tables = {}
        self.routers = []

    def add_router(self, name):
        if name not in self.graph:
            self.routers.append(name)
            self.graph[name] = {}
            self.routing_tables[name] = {}
            for router in self.routers:
                if router != name:
                    self.graph[name][router] = float('inf')
                    self.graph[router][name] = float('inf')
                    self.routing_tables[name][router] = [float('inf'), None]
                    self.routing_tables[router][name] = [float('inf'), None]

    def add_edge(self, start, end, weight):
        self.add_router(start)
        self.add_router(end)
        if weight == -1:
            weight = float('inf')
        self.graph[start][end] = weight
        self.graph[end][start] = weight
        self.routing_tables[start][end] = [weight, end]
        self.routing_tables[end][start] = [weight, start]

    def distance_vector(self):
        t = 0
        while True:
            update = False
            for router in sorted(self.routers):
                for destination in self.graph[router]:
                    for neighbour in self.graph[router]:
                        if destination != neighbour and destination != router:
                            if self.graph[router][neighbour] + self.routing_tables[neighbour][destination][0] < self.routing_tables[router][destination][0]:
                                self.routing_tables[router][destination] = [self.graph[router][neighbour] + self.routing_tables[neighbour][destination][0], neighbour]
                                update = True
            self.print_distance_table(t)
            self.print_routing_table()
            t += 1
            if not update:
                break

    def print_distance_table(self, t):
        for router in sorted(self.routers):
            print(f"{router} Distance Table at t={t}")
            for destination in sorted(self.graph[router]):
                if destination != router:
                    print("\t" + " ".join(sorted(self.graph[router].keys())))
                    break
            for next_hop in sorted(self.graph[router]):
                if next_hop != router:
                    print(f"{next_hop}\t", end="")
                    for destination in sorted(self.graph[router]):
                        if destination != router:
                            if self.routing_tables[router][destination][1] == next_hop:
                                print(f"{self.routing_tables[router][destination][0]}\t", end="")
                            else:
                                print("INF\t", end="")
                    print()
            print()

    def print_routing_table(self):
        for router in sorted(self.routers):
            print(f"{router} Routing Table:")
            for destination in sorted(self.routing_tables[router].keys()):
                next_hop, distance = self.routing_tables[router][destination]
                next_hop = next_hop if next_hop is not None else "INF"
                distance = distance if distance != float('inf') else "INF"
                print(f"{destination},{next_hop},{distance}")
            print()


if __name__ == '__main__':
    network = None
    while True:
        line = input().strip()
