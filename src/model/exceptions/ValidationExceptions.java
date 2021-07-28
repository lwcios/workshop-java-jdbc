package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationExceptions extends RuntimeException {

	private Map<String, String > errors = new HashMap<>(); 
	
	private static final long serialVersionUID = 1L;
	
	public ValidationExceptions(String msg) {
		super(msg);
		
	}
	
	public Map<String, String > getErros(){
		return errors;
	}
	
	public void addError(String fieldName ,String errorMessage) {
		
		errors.put(fieldName, errorMessage);
		
	}

}
