package com.express.domain;

import org.nutz.dao.entity.annotation.*;

@Table("user_info")
public class UserInfo extends BaseBean{

    private static final long serialVersionUID = -965829144356813385L;

    @Id
    private Long id;    // 逻辑id

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String reason;  // 申请理由


    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String addr;  // 住所

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String photo;  // 图片

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String phone;  // 联系电话

    @Column
    @ColDefine(type = ColType.INT)
    @Default("0")
    private Long userId;   // 所关联的用户id


    @Column("is_exam")
    @Default("false")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isExam;   // 是否审核

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isExam() {
        return isExam;
    }

    public void setExam(boolean exam) {
        isExam = exam;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
