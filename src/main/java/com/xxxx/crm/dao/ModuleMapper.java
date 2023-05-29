package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

  

    //查询所有的资源列表
    public List<TreeModule> queryAllModules();
    //  查询所有的资源数据
   public List<Module> queryModules();

   //通过层级与木块名查询资源对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade,@Param("moduleName") String moduleName);
    //通过层级与URL查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade,@Param("url")  String url);
    //通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);
    //查询指定资源是否存在子记录
    Integer querySubModuleByParentId(Integer mid);

    List<Map<String, Object>> queryAllModulesByGrade(Integer grade);
}