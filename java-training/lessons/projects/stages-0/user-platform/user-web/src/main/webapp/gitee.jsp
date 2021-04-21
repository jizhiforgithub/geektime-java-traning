<head>
    <%--<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />--%>

    <title>index</title>
    <script src="./static/js/jquery-2.2.3.min.js"></script>

</head>
<body>
<div class="container-lg">
    <p>hello word</p>
    <div>
        <a id="giteeinfo" href="https://gitee.com/api/v5/user?access_token=">gitee 用户信息</a>
    </div>
</div>
<script>
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]);
        return null; //返回参数值
    }


    $(document).ready(function () {
        var code = getUrlParam('code');
        var addr = "https://gitee.com/oauth/token?grant_type=authorization_code&code=" + code + "&client_id=63ebd19c2e5b5d1e771d93c4cd8e9403e3da8c8f540a517726104fe836d982b7&redirect_uri=http://localhost:8080/user-web/gitee.jsp&client_secret=55e56b2ad06420637a48b9f846819b498ecbd8ed022c9a19c50c7f2d48304ea1";

        $.ajax({
            type: "POST",
            url: addr,
            success: function (data) {
                access_token = data.access_token;
                refresh_token = data.refresh_token;
                $("#giteeinfo").attr("href", $("#giteeinfo").attr("href") + access_token);
            }
        });


    });

</script>

</body>