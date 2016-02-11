#include <iostream>
#include <cstdlib>
#include <boost/asio.hpp>
#include "server.cpp"

const int DEFAULT_PORT  = 8080;

void checkParameters(int argc,const char *argv[],int& port_no);

int main(int argc, char const *argv[])
{
  std::cout<<"REAC - Server Side"<<std::endl;
  std::cout<<"------------------"<<std::endl;

  int port_no;
  checkParameters(argc,argv,port_no);

  try
  {
    
    boost::asio::io_service io_service;
    reac_server server(io_service, port_no);
    io_service.run();

  }catch(std::exception& e)
  {
    std::cerr<<e.what()<<std::endl;
  }
  return 0;
}

void checkParameters(int argc,const char *argv[] ,int &portNumber)
{
  if(argc<2)
  {
    portNumber = DEFAULT_PORT;
  }
  else
  {
    try
    {
    portNumber = atoi(argv[1]);
    }catch(std::exception &e)
    {
      std::cerr<<"Invalid Port Format!!"<<std::endl;
      exit(-1);
    }
  }
}