all: DistanceVector SplitHorizon

DistanceVector: DistanceVector.cpp Graph.h Graph.cpp
	g++ -std=c++11 -Wall DistanceVector.cpp Graph.cpp -o DistanceVector

SplitHorizon: SplitHorizon.cpp Graph.h Graph.cpp
	g++ -std=c++11 -Wall SplitHorizon.cpp Graph.cpp -o SplitHorizon

clean:
	rm -f DistanceVector SplitHorizon
