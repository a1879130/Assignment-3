#include <iostream>
#include <sstream>
#include <vector>

using namespace std;
static string user_input;
static int routerCount;
static vector<string> user_inputs;

struct router
{
    /* data */
    string name;
    vector<vector<string>> distanceTable;
};


static vector<router> routers; // store all router information

void display_table(){
    for (int i =0; i < routerCount; i++)
    {
        for (int j = 0; j < routerCount; j++)
        {
            for(int k = 0; K < routerCount; k++)
            {
                cout << router.at(i).distanceTable[j][k] << " ";                
            }
            cout << endl;
        }
    }
}

vector<string> string_spilt(string input, char limiter)
{
    vector<string> result;
    int beg = 0;
    for(auto end = 0; (end = input.find(limiter,end)) != -1; ++end)
    {
        result.push_back(input.substr(beg,end -beg));
        beg = end +1;
    }
    result.push_back(input.substr(beg));

    return result;
}

/*initialize default destination tables based on routers name*/

void initialization(vector<string> routerNames)
{
      routerCount = routerNames.size();
      vector<string> temp = routerNames;

      for(int i = 0; i < routerCount; i++)
      {
        routers.push_back(router());
        routers.at(i).name = routerNames.at(i);
        routers.at(i).distanceTable.resize(routerCount, vector<string>(routerCount,"INF")); // prefill all table with value INF
        //routers.at(i).distanceTable.[0][0] = " ";
        temp.erase(remove(temp.begin(),temp.end(),routers.at(i).name),temp.end());
        int n = 1;
        for(int j = 0 ; j < temp.size() ; j++)
        {
            /*update the first column*/
            string routerName = temp.at(j);
            routers.at(i).distanceTable[n][0] = routerName;
            routers.at(i).distanceTable[0][n] = routerName;
            n++;
        }

      }
      display_table();

}
/*update direct destination*/
void update_t0(vector<string> routerInitial)
{
    vector<string> cost = routerInitial; //using a temp value to store the initial value
    vector<string> sepa_cost;
    for( int i = 0; i < routerCount; i++)
    {
        for (int j = 0; j < cost.size(); j++)
        {
            sepa_cost = string_spilt(cost.at(j),' ');
            if(sepa_cost.at(0) == routers.at(i).name)
            {

            }
        }
    }


}


int get_loc(vector<vector<string>> distanceTable, string destinationRouterName)
{
    int local = 0;
    vector<vector<string>>::iterator row;
    vector<vector<string>>::iterator col;

    for(row = distanceTable.begin(); row != distanceTable.end(); row++){
        
    }
}
/*let destination tables to self update itself untill reach convergence*/
void convergence(){

}

/*update local link cost based lateste input*/
void update_distance_table(vector<string> routerNames){

}
/*print final result*/
void print_output(){

}

void take_input(){
    while(getline(cin, user_input)){
        if(user_input == "DISTANCEVECTOR"){
            if(user_inputs.empty()){
                return;
            }
            initialization(user_inputs);
            user_inputs.clear();
            
        }
        if(user_input!= "DISTANCEVECTOR" && user_input != "UPDATE"){
            user_inputs.push_back(user_input);
        }

        if(user_input == "UPDATE")
        {
            if(user_inputs.empty()){
                return;
            }

            update_t0(user_inputs);
            convergence();
            user_inputs.clear();

        }

        if (user_inputs == "END"){
            update_distance_table(user_inputs);
            print_output();
            return;
        }
        
    }
}