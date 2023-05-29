package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有的资源列表
     * @return
     */
    public List<TreeModule> queryAllModules(Integer roleId){
        //查询所有的资源列表
        List<TreeModule>treeModuleList =moduleMapper.queryAllModules();
        //查询指定角色已经授权过的资源列表（查询角色）
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdsByRoleId(roleId);
        //判断角色是否拥有资源Id
        if (permissionIds !=  null  && permissionIds.size()>0){
            //循环所有的资源列表 判断用户拥有的资源Id中上是否有匹配的，如果有 则设置checked属性为true
            treeModuleList.forEach(treeModule -> {
                //判断角色拥有的资源Id中是否有当前遍历的资源ID
                if (permissionIds.contains(treeModule.getId())){
                    //如果包含 则说明角色售全国 设置checked为true
                    treeModule.setChecked(true);
                }
            });
        }
        return treeModuleList;
    }

    /**
     *
     * @return
     */
    public Map<String,Object> moduleList(){
        Map<String,Object> result = new HashMap<String,Object>();
        List<Module> modules =moduleMapper.queryModules();
        result.put("count",modules.size());
        result.put("data",modules);
        result.put("code",0);
        result.put("msg","");
        return result;
    }


    /**资源记录添加
     * 1.参数校验
     * 模块名-module_name
     * ⾮空 同⼀层级下模块名唯⼀
     * url
     * ⼆级菜单 ⾮空 不可重复
     * 上级菜单-parent_id
     * ⼀级菜单 null
     * ⼆级|三级菜单 parent_id ⾮空 必须存在
     * 层级-grade
     * ⾮空 0|1|2
     * 权限码 optValue
     * ⾮空 不可重复
     * 2.参数默认值设置
     * is_valid create_date update_date
     * 3.执⾏添加 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module){
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输⼊菜单名!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(),module.getModuleName()),"该层级下菜单重复!");
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定⼆级菜单url值");
            AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndUrl(module.getGrade(),module.getUrl()),"⼆级菜单url不可重复!");
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输⼊权限码!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复!");
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(module)<1,"菜单添加失败!");
    }

    /**资源记录更新
     * 1.参数校验
     * id ⾮空 记录存在
     * 模块名-module_name
     * ⾮空 同⼀层级下模块名唯⼀
     * url
     * ⼆级菜单 ⾮空 不可重复
     * 上级菜单-parent_id
     * ⼆级|三级菜单 parent_id ⾮空 必须存在
     * 层级-grade
     * ⾮空 0|1|2
     * 权限码 optValue
     * ⾮空 不可重复
     * 2.参数默认值设置
     * update_date
     * 3.执⾏更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        AssertUtil.isTrue(null == module.getId() || null== selectByPrimaryKey(module.getId()),"待更新记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请指定菜单名称!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        Module temp =moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下菜单已存在!");
        }
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定⼆级菜单url值");
            temp =moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if(null !=temp){
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下url已存在!");
            }
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输⼊权限码!");
        temp =moduleMapper.queryModuleByOptValue(module.getOptValue());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"权限码已存在!");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(module)<1,"菜单更新失败!");
    }

    /**
     * 资源列表删除
     * @param mid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid){
        Module temp =selectByPrimaryKey(mid);
        AssertUtil.isTrue(null == mid || null == temp,"待删除记录不存在!");
        /**
         * 如果存在⼦菜单 不允许删除
         */
        int count = moduleMapper.querySubModuleByParentId(mid);
        AssertUtil.isTrue(count>0,"存在⼦菜单，不⽀持删除操作!");
        // 权限表
        count =permissionMapper.countPermissionsByModuleId(mid);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid)<count,"菜单删除失败!");
        }
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"菜单删除失败!");
    }

    public List<Map<String, Object>> queryAllModulesByGrade(Integer grade) {
        return moduleMapper.queryAllModulesByGrade(grade );
    }
}
