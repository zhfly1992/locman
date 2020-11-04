$(function() {
    /*
     * 4. 输入框失去焦点进行校验
     */
    $(".inputClass").blur(
            function() {
                var id = $(this).attr("id");// 获取当前输入框的id
                var funName = "validate" + id.substring(0, 1).toUpperCase() + id.substring(1) + "()";// 得到对应的校验函数名
                console.log(funName);
                eval(funName);// 执行函数调用
            });

    /*
     * 5. 表单提交时进行校验
     */
    $("#loginForm").submit(function() {
        var bool = true;// 表示校验通过
        if (!validatePassword_()) {
            bool = false;
        }
        if (!validateAccount()) {
            bool = false;
        }

        return bool;
    });
});


/*
 * 用户名校验方法
 */
function validateAccount() {
    var id = "account";
    var value = $("#" + id).val();// 获取输入框内容

    /*
     * 1. 非空校验
     */

    if (!value) {
       /* $.post("register!registerCheck?username=" + value, function(d) {*/
            $("#account_msg").html("<font size='2' color='red'>账号不能为空</font>");
        /*});*/
        return false;
    }
    /*
     * 2. 验证用户名使用情况
     */
    else {
        $.get("/interGateway/v3/user/emailMobExist?emailMob=" + value, function(d) {
            if (d == 1) {
                $("#account_msg").html("");
            } else {
                $("#account_msg").html("<font size='2' color='red'>用户不存在，请重新输入</font>");
            }
        });
    }
    return true;
}

/*
 * 登录密码校验方法
 */
function validatePassword_() {
    var id = "password_";
    var value = $("#" + id).val();// 获取输入框内容
    /*
     * 1. 非空校验
     */
    if (!value) {
        $("#password_msg").html("<font size='2' color='red'>密码不能为空</font>");
        return false;
    }
    else if (!validatePassword_()) {
   	 $("#account_msg").html("");
   }
    
    /* else if (value.length < 3 || value.length > 20) {
        
         * 2. 长度校验
         
        $("#password_msg").html("<font color='red'>密码长度必须在3 ~ 20之间</font>");
        return false;
    } else {
        $("#password_msg").html("<font color='green'>√</font>");
    }*/

    return true;
}

/*
 * 确认密码校验方法
 */
/*function validateRepassword() {
    var id = "repassword";
    var value = $("#" + id).val();// 获取输入框内容
    
     * 1. 非空校验
     
    if (!value) {
        var pwd = $("#password").val();// 获取输入框内容
        if(pwd.length==0){
            $("#repassword_msg").html("<font color='red'>密码不能为空</font>");
            return false;
        }
    }
    
     * 2. 两次输入是否一致校验
     
    else if (value != $("#password").val()) {
        $("#repassword_msg").html("<font color='red'>密码不一致</font>");
        return false;
    } else {
        $("#repassword_msg").html("<font color='green'>√</font>");
    }
    return true;
}*/