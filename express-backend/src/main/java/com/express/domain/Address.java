package com.express.domain;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 地址保存
 */
@Table("address")
public class Address extends BaseBean{

    private static final long serialVersionUID = -965829144356813385L;

    @Id
    private Long id;    // 逻辑id

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String areaName;  // 地址名称

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float longitude;  // 经度

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float latitude;  // 纬度

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String phone;    // 手机号

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String adcode;   // 行政区划代码

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String address; // 详细地址

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String name;  // 姓名


    @ManyMany(relation = "address_user", from = "addressid", to = "userid")
    private List<User> users;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
