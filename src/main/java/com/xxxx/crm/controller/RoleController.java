package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;



    /**
     * 查询⻆⾊列表
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    /**
     * 点击用户管理之后，视图跳转到role页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }


    /**
     * 分页条件查询角色列表
     * @param roleQuery
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> selectByparams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }


    /**
     * 添加角色
     * @param role
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("角色添加成功!");
    }

    /**
     * 视图转发到添加页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer id, Model model){
        if(null !=id){
            model.addAttribute("role",roleService.selectByPrimaryKey(id));
        }
        return "role/add_update";
    }


    /**
     * 更新角色记录
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("⻆⾊记录更新成功");
    }

    /**
     * 删除角色记录
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("⻆⾊记录删除成功");
    }

    /**
     * 视图转发⽅法，转发到授权界面
     * @param roleId
     * @param model
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 角色授权
     * @param roleId
     * @param mIds
     * @return
     */
    @PostMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId , Integer[] mIds){
        roleService.addGrant(roleId,mIds);
        return success("角色授权成功！");
    }
}
