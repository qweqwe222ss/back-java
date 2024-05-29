package project.user;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class Agent extends EntityObject {
	private static final long serialVersionUID = 8554751796911238962L;

	private Serializable partyId;

	/**
	 * 父节点
	 */
	private Serializable parent_partyId;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public Serializable getParent_partyId() {
		return parent_partyId;
	}

	public void setParent_partyId(Serializable parent_partyId) {
		this.parent_partyId = parent_partyId;
	}



}
