package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.service.PermissionService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private PermissionService permissionService;
    @Resource
    UserService userService=new UserService();
    /**
     * 系统登录⻚
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    // 系统界⾯欢迎⻚
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主⻚⾯
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        //在页面右上角显示用户信息
        //通过工具类 从cookie汇总获取userId
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //调用对应的Service层的方法 通过userId主键查询对象
        User user = userService.selectByPrimaryKey(userId);
        //把用户信息设置到session作用域中
        request.getSession().setAttribute("user",user);

        //通过当前登录用户Id查询当前登录用户拥有的资源列表（查询对应资源的授权码）
        List<String > permissions=permissionService.queryUserHasRoleHasPermissionByUserId(userId);
        //将集合设置 到session作用域中
        request.getSession().setAttribute("permissions",permissions);

        return "main";
    }
}
