package project.wallet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import project.contract.ContractOrder;
import project.data.model.Realtime;
import project.futures.FuturesOrder;

/**
 * 钱包
 */
public interface AssetService {
	
	/**
	 * 获取其他拓展钱包币种的余额
	 */
	public Map<String, Object> getMoneyAll(Serializable partyId);

	/**
	 * 获取其他拓展钱包币种的余额
	 */
	public double getMoneyCoin(Serializable partyId, List<Realtime> realtimeall, List<String> list_symbol);

	/**
	 * 理财资产 = 用户托管订单金额+盈亏金额
	 */
	public double getMoneyFinance(Serializable partyId, List<Realtime> realtimeall);

	/**
	 * 矿机资产 = 用户托管订单金额+盈亏金额
	 */
	public double getMoneyMiner(Serializable partyId, List<Realtime> realtimeall);

	/**
	 * 所有限价 永续委托单 = 开仓数量 * 每手价格
	 */
	public double getMoneyContractApply(Serializable partyId);

	/*
	 * 获取 所有订单 永续合约总资产、总保证金、总未实现盈利
	 */
	public Map<String, Object> getMoneyContract(Serializable partyId);
	
	/*
	 * 获取 单个订单 永续合约总资产、总保证金、总未实现盈利
	 */
	public Map<String, Double> getMoneyContractByOrder(ContractOrder order);

	/*
	 * 获取 所有订单 交割合约总资产、总未实现盈利
	 */
	public Map<String, Object> getMoneyFutures(Serializable partyId);
	
	/*
	 * 获取 单个订单 交割合约总资产、总未实现盈利
	 */
	public Map<String, Double> getMoneyFuturesByOrder(FuturesOrder order);

	/**
	 * 币币交易限价单
	 */
	public double getMoneyexchangeApplyOrders(Serializable partyId, List<Realtime> realtimeall);

}
