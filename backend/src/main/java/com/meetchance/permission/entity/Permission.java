package com.meetchance.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long serviceId;
    
    private Long parentId;
    
    private String permissionCode;
    
    private String permissionName;
    
    private String permissionType;
    
    private String resourceType;
    
    private String resourcePath;
    
    private Integer level;
    
    private Integer sort;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private List<Permission> children;
}
