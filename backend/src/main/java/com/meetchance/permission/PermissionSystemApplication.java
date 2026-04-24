package com.meetchance.permission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.meetchance.permission.mapper")
public class PermissionSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionSystemApplication.class, args);
    }
}
