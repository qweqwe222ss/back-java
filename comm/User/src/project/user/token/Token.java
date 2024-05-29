package project.user.token;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class Token  extends EntityObject{
	private static final long serialVersionUID = -5132505045848059321L;

	private Serializable partyId;
	
	private String token;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
