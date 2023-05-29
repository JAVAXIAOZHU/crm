package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role, Integer> {
    // 查询⻆⾊列表
    public List<Map<String,Object>> queryAllRoles(Integer userId);
    //通过角色名称查询角色列表
   public  Role selectByRoleName(String roleName);
}