#include <stdio.h>
class executor
{
private:
	void *_ref;
	FILE *pipe;
public:
	executor()
	{

	}
	executor(void *ref,FILE *pipe)
	{
		this->_ref = ref;
		this->pipe = pipe;
	}
	~executor()
	{

	}
	
	void execute(std::string str);
};

