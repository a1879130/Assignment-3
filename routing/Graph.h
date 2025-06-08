#ifndef GRAPH_H
#define GRAPH_H

#include <map>
#include <set>
#include <string>


// Network Topology Graph
class Graph {
public:
    // add vertex (route) to graph
    void add_vertex(const std::string& vertex);

    // add edge (connection) to graph
    void add_edge(const std::string& v1, const std::string& v2, int cost);

    // remove edge from graph
    void remove_edge(const std::string& v1, const std::string& v2);

    // init topology graph
    void init();

    // update topology graph
    void update();

    // distance vector algorithm
    void dv(bool split_horizon = false);

private:
    // show distance tables
    void show_distance_tables();

    // show route tables
    void show_route_tables();

private:
    // time
    int time = 0;
    // vertices
    std::set<std::string> vertices;
    // adjacent lists
    std::map<std::string, std::map<std::string, int>> adjacent_lists;
    // distance vectors
    std::map<std::string, std::map<std::string, std::pair<std::string, int>>> distance_vectors;
    // distance tables
    std::map<std::string, std::map<std::string, std::map<std::string, int>>> distance_tables;
};

#endif