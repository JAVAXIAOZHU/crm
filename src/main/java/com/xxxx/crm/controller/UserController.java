package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    /**
     * ⽤户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping ("user/login")
    @ResponseBody  // 前台是Ajax请求 要的是返回的对象 所以加上这个注解，如果不加的话就是返回页面
    public ResultInfo  userLogin(String userName,String userPwd){
        ResultInfo resultInfo=new ResultInfo();
        //捕获service层的异常 如果service层抛出异常 则表示登录失败
        UserModel userModel = userService.userLogin(userName, userPwd);
        //调用ResultInfo的result的值  （将数据返回给请求）
        resultInfo.setResult(userModel);
        /*try {
            //捕获service层的异常 如果service层抛出异常 则表示登录失败
            UserModel userModel = userService.userLogin(userName, userPwd);
            //调用ResultInfo的result的值  （将数据返回给请求）
            resultInfo.setResult(userModel);
        }catch (ParamsException p){
            //捕捉到自定义参数异常
            //设置状态码和提示信息
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
            p.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败！");
        }*/
        return resultInfo;
    }

    /**
     * 修改密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param repeatPassword
     * @return
     */
    @PostMapping("/user/updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,
                                         String newPassword,String repeatPassword){
        ResultInfo resultInfo=new ResultInfo();
        //获取cookie中的userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层修改密码的方法
        userService.updatePassword(userId,oldPassword,newPassword,repeatPassword);
        /*try {
            //获取cookie中的userId
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            //调用service层修改密码的方法
            userService.updatePassword(userId,oldPassword,newPassword,repeatPassword);
        }catch (ParamsException p){
            p.printStackTrace();
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败！！");
            e.printStackTrace();
        }*/
        return resultInfo;
    }

    /**
     * 进入修改密码的页面
     * @return
     */
    @RequestMapping("/user/toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    /**
     * 查询所有的销售⼈员
     * @return
     */
    @RequestMapping("user/queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    /**
     * 多条件查询⽤户数据
     * @param userQuery
     * @return
     */
    @RequestMapping("user/list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }

    /**
     * 进⼊⽤户⻚⾯
     * @return
     */
    @RequestMapping("user/index")
    public String index(){
        return "user/user";
    }


    /**
     * 添加⽤户
     * @param user
     * @return
     */
    @PostMapping("user/save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        System.out.println("---------------------");
        System.out.println(user);
        System.out.println("---------------------");
        userService.saveUser(user);
        return success("⽤户添加成功！");
    }

    /**
     * 进⼊⽤户添加或更新⻚⾯
     * @param id
     * @return
     */
    @RequestMapping("user/addOrUpdateUserPage")
    public String addUserPage(Integer id, HttpServletRequest request){
        if(null != id){
            User user=userService.selectByPrimaryKey(id);
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }


    /**
     * 更新⽤户
     * @param user
     * @return
     */
    @PostMapping("user/update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("⽤户更新成功！");
    }

    /**
     * 删除⽤户
     * @param ids
     * @return
     */
    @RequestMapping("user/delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUserByIds(ids);
        return success("⽤户记录删除成功");
    }
}
