#include "Graph.h"
#include <iostream>
#include <sstream>

using namespace std;

// read vertices from stdin
void read_vertices(Graph& graph);

// read edges from stdin
bool read_lines(Graph& graph, const string& sep, bool update = false);

int main() {
    Graph graph;

    // read vertices
    read_vertices(graph);
    // read DISTANCEVECTOR information
    read_lines(graph, "UPDATE");
    // init
    graph.init();
    // run distance vector
    graph.dv();

    // read UPDATE info
    if (read_lines(graph, "END", true)) {
        // update
        graph.update();
        // run distance vector
        graph.dv();
    }

    return 0;
}

void read_vertices(Graph& graph) {
    string line;
    // continuously read lines from standard input until there are no more
    while (getline(cin, line)) {
        if (line.back() == '\r') {
            line.pop_back();
        }
        // if the line is empty, ignore it and continue with the next line
        if (line.empty()) {
            continue;
        }
        // if the line is "DISTANCEVECTOR", it signifies the end of vertex input, so break out of the loop
        // get DISTANCEVECTOR, done
        if (line == "DISTANCEVECTOR") {
            break;
        }
        // if none of the above conditions are met, treat the line as a vertex and add it to the graph
        graph.add_vertex(line);
    }
}

bool read_lines(Graph& graph, const string& sep, bool update) {
    string line;
    // Flag to indicate if any lines were read and processed
    bool ret = false;
    // Read lines from the standard input until there are no more lines
    while (getline(cin, line)) {
        // If the line ends with '\r' (carriage return), remove it
        // If the line is empty, skip it
        // If the line equals the 'sep' string (either "UPDATE" or "END"), stop reading lines
        if (line.back() == '\r') {
            line.pop_back();
        }

        if (line.empty()) {
            continue;
        }

        // get UPDATE or END, done
        if (line == sep) {
            break;
        }

        // Extract edge information from the line
        stringstream ss(line);
        string v1, v2;
        int cost;
        ss >> v1 >> v2 >> cost;
        // Set 'ret' to true because a line has been processed
        ret = true;
        // cost >= 0, normal edge
        if (cost >= 0) {
            graph.add_edge(v1, v2, cost);
        } else {
            // cost < 0, remove edge
            if (update) {
                // for update, just add new edge to overrap old
                graph.add_edge(v1, v2, -1);
            } else {
                // for init, remove edge
                graph.remove_edge(v1, v2);
            }
        }
    }
    // Return true if any lines were read and processed, false otherwise
    return ret;
}

