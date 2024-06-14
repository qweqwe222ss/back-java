package systemuser.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kernel.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.party.model.Party;
import project.party.recom.UserRecomService;
import systemuser.CustomerService;
import systemuser.model.Customer;

public class CustomerServiceImpl extends HibernateDaoSupport implements CustomerService {

	private UserRecomService userRecomService;

	private Map<String, Customer> cache = new ConcurrentHashMap<String, Customer>();

	public void init() {
		List<Customer> list = (List<Customer>) this.getHibernateTemplate().find(" FROM Customer ");
		for (Customer customer : list) {
			cache.put(customer.getUsername(), customer);
		}
	}

	public void save(Customer entity) {

		this.getHibernateTemplate().save(entity);
		cache.put(entity.getUsername(), entity);
	}

	/**
	 * 更新
	 * 
	 * @param entity
	 * @param isOnline true:必须在线才更新，false：都能更新
	 */
	public boolean update(Customer entity, boolean isOnline) {
		if (isOnline&&cacheByUsername(entity.getUsername()).getOnline_state() != 1) {
			return false;
		}
		getHibernateTemplate().update(entity);
		cache.put(entity.getUsername(), entity);
		return true;
	}

	public void delete(String username) {
		Customer entity = cacheByUsername(username);
		getHibernateTemplate().delete(entity);
		cache.remove(entity.getUsername());
	}

	public Customer cacheByUsername(String username) {
		return cache.get(username);
	}

	/**
	 * 分配一个在线客服给用户
	 * 
	 * @return
	 */
	public Customer cacheOnlineOne(String partyId) {
		Party agentParty = userRecomService.getAgentParty((Serializable) partyId);
		System.out.println("消息断点 agentParty:"+agentParty);
		List<Customer> list = new ArrayList<Customer>();
		Map<String, Customer> cacheCustomerList = new ConcurrentHashMap<String, Customer>();

		if (agentParty != null && agentParty.getId() != null) {
			System.out.println("partyId："+partyId+"存在代理："+agentParty.getId());
			// 存在代理，客服列表查询代理，如果没有代理客服列表，那就往下走
			List<Customer> agentCustomerList = (List<Customer>) this.getHibernateTemplate().find(" FROM Customer WHERE AGENT_PARTY_ID=?0", new Object[]{agentParty.getId()});

			System.out.println("代理客服列表："+agentCustomerList);

			if (agentCustomerList.size() > 0) {
				// 代理下存在任意客服
				for (Customer customer : agentCustomerList) {
					cacheCustomerList.put(customer.getUsername(), customer);
				}
			}else{
				//代理下不存在客服 用公共客服
				List<Customer> customerList = (List<Customer>) this.getHibernateTemplate().find(" FROM Customer WHERE AGENT_PARTY_ID IS NULL");
				for (Customer customer : customerList) {
					cacheCustomerList.put(customer.getUsername(), customer);
				}
			}
			list = new ArrayList<Customer>(cacheCustomerList.values());
		} else {
			System.out.println("partyId："+partyId+"不存在代理");
			// 存在代理，客服列表查询代理，如果没有代理客服列表，那就往下走
			List<Customer> customerList = (List<Customer>) this.getHibernateTemplate().find(" FROM Customer WHERE AGENT_PARTY_ID IS NULL");

			System.out.println("公共客服列表："+customerList);

			if (customerList.size() > 0) {
				for (Customer customer : customerList) {
					cacheCustomerList.put(customer.getUsername(), customer);
				}
			}
			list = new ArrayList<Customer>(cacheCustomerList.values());
		}
		System.out.println("list1："+list);
		CollectionUtils.filter(list, new Predicate() {// 在线客服
			@Override
			public boolean evaluate(Object arg0) {
				// TODO Auto-generated method stub
				return ((Customer) arg0).getOnline_state() == 1;
			}
		});
		System.out.println("list2："+list);
		if (CollectionUtils.isEmpty(list))
			return null;
		Collections.sort(list, new Comparator<Customer>() {
			@Override
			public int compare(Customer arg0, Customer arg1) {
				// TODO Auto-generated method stub
				if (arg0.getLast_customer_time() == null) {
					return -1;
				} else if (arg1.getLast_customer_time() == null) {
					return 1;
				}
				return (int) (arg0.getLast_customer_time().getTime() - arg1.getLast_customer_time().getTime());
			}
		});
		System.out.println("使用客服："+list.get(0));
		return list.get(0);
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

}
