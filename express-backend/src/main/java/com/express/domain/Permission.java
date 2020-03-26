package com.express.domain;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 设置权限菜单，用于关联角色
 */
@Table("system_permission")
public class Permission  extends BaseBean{
    private static final long serialVersionUID = -8140799124476746216L;
    @Id
    private Long id;
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String name;  // 权限名称
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String description;
    @ManyMany(target = Role.class, relation = "system_role_permission", from = "permissionid", to = "roleid")
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /*@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permission other = (Permission) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

}
