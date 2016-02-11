#include <boost/asio.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/bind.hpp>
#include <iostream>


using boost::asio::ip::tcp;

class reac_communication
{
private:
	tcp::socket socket;
public:
	reac_communication(boost::asio::io_service& io_service) : socket(io_service)
	{

	}


	void start()
	{
		std::cout<<"New user Connected from "<<socket.remote_endpoint().address().to_string()<<std::endl;
		sendWelcomeMessage();

        boost::bind(&reac_communication::write_handler, this,
          boost::asio::placeholders::error,
          boost::asio::placeholders::bytes_transferred);
	}

	void write_handler(const boost::system::error_code& error, size_t bytes_transferred)
	{
		if(!error)
			std::cout<<"[Message Send!]"<<std::endl;
		else
			std::cerr<<"[Message Send Failed!]"<<std::endl;
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

	//Need to Connect
	void async_read(boost::system::error_code& error, size_t bytes_transferred)
	{
		if(error == boost::asio::error::eof)
		{
			//Connection Closed!
		}
		else
		{
			//Normal Data
		}
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



