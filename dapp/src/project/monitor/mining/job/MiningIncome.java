package project.monitor.mining.job;

import java.io.Serializable;

/**
 * 任务收益（暂存）
 *
 */
public class MiningIncome {
	
	// public static final int TYPE_RECOM = 1;
	
	/**
	 * 收益类型
	 * 0:自身收益
	 * 1:推荐奖励
	 */
	// private int type = 0;

	private Serializable partyId;

	private double value;

	public Serializable getPartyId() {
		return partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}


}
