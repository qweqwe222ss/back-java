package project.blockchain.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.blockchain.AdminChannelBlockchainService;

public class AdminChannelBlockchainServiceImpl extends HibernateDaoSupport implements AdminChannelBlockchainService {
	private PagedQueryDao pagedQueryDao;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String coin_para) {
		StringBuffer queryString = new StringBuffer(
				" SELECT channelblockchain.UUID id,channelblockchain.BLOCKCHAIN_NAME blockchain_name,"
						+ "channelblockchain.IMG img ,channelblockchain.COIN coin,  "
						+ " channelblockchain.ADDRESS address ");

		queryString.append(" FROM T_CHANNEL_BLOCKCHAIN channelblockchain WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and  channelblockchain.BLOCKCHAIN_NAME like :name ");
			parameters.put("name", "%" + name_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(coin_para)) {
			queryString.append(" and  channelblockchain.COIN like :coin ");
			parameters.put("coin", "%" + coin_para + "%");
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	@Override
	public Page pagedPersonQuery(int pageNo, int pageSize, String userName, String roleName, String chainName, String coinSymbol, String address) {
		StringBuffer queryString = new StringBuffer(
				" SELECT USER_NAME,party.ROLENAME,party.USERCODE,CHAIN_NAME,COIN_SYMBOL,ADDRESS,AUTO,chain.CREATE_TIME FROM T_PARTY_BLOCKCHAIN chain " +
						"LEFT JOIN PAT_PARTY party ON party.USERNAME = chain.USER_NAME WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(address)) {
			queryString.append(" AND chain.ADDRESS =:address ");
			parameters.put("address", address);
		}
		if (!StringUtils.isNullOrEmpty(userName)) {
			queryString.append(" AND (chain.USER_NAME LIKE :userName OR party.USERCODE LIKE :userName)  ");
			parameters.put("userName", "%" + userName + "%");
		}
		if (!StringUtils.isNullOrEmpty(roleName)) {
			queryString.append(" AND party.ROLENAME = :roleName ");
			parameters.put("roleName", roleName);
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
}
