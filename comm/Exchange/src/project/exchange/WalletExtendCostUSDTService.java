package project.exchange;

import java.io.Serializable;

public interface WalletExtendCostUSDTService {
	public WalletExtendCostUSDT saveExtendByPara(Serializable partyId, String wallettype);
	
	public void save(WalletExtendCostUSDT entity);

	public void update(WalletExtendCostUSDT entity);
}
