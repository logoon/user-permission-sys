package com.meetchance.permission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetchance.permission.common.Result;
import com.meetchance.permission.entity.Permission;
import com.meetchance.permission.entity.Role;
import com.meetchance.permission.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/page")
    public Result<Page<Role>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String roleName,
            String roleType,
            Integer status) {
        Page<Role> page = roleService.page(pageNum, pageSize, roleName, roleType, status);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }

    @GetMapping("/list-by-service")
    public Result<List<Role>> listByService(@RequestParam(required = false) Long serviceId) {
        List<Role> roles = roleService.listByService(serviceId);
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @GetMapping("/{roleId}/permissions")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    @GetMapping("/{roleId}/permission-ids")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }

    @GetMapping("/{roleId}/assignable-permissions")
    public Result<List<Permission>> getAssignablePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = roleService.getAssignablePermissions(roleId);
        return Result.success(permissions);
    }

    @PostMapping("/{roleId}/assign-permissions")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
}
