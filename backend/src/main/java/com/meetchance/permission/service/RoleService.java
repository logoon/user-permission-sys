package com.meetchance.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetchance.permission.entity.Permission;
import com.meetchance.permission.entity.Role;
import com.meetchance.permission.entity.RolePermission;
import com.meetchance.permission.mapper.PermissionMapper;
import com.meetchance.permission.mapper.RoleMapper;
import com.meetchance.permission.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public Page<Role> page(Integer pageNum, Integer pageSize, String roleName, String roleType, Integer status) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName)
                .eq(StringUtils.hasText(roleType), Role::getRoleType, roleType)
                .eq(status != null, Role::getStatus, status)
                .orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }

    public List<Role> list() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getId);
        return roleMapper.selectList(wrapper);
    }

    public List<Role> listByService(Long serviceId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .eq(serviceId != null, Role::getServiceId, serviceId)
                .orderByAsc(Role::getId);
        return roleMapper.selectList(wrapper);
    }

    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Role role) {
        if (role.getServiceId() == null) {
            throw new RuntimeException("请选择关联服务");
        }
        roleMapper.insert(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        roleMapper.deleteById(id);
        rolePermissionMapper.deleteByRoleId(id);
    }

    public List<Permission> getRolePermissions(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    public List<Permission> getAssignablePermissions(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        Long serviceId = role.getServiceId();
        if (serviceId == null) {
            return List.of();
        }
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getServiceId, serviceId)
                .orderByAsc(Permission::getLevel, Permission::getSort);
        return permissionMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        Long serviceId = role.getServiceId();
        
        if (permissionIds != null && !permissionIds.isEmpty() && serviceId != null) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Permission::getId, permissionIds);
            List<Permission> permissions = permissionMapper.selectList(wrapper);
            
            for (Permission p : permissions) {
                if (!serviceId.equals(p.getServiceId())) {
                    throw new RuntimeException("权限 " + p.getPermissionName() + " 不属于角色关联的服务");
                }
            }
        }
        
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
}
