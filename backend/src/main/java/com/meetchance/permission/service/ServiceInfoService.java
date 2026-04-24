package com.meetchance.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetchance.permission.entity.ServiceInfo;
import com.meetchance.permission.mapper.ServiceInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceInfoService {

    private final ServiceInfoMapper serviceInfoMapper;

    public Page<ServiceInfo> page(Integer pageNum, Integer pageSize, String serviceName, String serviceCode, Integer status) {
        Page<ServiceInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(serviceName), ServiceInfo::getServiceName, serviceName)
                .like(StringUtils.hasText(serviceCode), ServiceInfo::getServiceCode, serviceCode)
                .eq(status != null, ServiceInfo::getStatus, status)
                .orderByDesc(ServiceInfo::getCreateTime);
        return serviceInfoMapper.selectPage(page, wrapper);
    }

    public List<ServiceInfo> list() {
        LambdaQueryWrapper<ServiceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceInfo::getStatus, 1)
                .orderByAsc(ServiceInfo::getId);
        return serviceInfoMapper.selectList(wrapper);
    }

    public ServiceInfo getById(Long id) {
        return serviceInfoMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(ServiceInfo serviceInfo) {
        serviceInfoMapper.insert(serviceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ServiceInfo serviceInfo) {
        serviceInfoMapper.updateById(serviceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        serviceInfoMapper.deleteById(id);
    }
}
