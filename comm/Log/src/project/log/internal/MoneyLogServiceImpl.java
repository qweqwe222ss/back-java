package project.log.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.orders.model.MallOrderRebate;
import project.mall.orders.model.MallOrdersPrize;

public class MoneyLogServiceImpl extends HibernateDaoSupport implements MoneyLogService {
	protected PagedQueryDao pagedDao;

	public void save(MoneyLog moneyLog) {
		if (moneyLog.getCreateTime() == null) {
			moneyLog.setCreateTime(new Date());
		}
		getHibernateTemplate().save(moneyLog);
	}

	public Page pagedQuery(int pageNo, int pageSize, String category, String content_type, String partyId, Date startTime, Date endTime) {
		StringBuffer queryString = new StringBuffer("");
		queryString.append(" FROM MoneyLog WHERE 1=1 AND content_type NOT IN ('changesub')");
		Map parameters = new HashMap();

		if (StringUtils.isNotEmpty(category)) {
			queryString.append(" AND category =:category");
			parameters.put("category", category);
		}
		if(StringUtils.isNotEmpty(content_type)){
			queryString.append(" AND content_type =:content_type");
			parameters.put("content_type", content_type);
		}
		if (StringUtils.isNotEmpty(partyId)) {
			queryString.append(" AND partyId =:partyId");
			parameters.put("partyId", partyId);
		}
		if (null != startTime){
			queryString.append(" AND createTime >=:startTime");
			parameters.put("startTime", startTime);
		}
		if (null != endTime){
			queryString.append(" AND createTime <=:endTime");
			parameters.put("endTime", endTime);
		}
		queryString.append(" order by createTime desc ");
		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public List<MoneyLog> findLogsByConentTypeAndDate(String type, String date) {
		StringBuffer queryString = new StringBuffer("");
		List<Object> paras = new LinkedList<Object>();
		queryString.append(" FROM MoneyLog WHERE 1=1 ");
		if (StringUtils.isNotEmpty(type)) {
			queryString.append(" AND content_type =?0");
			paras.add(type);
		}
		if (StringUtils.isNotEmpty(date)) {
			queryString.append(" AND DATE(createTime) =DATE(?1)");
			paras.add(date);
		}
		List<MoneyLog> find=(List<MoneyLog>) this.getHibernateTemplate().find(queryString.toString(),paras);
		return find;
	}

	@Override
	public List<MoneyLog> findByLog(String type, String log) {
		DetachedCriteria query = DetachedCriteria.forClass(MoneyLog.class);
		query.add( Property.forName("content_type").eq(type) );
		query.add( Property.forName("log").eq(log) );
		query.addOrder(Order.desc("createTime"));
		return (List<MoneyLog>) getHibernateTemplate().findByCriteria(query,0,1);
	}

	@Override
	public List<MallOrderRebate> getOrderRebate(String orderId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MallOrderRebate.class);
		criteria.add(Property.forName("orderId").eq(orderId));
		criteria.addOrder(Order.desc("level"));
		return (List<MallOrderRebate>)this.getHibernateTemplate().findByCriteria(criteria);
	}


	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public PagedQueryDao getPagedDao() {
		return pagedDao;
	}

}
