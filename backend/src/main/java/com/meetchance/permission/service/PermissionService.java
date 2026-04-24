package com.meetchance.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meetchance.permission.entity.Permission;
import com.meetchance.permission.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;

    public List<Permission> list(Long serviceId, String permissionType) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(serviceId != null, Permission::getServiceId, serviceId)
                .eq(StringUtils.hasText(permissionType), Permission::getPermissionType, permissionType)
                .orderByAsc(Permission::getLevel, Permission::getSort);
        return permissionMapper.selectList(wrapper);
    }

    public List<Permission> tree(Long serviceId, String permissionType) {
        List<Permission> allPermissions = list(serviceId, permissionType);
        return buildTree(allPermissions);
    }

    private List<Permission> buildTree(List<Permission> permissions) {
        Map<Long, List<Permission>> parentMap = permissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));
        
        permissions.forEach(p -> {
            List<Permission> children = parentMap.get(p.getId());
            if (children != null && !children.isEmpty()) {
                p.setChildren(children);
            } else {
                p.setChildren(new ArrayList<>());
            }
        });
        
        return permissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());
    }

    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Permission permission) {
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
            permission.setLevel(1);
        } else {
            Permission parent = permissionMapper.selectById(permission.getParentId());
            if (parent != null) {
                int level = parent.getLevel() + 1;
                if (level > 3) {
                    throw new RuntimeException("权限层级不能超过3层");
                }
                permission.setLevel(level);
            } else {
                permission.setLevel(1);
            }
        }
        permissionMapper.insert(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        List<Permission> children = permissionMapper.selectList(wrapper);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子权限，无法删除");
        }
        permissionMapper.deleteById(id);
    }

    public List<Permission> getUserPermissions(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }

    public List<Permission> getUserPermissionTree(Long userId) {
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(userId);
        return buildTree(permissions);
    }
}
