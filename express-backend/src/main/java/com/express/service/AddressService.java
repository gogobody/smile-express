package com.express.service;

import com.express.domain.Address;
import com.express.exception.BusinessException;
import com.express.web.support.ResponseCodes;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 地址管理业务类
 */

@IocBean(args = { "refer:dao" })
public class AddressService extends BaseService<Address> {



    public AddressService(Dao dao) {
        super(dao);
    }

    /**
     * insert object of address
     * @param address object
     * @param userId  user id
     * @throws Exception
     */
    public void insert(final Address address, final Long userId) throws Exception{

        try {
            Trans.exec(new Atom(){
                public void run() {
                    dao().insert(address);
                    Sql sql = Sqls.create("INSERT INTO $table (addressid,userid) VALUES(@addressid,@userid)");
                    // 为变量占位符设值
                    sql.vars().set("table","address_user");
                    // 为参数占位符设值
                    sql.params().set("addressid",address.getId()).set("userid",userId);
                    dao().execute(sql);
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }

    }

    /**
     * update object of address
     * @param address object
     */
    public int update(Address address) {
        return dao().update(address);
    }


    /**
     * 获取地址列表
     * @param address object
     * @param userId 用户id
     * @return List<Address>
     */
    public List<Address> list(Address address, Long userId) {
        Sql sql = Sqls.create("select * from $table1 where address.id in (SELECT addressid from $table2 where userid = @userId) and areaName like @areaName");
        sql.vars().set("table1", "address");
        sql.vars().set("table2", "address_user");
        sql.params().set("userId", userId).set("areaName", address.getAreaName()+"%");
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<Address> list = new LinkedList<Address>();
                while (rs.next()) {
                    Address addr = new Address();
                    addr.setId(rs.getLong("id"));
                    addr.setLongitude(rs.getFloat("longitude"));
                    addr.setLatitude(rs.getFloat("latitude"));
                    addr.setAreaName(rs.getString("areaName"));
                    addr.setPhone(rs.getString("phone"));
                    addr.setName(rs.getString("name"));
                    addr.setAdcode(rs.getString("adcode"));
                    addr.setAddress(rs.getString("address"));
                    list.add(addr);
                }
                return list;
            }
        });
        dao().execute(sql);
        return sql.getList(Address.class);
    }

    /**
     * 删除地址
     * @param addrId  地址id
     */
    public void delete(final Long addrId) throws Exception{

        try {
            Trans.exec(new Atom(){
                public void run() {
                    dao().delete(Address.class, addrId);
                    dao().clear("address_user", Cnd.where("addressid", "=", addrId));
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BusinessException(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR);
        }
    }


    public Address find(Long addrId) {
        return dao().fetch(Address.class, addrId);
    }



}
