package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole ,Integer> {
    //根据用户Id查询用户角色记录
    Integer countUserRoleByUserId(Integer userId);
    //根据用户Id删除用户记录
    Integer deleteUserRoleByUserId(Integer userId);
}