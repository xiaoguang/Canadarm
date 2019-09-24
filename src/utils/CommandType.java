package utils;

public enum CommandType {

	LOADPROBLEM("Load Problem"), LOADSOLUTION("Load Solution"), EXIT("Exit");

	@SuppressWarnings("unused")
	private String representation;

	CommandType(String type) {
		this.representation = type;
	}

}
