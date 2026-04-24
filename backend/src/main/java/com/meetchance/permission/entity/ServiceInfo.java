package com.meetchance.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_service")
public class ServiceInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String serviceCode;
    
    private String serviceName;
    
    private String serviceUrl;
    
    private String description;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
