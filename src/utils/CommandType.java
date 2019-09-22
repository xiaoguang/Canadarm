package utils;

public enum CommandType {

	PROBLEM("Problem"), SOLUTION("Solution"), LOADPROBLEM("Load Problem"),
	LOADSOLUTION("Load Solution"), EXIT("Exit"), INITIALIZE("Initialize"),
	PLAY("Play"), STOP("Stop"), PAUSE("Pause");

	@SuppressWarnings("unused")
	private String representation;

	CommandType(String type) {
		this.representation = type;
	}

}
