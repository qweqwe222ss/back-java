package project.party.model;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class UserRecom extends EntityObject {
	private static final long serialVersionUID = 4306215956505507789L;
	private Serializable partyId;
	/**
	 * 推荐人
	 */
	private Serializable reco_id;

	public Serializable getPartyId() {
		return this.partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public Serializable getReco_id() {
		return this.reco_id;
	}

	public void setReco_id(Serializable reco_id) {
		this.reco_id = reco_id;
	}
}
