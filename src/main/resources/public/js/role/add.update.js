layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 绑定确定按钮
     */
    form.on("submit(addOrUpdateRole)", function (data) {
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});

        //弹出loading
        var url=ctx + "/role/add";
        if($("input[name='id']").val()){
            url=ctx + "/role/update";
        }
        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    layer.closeAll("iframe");
                    //刷新⽗⻚⾯
                    parent.location.reload();
                }, 500);
            }
            else {
                    layer.msg(
                        res.msg, {
                            icon: 5
                        }
                    );
                }
            });
            return false;
        });

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 再执⾏关闭
        parent.layer.close(index);
    });
});