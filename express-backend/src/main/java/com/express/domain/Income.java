package com.express.domain;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 收入明细表
 */
@Table("income")
public class Income extends BaseBean {

    @Id
    private Long id;

    @Column
    @ColDefine(type = ColType.FLOAT)
    private Double amount;  // 金额

    @Column
    @ColDefine(type = ColType.INT)  // 0:收入, 1:支出
    @Default("0")
    private int type;  // 金额

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String description; // 明细说明

    @ManyMany(target = User.class, relation = "user_income", from = "incomeid", to = "userid")
    private List<User> users;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
