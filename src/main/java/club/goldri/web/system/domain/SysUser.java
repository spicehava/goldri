package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Table(name = "sys_user")
public class SysUser extends BaseDomain {
    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码：加密算法（公盐+私盐）
     */
    private String password;

    /**
     * 原密码
     */
    @Transient
    private String oriPassword;

    /**
     * 盐：加密用
     */
    private String salt;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 职位。数据字典：UserPosition
     */
    private String position;

    /**
     * 入职日期：精确到天
     */
    @Column(name = "enter_date")
    private Date enterDate;

    /**
     * 性别。数据字典：UserSex
     */
    private String sex;

    /**
     * 办公电话：可以是座机，也可以是手机
     */
    private String telphone;

    /**
     * 移动电话：用于发送短信
     */
    private String mobile;

    /**
     * 邮件：用于发送邮件信息
     */
    private String email;

    /**
     * 用户状态：默认NORMAL。数据字典：UserState
     */
    private String state;

    //-----------------------------------------------------扩展字段-----------------------------------------------------
    @Transient
    private List<SysOrg> orgList ;

    @Transient
    private List<SysRole> roleList;

    @Transient
    private String orgName;
    /**
     * 获取登录用户名
     *
     * @return username - 登录用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置登录用户名
     *
     * @param username 登录用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取登录密码：加密算法（公盐+私盐）
     *
     * @return PASSWORD - 登录密码：加密算法（公盐+私盐）
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码：加密算法（公盐+私盐）
     *
     * @param password 登录密码：加密算法（公盐+私盐）
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriPassword() {
        return oriPassword;
    }

    public void setOriPassword(String oriPassword) {
        this.oriPassword = oriPassword;
    }

    /**
     * 获取盐：加密用
     *
     * @return salt - 盐：加密用
     */
    public String getSalt() {
        return salt;
    }

    /**
     * 设置盐：加密用
     *
     * @param salt 盐：加密用
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * 获取用户姓名
     *
     * @return name - 用户姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户姓名
     *
     * @param name 用户姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取职位。数据字典：UserPosition
     *
     * @return position - 职位。数据字典：UserPosition
     */
    public String getPosition() {
        return position;
    }

    /**
     * 设置职位。数据字典：UserPosition
     *
     * @param position 职位。数据字典：UserPosition
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 获取入职日期：精确到天
     *
     * @return enter_date - 入职日期：精确到天
     */
    public Date getEnterDate() {
        return enterDate;
    }

    /**
     * 设置入职日期：精确到天
     *
     * @param enterDate 入职日期：精确到天
     */
    public void setEnterDate(Date enterDate) {
        this.enterDate = enterDate;
    }

    /**
     * 获取性别。数据字典：UserSex
     *
     * @return sex - 性别。数据字典：UserSex
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置性别。数据字典：UserSex
     *
     * @param sex 性别。数据字典：UserSex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取办公电话：可以是座机，也可以是手机
     *
     * @return telphone - 办公电话：可以是座机，也可以是手机
     */
    public String getTelphone() {
        return telphone;
    }

    /**
     * 设置办公电话：可以是座机，也可以是手机
     *
     * @param telphone 办公电话：可以是座机，也可以是手机
     */
    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    /**
     * 获取移动电话：用于发送短信
     *
     * @return mobile - 移动电话：用于发送短信
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置移动电话：用于发送短信
     *
     * @param mobile 移动电话：用于发送短信
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取邮件：用于发送邮件信息
     *
     * @return email - 邮件：用于发送邮件信息
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮件：用于发送邮件信息
     *
     * @param email 邮件：用于发送邮件信息
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取用户状态：默认NORMAL。数据字典：UserState
     *
     * @return state - 用户状态：默认NORMAL。数据字典：UserState
     */
    public String getState() {
        return state;
    }

    /**
     * 设置用户状态：默认NORMAL。数据字典：UserState
     *
     * @param state 用户状态：默认NORMAL。数据字典：UserState
     */
    public void setState(String state) {
        this.state = state;
    }

    public List<SysOrg> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<SysOrg> orgList) {
        this.orgList = orgList;
    }

    public List<SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SysRole> roleList) {
        this.roleList = roleList;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", enterDate=" + enterDate +
                ", sex='" + sex + '\'' +
                ", telphone='" + telphone + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", state='" + state + '\'' +
                ", id='" + this.getId() + '\'' +
                ", craeteBy='" + this.getCreateBy() + '\'' +
                ", createDate='" + this.getCreateDate() + '\'' +
                ", updateBy='" + this.getUpdateBy() + '\'' +
                ", updateDate='" + this.getUpdateDate() + '\'' +
                ", delFlag='" + this.getDelFlag() + '\'' +
                '}';
    }
}