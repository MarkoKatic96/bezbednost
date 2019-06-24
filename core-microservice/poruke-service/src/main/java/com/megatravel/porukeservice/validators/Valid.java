package com.megatravel.porukeservice.validators;

public class Valid {
	private boolean isValid;
	private String errCode;
	
	public Valid() {}

	public Valid(boolean isValid, String errCode) {
		this.isValid = isValid;
		this.errCode = errCode;
	}

	public boolean isValid() {
		return isValid;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
}
