package project.wallet;

import java.io.Serializable;

import kernel.bo.EntityObject;

/**
 * 钱包
 *
 */
public class Wallet extends EntityObject {

	private static final long serialVersionUID = 7522745589282180818L;

	private Serializable partyId;
	/**
	 * 现金
	 */
	private Double money = 0.0D;

	private Double rebate = 0.0D;

	/**
	 * 累计充值提成，注意：此为一个用于提示的字段，不可用于提现
	 */
	private Double rechargeCommission = 0.0;

	/** 2023-10-24 新增需求，冻结以后返佣等加钱操作不可用于采购，只有新充值金额可以用于采购
	 * 冻结后的充值金额
	 */
	private double moneyAfterFrozen = 0.0D ;

	/**
	 * 冻结状态 默认0-未冻结，1-已冻结
	 */
	private int frozenState = 0 ;

	public Serializable getPartyId() {
		return this.partyId;
	}

	public void setPartyId(Serializable partyId) {
		this.partyId = partyId;
	}

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getRebate() {
		return rebate;
	}

	public void setRebate(Double rebate) {
		this.rebate = rebate;
	}

	public Double getRechargeCommission() {
		return rechargeCommission;
	}

	public void setRechargeCommission(Double rechargeCommission) {
		this.rechargeCommission = rechargeCommission;
	}

	public double getMoneyAfterFrozen() {
		return moneyAfterFrozen;
	}

	public void setMoneyAfterFrozen(double moneyAfterFrozen) {
		this.moneyAfterFrozen = moneyAfterFrozen;
	}

	public int getFrozenState() {
		return frozenState;
	}

	public void setFrozenState(int frozenState) {
		this.frozenState = frozenState;
	}
}
