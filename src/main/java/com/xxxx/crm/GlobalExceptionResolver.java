package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 异常处理方法
     *  方法的返回值：
     *      1.返回视图
     *      2.返回数据（JSON数据）
     *
     *   如何判断方法的返回值？
     *      通过方法上是否声明@Response注解
     *          如果未声明，则表示返回的是视图
     *          如果声明了，则表示返回的是数据
     * @param request  request请求对象
     * @param response  response响应对象
     * @param handler 方法对象
     * @param ex        异常对象
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        /**
         * 非法请求拦截
         *  判断是否抛出未登录异常
         *      如果抛出该异常 则要求用户登录
         */
        if (ex instanceof NoLoginException){
            // 如果捕获的是未登录异常，则重定向到登录⻚⾯
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }



        /*
        设置默认异常处理（返回视图）
         */
        ModelAndView modelAndView=new ModelAndView("error");
        //设置异常信息
        modelAndView.addObject("code",500);
        modelAndView.addObject("msg","出现异常，请重试，，，");

        //判断HandlerMethod
        if (handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            //获取方法上声明的@ResponseBody注解对象
            ResponseBody responseBody=handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断ResponseBody对象是否为空（如果对象为空，则表示返回的是视图，如果不为空，则表示返回的是数据对象）
            if (responseBody==null){
                //返回的是视图
                //判断异常类型
                if(ex instanceof ParamsException){
                    ParamsException p= (ParamsException) ex;
                    //设置异常信息
                    modelAndView.addObject("code",p.getCode());
                    modelAndView.addObject("msg",p.getMsg());
                }
                return modelAndView;
            }else {
                //返回的是数据对象
                //设置默认的异常处理
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("出现异常，请重试，，，");
                //判断异常类型是否是自定义异常
                if (ex  instanceof ParamsException){
                    ParamsException p= (ParamsException) ex;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }
                //设置相应类型以及编码格式响应JSON的数据）
                response.setContentType("application/json;charset=UTF-8");
                //得到字符输出流
                PrintWriter out=null;
                try {
                    //得到输出流
                    out=response.getWriter();
                    //将需要返回的对象转换成JSON格式的字符
                    String json= JSON.toJSONString(resultInfo);
                    out.write(json);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }
        return modelAndView;
    }
}
