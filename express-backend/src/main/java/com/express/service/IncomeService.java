package com.express.service;

import com.express.common.page.Pagination;
import com.express.domain.Income;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.util.cri.Exps;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

@IocBean(args = { "refer:dao" })
public class IncomeService extends BaseService<Income>{

    public IncomeService(Dao dao) {
        super(dao);
    }


    /**
     * 收入明细列表
     * @param userId
     * @param page
     * @return
     */
    public List<Income> list(Long userId, Pagination page) {
        Criteria cri = Cnd.cri();

        if(userId == 0) { //则不受限于用户

        } else {
            cri.where().and(Exps.inSql("id", "select incomeid from user_income where userid = "+userId));
        }
        cri.getOrderBy().desc("id");
        List<Income> incomes = dao().query(Income.class, cri, dao().createPager(page.getPageNo(), page.getPageSize()));
        return incomes;
    }

    public double amount(Long userId) {
        Criteria cri = Cnd.cri();

        cri.where().and(Exps.inSql("id", "select incomeid from user_income where userid = "+userId));

        cri.getOrderBy().desc("id");
        List<Income> incomes = dao().query(Income.class, cri);
        double amount = 0;
        for(int i = 0; i < incomes.size(); i++) {
            if(incomes.get(i).getType() == 0) {// 收入
                amount = amount + incomes.get(i).getAmount();
            } else {
                amount = amount - incomes.get(i).getAmount();
            }
        }
        return amount;
    }

}
