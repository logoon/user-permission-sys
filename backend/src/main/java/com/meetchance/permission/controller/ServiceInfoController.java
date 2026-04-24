package com.meetchance.permission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetchance.permission.common.Result;
import com.meetchance.permission.entity.ServiceInfo;
import com.meetchance.permission.service.ServiceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceInfoController {

    private final ServiceInfoService serviceInfoService;

    @GetMapping("/page")
    public Result<Page<ServiceInfo>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String serviceName,
            String serviceCode,
            Integer status) {
        Page<ServiceInfo> page = serviceInfoService.page(pageNum, pageSize, serviceName, serviceCode, status);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<ServiceInfo>> list() {
        List<ServiceInfo> services = serviceInfoService.list();
        return Result.success(services);
    }

    @GetMapping("/{id}")
    public Result<ServiceInfo> getById(@PathVariable Long id) {
        ServiceInfo serviceInfo = serviceInfoService.getById(id);
        return Result.success(serviceInfo);
    }

    @PostMapping
    public Result<Void> save(@RequestBody ServiceInfo serviceInfo) {
        serviceInfoService.save(serviceInfo);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody ServiceInfo serviceInfo) {
        serviceInfoService.update(serviceInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        serviceInfoService.delete(id);
        return Result.success();
    }
}
