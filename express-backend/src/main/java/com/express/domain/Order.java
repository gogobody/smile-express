package com.express.domain;


import org.nutz.dao.entity.annotation.*;

@Table("orders")
public class Order extends BaseBean{


    @Id
    private Long id;    // 逻辑id

    @Name
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String orderId; // 订单号

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String type; // "help" or "buy"

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String getTime;  // 时间

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    @Default("")
    private String finish_time;  // 已完成时间

    @Column
    @ColDefine(type = ColType.FLOAT)
    private Double amount;  // 总额

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float markiUserFee;  // 时费用

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float markiUserRate;  // 时速

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float mileageTotal;  //

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float singleAmount;  //

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String note;  //说明


    @Column
    @ColDefine(type = ColType.FLOAT)
    private float startMileage;  //start_km

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float startAmount;  //start_price


    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String f_address;  //areaName+address

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float f_longitude;  // 经度

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float f_latitude;  // 纬度


    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String f_area3;  //adcode

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String f_name;  //name

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String f_phone;  //f_phone


    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String t_area3;  //adcode

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String t_name;  //name

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String t_phone;  //f_phone

    @Column
    @ColDefine(type = ColType.VARCHAR,  width = 200)
    private String t_address;  //areaName+address

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float t_longitude;  // 经度

    @Column
    @ColDefine(type = ColType.FLOAT)
    private float t_latitude;  // 纬度

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @Default("")
    private String content;  // 评价

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Default("[]")
    private String label;  // label

    @Column
    @ColDefine(type = ColType.INT)
    @Default("0")
    private float score;  // score

    @Column
    @ColDefine(type = ColType.INT)
    private int orderStatus;  // 订单状态 1.待付款 2.待取货 3.待送货 4.待评论 5.已完成 6已取消




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGetTime() {
        return getTime;
    }

    public void setGetTime(String getTime) {
        this.getTime = getTime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public float getMarkiUserFee() {
        return markiUserFee;
    }

    public void setMarkiUserFee(float markiUserFee) {
        this.markiUserFee = markiUserFee;
    }

    public float getMarkiUserRate() {
        return markiUserRate;
    }

    public void setMarkiUserRate(float markiUserRate) {
        this.markiUserRate = markiUserRate;
    }

    public float getMileageTotal() {
        return mileageTotal;
    }

    public void setMileageTotal(float mileageTotal) {
        this.mileageTotal = mileageTotal;
    }

    public float getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(float singleAmount) {
        this.singleAmount = singleAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(float startMileage) {
        this.startMileage = startMileage;
    }

    public float getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(float startAmount) {
        this.startAmount = startAmount;
    }

    public String getF_address() {
        return f_address;
    }

    public void setF_address(String f_address) {
        this.f_address = f_address;
    }

    public float getF_longitude() {
        return f_longitude;
    }

    public void setF_longitude(float f_longitude) {
        this.f_longitude = f_longitude;
    }

    public float getF_latitude() {
        return f_latitude;
    }

    public void setF_latitude(float f_latitude) {
        this.f_latitude = f_latitude;
    }

    public String getF_area3() {
        return f_area3;
    }

    public void setF_area3(String f_area3) {
        this.f_area3 = f_area3;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_phone() {
        return f_phone;
    }

    public void setF_phone(String f_phone) {
        this.f_phone = f_phone;
    }

    public String getT_area3() {
        return t_area3;
    }

    public void setT_area3(String t_area3) {
        this.t_area3 = t_area3;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }

    public String getT_phone() {
        return t_phone;
    }

    public void setT_phone(String t_phone) {
        this.t_phone = t_phone;
    }

    public String getT_address() {
        return t_address;
    }

    public void setT_address(String t_address) {
        this.t_address = t_address;
    }

    public float getT_longitude() {
        return t_longitude;
    }

    public void setT_longitude(float t_longitude) {
        this.t_longitude = t_longitude;
    }

    public float getT_latitude() {
        return t_latitude;
    }

    public void setT_latitude(float t_latitude) {
        this.t_latitude = t_latitude;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", type='" + type + '\'' +
                ", getTime='" + getTime + '\'' +
                ", finish_time='" + finish_time + '\'' +
                ", amount=" + amount +
                ", markiUserFee=" + markiUserFee +
                ", markiUserRate=" + markiUserRate +
                ", mileageTotal=" + mileageTotal +
                ", singleAmount=" + singleAmount +
                ", note='" + note + '\'' +
                ", startMileage=" + startMileage +
                ", startAmount=" + startAmount +
                ", f_address='" + f_address + '\'' +
                ", f_longitude=" + f_longitude +
                ", f_latitude=" + f_latitude +
                ", f_area3='" + f_area3 + '\'' +
                ", f_name='" + f_name + '\'' +
                ", f_phone='" + f_phone + '\'' +
                ", t_area3='" + t_area3 + '\'' +
                ", t_name='" + t_name + '\'' +
                ", t_phone='" + t_phone + '\'' +
                ", t_address='" + t_address + '\'' +
                ", t_longitude=" + t_longitude +
                ", t_latitude=" + t_latitude +
                ", orderStatus=" + orderStatus +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
