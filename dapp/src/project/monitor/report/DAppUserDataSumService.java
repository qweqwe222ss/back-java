package project.monitor.report;

import java.io.Serializable;
import java.util.Date;

public interface DAppUserDataSumService {

	public DAppData cacheGetData(Date date);

	public void save(DAppUserDataSum entity);

	public void saveRegister(Serializable partyId);

	/**
	 * 授权通过
	 * 
	 * @param partyId
	 */
	public void saveApprove(Serializable partyId);

	/**
	 * 授权通过变成不通过
	 * 
	 * @param partyId
	 */
	public void saveApproveSuccessToFail(Serializable partyId);

	/**
	 * 授权金额(用户余额变更)
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveUsdtUser(Serializable partyId, double amount);

	/**
	 * 授权转账金额
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveTransferfrom(Serializable partyId, double amount);
	/**
	 * 清算金额
	 * 
	 * @param amount
	 */
	public void saveSettle(double amount);
}
