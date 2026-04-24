package com.meetchance.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meetchance.permission.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0 " +
            "ORDER BY p.level, p.sort")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0 " +
            "ORDER BY p.level, p.sort")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
