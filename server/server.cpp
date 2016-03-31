#include <boost/asio.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/bind.hpp>

#include <iostream>
#include <fstream>
#include <stdio.h>
#include "executor.h"
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

using boost::asio::ip::tcp;
#define REAC_PROMPT "manager@REAC> "

int pno;

class reac_communication
{
private:
	executor *_executor;
	tcp::socket socket;
	boost::asio::streambuf response_;
	bool isCommunicating;
	std::string IP;
	FILE* pipe;

public:
	std::string getIP() {
		return IP;
	}
	reac_communication(boost::asio::io_service& io_service) : socket(io_service)
	{
		
		_executor = new executor(this,pipe);
	}

	~reac_communication()
	{
		//pclose(pipe);
		delete _executor;
	}

	
	void start()
	{
		std::cout<<"New user Connected from "<<(IP=socket.remote_endpoint().address().to_string())<<std::endl;
		ServerPassword();
		
		
	}
	
	void write_to_socket(std::string message)
	{
		char line[1024];
		strcpy(line,message.c_str());
		
		//while (std::cin.getline(line, 256 + 1))
		{
 	  	//using namespace std; // For strlen and memcpy.
		//int len=strlen(line);
		//line[len]='\n';
		//line[len+1]='\0';
		
		//Socket Closed!
		if (!isCommunicating)
			return;

 	  	boost::asio::async_write(socket, boost::asio::buffer(line, message.length()),
			boost::bind(&reac_communication::write_handler, this,
			    	boost::asio::placeholders::error,
			   	boost::asio::placeholders::bytes_transferred));
			
		}
	}

	void write_handler(const boost::system::error_code& error, size_t bytes_transferred)
	{
		if(!error)
			std::cout<<"[Message Send!]"<<std::endl;
		else
			std::cerr<<"[Message Send Failed!]"<<std::endl;
	}
	void close() {
			socket.close();
			isCommunicating = false;
	}
	void read_handler(const boost::system::error_code& error)
	{
		 
		if(error == boost::asio::error::eof)
		{
			socket.close();
			isCommunicating = false;
			return;
  		}
  		else if(!error)
			{
				//std::string s( (std::istreambuf_iterator<char>(&stream_buf)), std::istreambuf_iterator<char>() );
				//_executor->execute(s);
				std::string myString;  

			// Convert streambuf to std::string  
				std::istream(&response_) >> myString;

				_executor->execute(myString);
				std::cout<<"[Message Received!] "<<&response_<<std::endl;
			}
		else
			{
				std::cerr<<"Message Received Failed!"<<std::endl;
			}
		async_read();//Again Read

		
		
	}

	tcp::socket& get_socket()
	{
		return socket;
	}
  
	void sendWelcomeMessage()
	{
		boost::asio::async_write(socket, boost::asio::buffer("You are connected to REAC-Server!\n"),
		
			boost::bind(&reac_communication::write_handler, this,
          boost::asio::placeholders::error,
          boost::asio::placeholders::bytes_transferred));
		
	}
	
	void serverPassword()
	{
		boost::asio::async_write(socket, boost::asio::buffer("Please enter the server Password:\n"),		
			boost::bind(&reac_communication::write_handler, this,
          boost::asio::placeholders::error,
          boost::asio::placeholders::bytes_transferred));

		boost::asio::async_read( socket,
          response_,
          boost::asio::transfer_at_least(1),
          boost::bind(&reac_communication::read_handler1, this,
            boost::asio::placeholders::error) );	

	}

	void read_handler1(const boost::system::error_code& error)
	{
		 
		if(error == boost::asio::error::eof)
		{
			socket.close();
			isCommunicating = false;
			return;
  		}
  		else if(!error)
			{
				//std::string s( (std::istreambuf_iterator<char>(&stream_buf)), std::istreambuf_iterator<char>() );
				//_executor->execute(s);
				std::string myString;  

			// Convert streambuf to std::string  
				std::istream(&response_) >> myString;
				ifstream infile;
			infile.open ("passwd.txt");
			std::string pass;
        	while(!infile.eof) // To get you all the lines.
        	{
	        	getline(infile,pass); // Saves the line in STRING.
	        	 // Prints our STRING.
        	}
			infile.close();
			if(strcmp(pass,myString)==0)
			{
				sendWelcomeMessage();
				write_to_socket(REAC_PROMPT);

			//Multit	hreading for Writing to Socket
				isCommunicating = true;
				char buff[512];
		

			async_read(); 
			//std::string cmd = "nc -l "+std::to_string(pno+1)+" | ./lshell  | tee -a log.txt | nc localhost "+std::to_string(pno+2);
			//system(cmd.c_str());
			//socket.close();
			//exit(0);
			//listening = NULL;//std::thread([=]{write_to_socket();});
			}
			else
			{
				socket.close();
			}

				std::cout<<"[Message Received!] "<<&response_<<std::endl;
			}
		else
			{
				std::cerr<<"Message Received Failed!"<<std::endl;
			}
		async_read();//Again Read

		
		
	}

 
	void async_read()
	{
		if (!isCommunicating)
			return;

		boost::asio::async_read( socket,
          response_,
          boost::asio::transfer_at_least(1),
          boost::bind(&reac_communication::read_handler, this,
            boost::asio::placeholders::error) );
		
	}
};

class reac_server
{
private:
	tcp::acceptor _acceptor;
	boost::asio::io_service *io_service;

public:
	//Constructor
	reac_server(boost::asio::io_service &io_service, int port_number) : _acceptor(io_service, tcp::endpoint(tcp::v4(), port_number))
	{
		this->io_service = &io_service;
		pno = port_number;
		std::cout<<"Server Started on Port "<<port_number<<std::endl;
		
		accept_clients();
	}


private:
	void accept_clients()
	{
		reac_communication *new_one = new reac_communication(*io_service);
		_acceptor.async_accept(new_one->get_socket(),
			boost::bind(&reac_server::accept_handler, this, new_one, _1)); //boost::asio::placeholder::error
	}

	void accept_handler(reac_communication *new_one, const boost::system::error_code& error)
	{
		if(!error)
		{
			new_one->start();
		}
		//Again Accept Another Client
		accept_clients();
	}
};

void killProgram(reac_communication *reac, std::string process) {
	char cmd[500];
	strcpy(cmd,("kill `pgrep "+process+"`").c_str());
	int sys = system(cmd);
	reac->write_to_socket("Status : "+std::to_string(sys));
		
}
void killProgramPID(reac_communication *reac, std::string pid) {
	char cmd[500];
	strcpy(cmd,("kill "+pid).c_str());
	int sys = system(cmd);
	reac->write_to_socket("Status : "+std::to_string(sys));
		
}	



void executeShell(reac_communication *reac,std::string relPath, std::string argv1) {
	int pid = fork();

	if (pid==0) {
		char cwd[150];
		getcwd(cwd,150);
		char exePath[200];




		strcpy(exePath,(std::string(cwd)+"/"+relPath).c_str());

				//Child
		char *argv[4]={"/bin/sh", exePath, NULL, NULL};
		char argv1_[100];
		if (!argv1.empty())
		{
			strcpy(argv1_, argv1.c_str());
			argv[2] = argv1_;
		}
		char *newenv[]={NULL};
		execve("/bin/sh",argv,newenv);
		perror("Error in Execution!");
		reac->write_to_socket("Error in Execution!\n");
		exit(0);

	} else {
		reac->write_to_socket("PID : "+std::to_string(pid)+"\n");
				
	}
}


void executor::execute(std::string str)
{
	reac_communication *reac = (reac_communication *)_ref;
	std::cout<<str<<std::endl;

	if (str == "START_SHELL") {
		reac->write_to_socket("Shell : Starting\n");
		system(("/bin/sh ./executor/server_script "+reac->getIP()).c_str());
	} else if (str == "EXIT") {
		reac->close();
	} else if (str.substr(0,5) == "KILL_") {
		killProgram(reac, str.substr(5));
	} else if (str.substr(0,8) == "PIDKILL_") {
		killProgramPID(reac, str.substr(8));
	} else if (str.substr(0,6) == "BLOCK_") {
		executeShell(reac,"Log/killer",str.substr(6));
	} else
		reac->write_to_socket("Invalid REAC Command!\n");
	

}

