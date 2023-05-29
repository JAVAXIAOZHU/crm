package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    /**
     * 查询所有的资源列表
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModule> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    @RequestMapping("/index")
    public String index(){
        return "module/module";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> moduleList(){
        return moduleService.moduleList();
    }

    // 添加资源⻚视图转发
    @RequestMapping("addModulePage")
    public String addModulePage(Integer grade,Integer parentId,Model model){
        model.addAttribute("grade",grade);
        model.addAttribute("parentId",parentId);
        return "module/add";
    }
    // 更新资源⻚视图转发
    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id,Model model){
        model.addAttribute("module",moduleService.selectByPrimaryKey(id));
        return "module/update";
    }

    /**
     * 资源列表添加
     * @param module
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveModule(Module module){
        moduleService.saveModule(module);
        return success("菜单添加成功");
    }

    @RequestMapping("queryAllModulesByGrade")
    @ResponseBody
    public List<Map<String,Object>> queryAllModulesByGrade(Integer grade){
        return moduleService.queryAllModulesByGrade(grade);
    }

    /**
     *资源列表更新
     * @param module
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("菜单更新成功");
    }

    /**
     * 资源列表删除
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModuleById(id);
        return success("菜单删除成功");
    }
}
