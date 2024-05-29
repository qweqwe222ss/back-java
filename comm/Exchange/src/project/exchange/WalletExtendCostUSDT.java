package project.exchange;

import java.io.Serializable;

import kernel.bo.EntityObject;

public class WalletExtendCostUSDT  extends EntityObject{
	
	private static final long serialVersionUID = 3644783740625158698L;
	
	private Serializable partyId;
	/**
	 * 币种，见Constants定义
	 */
	private String wallettype;
	
	
	/**
	 * 数量
	 */
	private double volume;
	
	
	/**
	 * 价格
	 */
	private double price;


	public Serializable getPartyId() {
		return partyId;
	}


	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}


	public String getWallettype() {
		return wallettype;
	}


	public void setWallettype(String wallettype) {
		this.wallettype = wallettype;
	}


	public double getVolume() {
		return volume;
	}


	public void setVolume(double volume) {
		this.volume = volume;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}
	
	

}
