package com.example.SocialNetwork.exception;

public class TenantHeaderMissingException extends RuntimeException {
    private String message;

	public TenantHeaderMissingException(String message) {
		this.message = message;
	}

	public TenantHeaderMissingException() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
