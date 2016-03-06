#include <boost/asio.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/bind.hpp>
#include <unistd.h>
#include <iostream>
#include <thread>
//#include "executor.cpp"

using boost::asio::ip::tcp;

class executor
{
private:
	void *_ref;
public:
	executor()
	{

	}
	executor(void *ref)
	{
		this->_ref = ref;
	}
	~executor()
	{

	}
	
	void execute(std::string str);
};

class reac_communication
{
private:
	executor *_executor;
	tcp::socket socket;
	boost::asio::streambuf response_;
	std::thread listening;
	bool isCommunicating;

public:
	reac_communication(boost::asio::io_service& io_service) : socket(io_service)
	{
		_executor = new executor(this);
	}

	~reac_communication()
	{
		delete _executor;
	}
	void start()
	{
		std::cout<<"New user Connected from "<<socket.remote_endpoint().address().to_string()<<std::endl;
		sendWelcomeMessage();

		//Multithreading for Writing to Socket
		isCommunicating = true;
		async_read(); 
		//listening = NULL;//std::thread([=]{write_to_socket();});
		
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
				std::cout<<"[Message Recived!] "<<&response_<<std::endl;
			}
		else
			{
				std::cerr<<"[Message Received Failed!]"<<std::endl;
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
	
	void async_read()
	{
		
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





void executor::execute(std::string str)
{
	std::string output;
	if(str=="ls")
		output = "/bin /boot /home /etc /proc /var /usr\n";	
	else if(str=="cd")
		output = "CHANGE DIRECTORY\n";
	else output = "Invalid Command : "+str+"\n";	
	((reac_communication*)_ref)->write_to_socket(output);
}
