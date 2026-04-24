package com.meetchance.permission.controller;

import com.meetchance.permission.common.Result;
import com.meetchance.permission.entity.Permission;
import com.meetchance.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/list")
    public Result<List<Permission>> list(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String permissionType) {
        List<Permission> permissions = permissionService.list(serviceId, permissionType);
        return Result.success(permissions);
    }

    @GetMapping("/tree")
    public Result<List<Permission>> tree(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String permissionType) {
        List<Permission> tree = permissionService.tree(serviceId, permissionType);
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Permission permission) {
        permissionService.update(permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }

    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getUserPermissions(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    @GetMapping("/user/{userId}/tree")
    public Result<List<Permission>> getUserPermissionTree(@PathVariable Long userId) {
        List<Permission> tree = permissionService.getUserPermissionTree(userId);
        return Result.success(tree);
    }
}
