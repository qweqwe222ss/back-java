package project.wallet.rate;

import java.io.Serializable;

import kernel.bo.EntityObject;

/**
 * 用户汇率配置
 * 
 * @author User
 *
 */
public class UserRateConfig extends EntityObject {
	private static final long serialVersionUID = 1L;
	
	private Serializable partyId;

	/**
	 * 货币，见Constants定义
	 */
	private String currency;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
