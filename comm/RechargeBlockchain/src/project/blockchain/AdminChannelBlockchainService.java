package project.blockchain;

import kernel.web.Page;

/**
 * 后台区块链地址查询
 *
 */
public interface AdminChannelBlockchainService {

	/**
	 * 代理分页查询 name_para链名称，coin_para币种名称
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String coin_para);

	Page pagedPersonQuery(int pageNo, int pageSize, String userName, String roleName, String chainName, String coinSymbol, String address);
}
