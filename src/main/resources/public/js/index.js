layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    /**
     * 表单submit提交  form.on('submit(按钮的lay-filter的值)', function(data){}
     */
    form.on('submit(login)', function(data){
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
        //表单的非空校验，在在在前台的LayUI中以及做过校验了
        /* 发送Ajax请求 传递用户姓名和密码 执行用户登录操作*/
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                //这个username password要和表单的文本框和密码框的name值一样
               userName:data.field.username,
               userPwd:data.field.password
            },
            success:function (result){
               //result是回调函数，接收冲后台controller返回的ResultInfo对象
                console.log(result);
                //判断是否登录成功 如果code=200  则表示成功 否则表示失败
                if (result.code==200){
                    //登录成功,把用户信息存在cookie中
                    /**
                     * session  服务器关闭失效
                     * 所以利用cookie 保存用户信息，cookie未失效 则用户是登录状态
                     */
                    layer.msg("登录成功！",function (){

                        if ($("#rememberMe").prop("checked")){
                            // 如果⽤户选择"记住我"，则设置cookie的有效期为7天
                            $.cookie("userIdStr", result.result.userIdStr, { expires: 7 });
                            $.cookie("userName", result.result.userName, { expires: 7 });
                            $.cookie("trueName", result.result.trueName, { expires: 7 });
                        }else {
                            //将用户信息设置到cookie中
                            $.cookie("userIdStr",result.result.userIdStr);
                            $.cookie("userName",result.result.userName);
                            $.cookie("trueName",result.result.trueName);
                        }

                        //登录成功后 跳转到首页
                        window.location.href=ctx+"/main";
                        });
                }else {
                    //登陆失败
                    layer.msg(result.msg,{icon:5})
                }
            }
        });
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
});