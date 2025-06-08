#include "Graph.h"
#include <iostream>
#include <iomanip>

using namespace std;


void Graph::add_vertex(const string& vertex) {
    vertices.insert(vertex);
}

void Graph::add_edge(const string& v1, const string& v2, int cost) {
    adjacent_lists[v1][v2] = cost;
    adjacent_lists[v2][v1] = cost;
}

void Graph::remove_edge(const string& v1, const string& v2) {
    // erase node from adjacent list
    adjacent_lists[v1].erase(v2);
    adjacent_lists[v2].erase(v1);
}

void Graph::init() {
    // init distance vectors and tables
    for (auto& u: vertices) {
        // neighbors of u
        auto& neighbors = adjacent_lists[u];

        for (auto& v: vertices) {
            // self to self
            if (u == v) {
                distance_vectors[u][v] = make_pair(u, 0);
                continue;
            }

            auto it = neighbors.find(v);
            if (it != neighbors.end()) {
                // connected
                distance_vectors[u][v] = make_pair(it->first, it->second);
            } else {
                // not connected
                distance_vectors[u][v] = make_pair("", -1);
            }

            // generate distance table
            for (auto& w: vertices) {
                // skip self
                if (w == u) {
                    continue;
                }

                if (v == w && it != neighbors.end()) {
                    // connected
                    distance_tables[u][v][w] = it->second;
                } else {
                    // not connected
                    distance_tables[u][v][w] = -1;
                }
            }
        }
    }

    // show initial distance tables
    show_distance_tables();
}

void Graph::show_distance_tables() {
    // process router by router
     // Iterate over all vertices in the graph
    for (auto& u: vertices) {
        // Print the vertex and current time step
        cout << u << " Distance Table at t=" << time << "\n";

        cout << "     ";
        for (auto& v: vertices) {
            // Skip the distance from a vertex to itself
            if (u == v) {
                continue;
            }

            cout << left << setw(5) << v;
        }
        cout << "\n";

        // show table
        auto& table = distance_tables[u];
        for (auto& v: vertices) {
            if (u == v) {
                continue;
            }
            // Print the row header
            cout << v << "    ";

            for (auto& w: vertices) {
                if (u == w) {
                    continue;
                }
                // Reference the distance from vertex v to vertex w
                auto dist = table[v][w];
                cout << left << setw(5);
                // Print "INF" if distance is negative, else print the distance
                if (dist < 0) {
                    cout << "INF";
                } else {
                    cout << dist;
                }
            }
            cout << "\n";
        }
        cout << endl;
    }
}

// update the distance vector table
void Graph::update() {
    
    for (auto& u: vertices) {
        //get neighbours, distance table and distance vector from current vertex u
        auto& neighbors = adjacent_lists[u];
        auto& distance_table = distance_tables[u];
        auto& distance_vector = distance_vectors[u];

        // process all neighbors
        for (auto& p: neighbors) {
            // Get the neighbor vertex 'v' and its current distance from 'u'
            auto& v = p.first;            
            auto dist = p.second;
            // Get the old distance from 'u' to 'v'
            auto old_dist = distance_table[v][v];

            // Skip the update if the distance hasn't changed
            if (old_dist == dist) {
                continue;
            }

            // Compute the change in distance
            auto diff = dist - old_dist;

            // If the new distance is negative, it means 'v' is now disconnected
            if (dist < 0) {
                // disconnected, clear all distance table entry about v
                for (auto& dt: distance_table) {
                    dt.second[v] = -1;
                }
            } else {
                // If the new distance is positive, it means distance to 'v' has changed
                for (auto& dt: distance_table) {
                    dt.second[v] += diff;
                }
            }

            // update distance vector
            for (auto& dv: distance_vector) {
                auto& w = dv.first;
                auto& next = dv.second.first;

                // skip if 'w' is 'u' itself
                if (w == u) {
                    continue;
                }

                // next jump is not v, skip
                if (next != v) {
                    continue;
                }

                // next jump is v, dist changed
                if (dist < 0) {
                    // disconnected
                    dv.second.first = "";
                    dv.second.second = -1;
                } else {
                    // update distance
                    dv.second.second += diff;
                }

                // need to find new next jump from distance table
                for (auto& dt: distance_table[w]) {
                    // find new next jump
                    if (dv.second.second < 0 || dt.second < dv.second.second) {
                        dv.second.first = dt.first;
                        dv.second.second = dt.second;
                    }
                }
            }
        }
    }
}


void Graph::dv(bool split_horizon) {
    bool changed;
    // loop until no changes to distance vectors
    do {
        changed = false;
        ++time;

        // copy old distance vectors
        auto dup_vectors = distance_vectors;
        // process all nodes
        for (auto& u: vertices) {
            // get its adjacent list
            auto& neighbors = adjacent_lists[u];
            auto& distance_vector = distance_vectors[u];

            // check other nodes
            for (auto& v: vertices) {
                // skip if vertex is itself
                if (v == u) {
                    continue;
                }

                // check whether connected
                auto neighbor_it = neighbors.find(v);
                // skip not-connected
                if (neighbor_it == neighbors.end()) {
                    continue;
                }

                // process neighbor's distance vector
                for (auto& p: dup_vectors[v]) {
                    auto& w = p.first;
                    auto dist = p.second.second;

                    //  skip if vertex is itself
                    if (w == u) {
                        continue;
                    }

                    // skip not available
                    if (neighbor_it->second < 0) {
                        continue;
                    }

                    auto& dv_pair = distance_vector[w];
                    //spilt horizon part
                    if (split_horizon) {
                        if (dist < 0) {
                            if (dv_pair.first == v) {
                                changed = true;
                                dv_pair.first = "";
                                dv_pair.second = -1;
                            }
                        } else {
                            // if the route was learned from current vertex, skip
                            if (p.second.first == u) {
                                continue;
                            }

                            // calculate new distance
                            dist += neighbor_it->second;
                            // update distance vector if needed
                            if (dv_pair.second < 0 || dist < dv_pair.second) {
                                // distance vector changed
                                changed = true;
                                // update distance vector
                                dv_pair.first = v;
                                dv_pair.second = dist;
                            }
                        }
                    } else {
                        if (dist < 0) {
                            continue;
                        }

                        // calculate new distance
                        dist += neighbor_it->second;
                        // update distance vector if needed
                        if (dv_pair.second < 0 || dist < dv_pair.second) {
                            // distance vector changed
                            changed = true;
                            // update distance vector
                            dv_pair.first = v;
                            dv_pair.second = dist;
                        }
                    }

                    // update distance vector
                    distance_tables[u][w][v] = dist;
                }
            }
        }

        // show distance tables
        show_distance_tables();
    } while (changed);

    // show route tables
    show_route_tables();
}

void Graph::show_route_tables() {
    for (auto& u: vertices) {
        cout << u << " Routing Table:\n";

        auto& distance_vector = distance_vectors[u];
        // process its distance table
        for (auto& v: vertices) {
            if (u == v) {
                continue;
            }

            auto& p = distance_vector[v];
            // no route
            if (p.second < 0) {
                continue;
            }

            // show next jump and dist
            cout << v << "," << p.first << "," << p.second << "\n";
        }

        cout << endl;
    }
}



