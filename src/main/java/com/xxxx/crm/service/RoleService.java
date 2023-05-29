package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role, Integer> {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;


    /**
     * 查询⻆⾊列表
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }


    /**
     * 添加角色
     *  1. 参数校验
     *      角色名称  非空且唯一
     *  2. 设置参数参数默认值
     *      是否有效
     *      创建时间
     *      修改时间
     *   3. 执行添加操作，判断受影响的行数
     * @param role
     */
    @Transactional (propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        // 1. 参数校验
        //     角色名称  非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不能为空!");
        //通过角色名称查询角色记录
        Role temp=roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp,"该⻆⾊已存在!");
        // 2. 设置参数参数默认值
        //     是否有效
        //     创建时间
        //     修改时间
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //  3. 执行添加操作，判断受影响的行数
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1 , "角色添加失败！");
    }

    /**
     * 更新角色记录
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        //参数校验
        AssertUtil.isTrue(null==role.getId()||null==selectByPrimaryKey(role.getId()),"待修改的记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输⼊⻆⾊名!");
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(role.getId())),"该⻆⾊已存在!");
        //设置默认属性值
        role.setUpdateDate(new Date());
        //执行更新操作 返回受影响的行数
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"⻆⾊记录更新失败!");
    }

    /**
     * 删除角色记录
     * @param roleId
     */
    public void deleteRole(Integer roleId){
        Role temp =selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId||null==temp,"待删除的记录不存在!");
        //把is_valid属性设置为0
        temp.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"⻆⾊记录删除失败!");
    }

    /**
     * 角色授权
     *      将对应的角色Id与资源id  添加到对应的权限表中
     *          先将已有的权限记录删除 ，再将需要设置的权限记录添加
     *          1.通过角色Id查询对应的权限记录
     *          2，如果权限记录存在，删除对应的角色拥有的权限记录
     *          3.如果有授权记录 则添加权限记录（批量添加）
     * @param roleId
     * @param mIds
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mIds) {
        //1.通过角色Id查询对应的权限记录
        Integer count=permissionMapper.countPermissionByRoleId(roleId);
        //2，如果权限记录存在，删除对应的角色拥有的权限记录
        if (count >0){
            //删除权限记录
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        //3.如果有授权记录 则添加权限记录（批量添加）
        if (mIds != null && mIds.length>0){
            //定义Permission集合
            List<Permission> permissionList=new ArrayList<>();
            //遍历资源ID数组
            for (Integer mId : mIds ){
                Permission permission=new Permission();
                permission.setModuleId(mId);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //将对象设置到集合中
                permissionList.add(permission);
            }
            //执行批量添加操作  判断受影响的行数
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败！");
        }
    }
}
