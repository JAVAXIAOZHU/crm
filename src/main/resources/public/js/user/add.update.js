layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
        // 引⼊ formSelects 模块
        var formSelects = layui.formSelects;

    /**
     * 加载下拉框数据
     */
    var userId=$("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?userId="+userId,
        //⾃定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //⾃定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);


    /**
     * 添加或更新⽤户
     * 绑定的是添加或更新用户的确定按钮
     */
    /**
     * 添加或更新⽤户
     */
    form.on("submit(addOrUpdateUser)", function (data) {
        // 弹出loading层
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        var url = ctx + "/user/save";
        console.log($("input[name='id']").val());
        if($("input[name='id']").val()){
            url = ctx + "/user/update";
        }
        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    // 关闭弹出层（返回值为index的弹出层）
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    // 关闭所有ifream层
                    layer.closeAll("iframe");
                    // 刷新⽗⻚⾯
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        });
        return false;
    });

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执⾏关闭
    });

});