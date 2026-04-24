package com.meetchance.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meetchance.permission.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(Long roleId);
}
