<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>SQLCloud</title>
<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="${path}/resources/css/glyphicons.css">
<script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.serializeJSON/2.9.0/jquery.serializejson.min.js"></script>
<script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<script type="text/javascript">
$(function(){
	//保存JDBC连接
	$("#create-jdbc-form").on("submit",function(){
		var jdbcInfo = $(this).serializeJSON();
		$.post("${path}/jdbc/add",jdbcInfo,function(data, status, xhr){
			if(data.code == 200){
				newTab(jdbcInfo);
				$('#JDBCModalCenter').modal('hide')
			}else{
				alert(data.message);
			}
		},"json");
		return false;
	});
	
	//创建一个新的SQL操作窗口
	function newTab(jdbc){
		var tab = $("#"+jdbc.name+"-tab");
		if(tab.length == 1){
			tab.tab('show');
			return false;
		}
		//
		var $nav = $('<li class="nav-item"></li>');
		var $tab = $('<a class="nav-link active" id="'+jdbc.name+'-tab" data-toggle="tab" href="#'+jdbc.name+'" role="tab">'+jdbc.name+'</a>');
		var $close = $('<span class="glyphicon glyphicon-remove"></span>');
		var $panel = $('<div class="tab-pane fade show active" id="'+jdbc.name+'" role="tabpanel"></div>');
		var $iframe = $('<iframe id="main-iframe" class="main-iframe" src="${path}/views/container.jsp"></iframe>');
		//移除选中状态
		$("a[data-toggle=tab]").removeClass("active");
		$("div[role=tabpanel]").removeClass("show active");
		
		$("#jdbcTabContent").append($panel.append($iframe));
		$("#jdbcTabs").append($nav.append($tab.append($close)));
		holding(jdbc.name);
		//关闭Tab
		$close.on("click",function(event){
			$tab.tab('dispose');
			$nav.remove();
			$panel.remove();
			stopPropagation(event);
			//选中最后一个选项卡
			$("a[data-toggle=tab]").last().addClass('active');
			var activePanel = $("div[role=tabpanel]").last().addClass('show active');
			var jdbcName = activePanel.attr("id");
			if(typeof jdbcName !== 'undefined'){
				holding(jdbcName);
			}
		});
		$tab.on('show.bs.tab', function (e) {
			var tabId = $(this).attr("id");
			holding(tabId.split("-")[0]);
		});
		//指定当前使用的连接 
		function holding(jdbcName){
			$.post("${path}/jdbc/holding",{jdbcName:jdbcName},function(data, status, xhr){
				if(data.code != 200){
					alert(data.message);
				}
			},"json");
		}
	}
	//阻止冒泡
	function stopPropagation(event){
		if (event&&event.stopPropagation) {
	        event.stopPropagation();
	    }else{
	    	window.event.cancelBubble=true;
	    }
	}
});
</script>
<style type="text/css">
.nav-link{
	padding: 0.1rem 0.5rem;
}
.main-iframe{
	width: 100%;
    height: 100%;
    border: 0;
}
html,body{
	height: 99%;
}
main,.tab-content{
	height: 100%;
}
.tab-content>div{
	height: 100%;
}
</style>
</head>
<body style="padding-top: 75px;">
	<header class="fixed-top">
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark" style="padding: 0.1rem 0.5rem">
	  <a class="navbar-brand" href="#">SQLCloud</a>
	  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
	    <span class="navbar-toggler-icon"></span>
	  </button>
	  <div class="collapse navbar-collapse" id="navbarNavDropdown">
	    <ul class="navbar-nav">
	      <li class="nav-item active">
	        <a class="nav-link" href="#" data-toggle="modal" data-target="#JDBCModalCenter">新连接 <span class="sr-only">(current)</span></a>
	      </li>
	      <!-- <li class="nav-item dropdown">
	        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	          Dropdown link
	        </a>
	        <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
	          <a class="dropdown-item" href="#">Action</a>
	          <a class="dropdown-item" href="#">Another action</a>
	          <a class="dropdown-item" href="#">Something else here</a>
	        </div>
	      </li> -->
	    </ul>
	  </div>
    <button type="button" class="btn btn-dark" onclick="javascript:location.href='${path}/user/logout'">退出</button>
	</nav>
	<ul class="nav nav-tabs" id="jdbcTabs" role="tablist"></ul>
	</header>
	<main role="main">
		<div class="tab-content" id="jdbcTabContent"></div>
	</main>

	<!-- Create JDBC Modal -->
	<div class="modal fade" id="JDBCModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
	  <div class="modal-dialog modal-dialog-centered" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLongTitle">连接到我的SQL主机</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <form id="create-jdbc-form" action="" onsubmit="return false;">
	      <div class="modal-body">
	         <div class="form-group row">
			    <label for="colJdbcNameLabel" class="col-sm-2 col-form-label">类型</label>
			    <div class="col-sm-10">
			      <select class="custom-select" name="sqlType">
					  <option value="MYSQL" selected>MYSQL</option>
					  <option value="ORACLE">ORACLE</option>
					</select>
			    </div>
			  </div>
	          <div class="form-group row">
			    <label for="colJdbcNameLabel" class="col-sm-2 col-form-label">连接名</label>
			    <div class="col-sm-10">
			      <input type="text" class="form-control" id="colJdbcNameLabel" name="name" placeholder="JDBC Name" required="required" value="localhost">
			    </div>
			  </div>
			  <div class="form-group row">
			    <label for="colHostLabel" class="col-sm-2 col-form-label">主机</label>
			    <div class="col-sm-10">
			      <input type="text" class="form-control" id="colHostLabel" name="host" placeholder="Host" required="required" value="127.0.0.1">
			    </div>
			  </div>
			  <div class="form-group row">
			    <label for="colUsernameLabel" class="col-sm-2 col-form-label">用户名</label>
			    <div class="col-sm-10">
			      <input type="text" class="form-control" id="colUsernameLabel" name="username" placeholder="Username" required="required" value="root">
			    </div>
			  </div>
			  <div class="form-group row">
			    <label for="colPasswordLabel" class="col-sm-2 col-form-label">密码</label>
			    <div class="col-sm-10">
			      <input type="password" class="form-control" id="colPasswordLabel" name="password" placeholder="Password" required="required" value="root">
			    </div>
			  </div>
			  <div class="form-group row">
			    <label for="colPortLabel" class="col-sm-2 col-form-label">端口</label>
			    <div class="col-sm-10">
			      <input type="number" class="form-control" id="colPortLabel" name="port" placeholder="Port" required="required" value="3306" max="9999" min="1000">
			    </div>
			  </div>
			  <div class="form-group row">
			    <label for="colDatabaseLabel" class="col-sm-2 col-form-label">数据库</label>
			    <div class="col-sm-10">
			      <input type="text" class="form-control" id="colDatabaseLabel" name="database" placeholder="Database" required="required" value="sqlcloud">
			    </div>
			  </div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
	        <button type="submit" class="btn btn-primary">连接</button>
	      </div>
          </form>
	    </div>
	  </div>
	</div>
</body>
</html>