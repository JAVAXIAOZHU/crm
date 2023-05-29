package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;


    /**
     * 多条件分⻚查询营销机会
     * 如果 flag的值不为空且值为1 则表示当前查询的是客户开发计划  否则查询的是营销机会数据
     * @param saleChanceQuery
     * @param flag
     * @param request
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequirePermission(code = "101001")
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery saleChanceQuery , Integer flag , HttpServletRequest request) {
        //判断flag的值
        if (flag !=null && flag == 1){
            //查询客户开发计划
            //设置分配状态
            //默认是已分配
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置指派人（当前登录用户的Id）
            //从cookie中获取当前登录用户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        //如果flag不是1   就是查询营销机会数据
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }


    /**
     * 进⼊营销机会⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index () {
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销机会
     * @param saleChance
     * @param request
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        //从cookie中获取当前登录的用户名、
        String userName= CookieUtil.getCookieValue(request,"userName");
        //设置用户名到营销机会对象
        saleChance.setCreateMan(userName);
        //调用service层的添加方法
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功!");
    }


    /**
     * 更新营销机会数据
     * @param request
     * @param saleChance
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 更新营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功！");
    }


    /**
     * 机会数据添加与更新表单⻚⾯视图转发
     * id为空 添加操作
     * id⾮空 修改操作
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        // 如果id不为空，表示是修改操作，修改操作需要查询被修改的数据
        if (null != id) {
            // 通过主键查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            // 将数据存到作⽤域中
            model.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance (Integer[] ids) {
        // 删除营销机会的数据
        saleChanceService.deleteBatch(ids);
        return success("营销机会数据删除成功！");
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功！");
    }
}
