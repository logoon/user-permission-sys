package com.meetchance.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetchance.permission.entity.Role;
import com.meetchance.permission.entity.User;
import com.meetchance.permission.entity.UserRole;
import com.meetchance.permission.mapper.RoleMapper;
import com.meetchance.permission.mapper.UserMapper;
import com.meetchance.permission.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public Page<User> page(Integer pageNum, Integer pageSize, String username, String nickname, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username)
                .like(StringUtils.hasText(nickname), User::getNickname, nickname)
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(User user) {
        userMapper.insert(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
    }

    public List<Role> getUserRoles(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }
}
