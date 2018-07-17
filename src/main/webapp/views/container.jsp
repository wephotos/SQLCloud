<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>SQLCloud</title>
<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="${path}/resources/css/glyphicons.css">
<link href="https://cdn.bootcss.com/zTree.v3/3.5.33/css/metroStyle/metroStyle.min.css" rel="stylesheet">

<script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/zTree.v3/3.5.33/js/jquery.ztree.all.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.serializeJSON/2.9.0/jquery.serializejson.min.js"></script>
<script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

<style type="text/css">
html,body{
	height: 100%;
}
.container-fluid,.main{
	height: 100%;
}
.col{
	padding: 0px;
}
.sql-toolbar{
	padding:0px;
	list-style: none;
	margin-bottom: 0px;
}
.sql-toolbar > li{
	display: inline-block;
}
.sql-toolbar > li label{
	padding:5px;
	margin: 0px;
}
.nav-link{
	padding: 0.1rem 0.5rem;
}
.table{
	width:auto;
	table-layout: fixed;
}
.table td,table th{
	text-align:left;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	max-width: 300px;
	text-align: center;
}
</style>

<script type="text/javascript">
$(function(){
	var tableTree;
	//ztree setting
	var setting = {
		view:{
			showLine:false
		},
		simpleData: {
			enable: true,
			idKey: "id",
			pIdKey: "pId",
			rootPId: -1
		},
		callback: {
			beforeExpand: function(treeId, table){//展开表格加载列
				if($.isArray(table.children)){
					return true;
				}
				$.post("${path}/sql/columns",{tableName:table.id},function(data, status, xhr){
					var columns = data.value.map(function(column){
						return {
							name:column.columnName + "("+column.columnType+")"
						};
					});
					tableTree.addNodes(table,columns);
				},"json");
			}
		}
	};
	//加载表格
	$.post("${path}/sql/tables",{},function(data, status, xhr){
		var zNodes = data.value.map(function(item){
			return {
				pId:-1,
				id:item.tableName,
				name:item.tableName,
				nodeType:'table',
				isParent:true
			};
		});
		tableTree = $.fn.zTree.init($("#tableTree"), setting, zNodes);
	},"json");
	
	//执行SQL
	$("#executeSQL").on("click",function(){
		var sql = $("textarea[name=sql]").val();
		if(!sql.replace(/^\s+/,'')){
			return false;
		}
		//转成大写，去除开头空格
		var sql = sql.toUpperCase().replace(/^\s+/,'');
		$.post('${path}/sql/execute',{sql:sql},function(data, status, xhr){
			if(data.code == 200){
				var results = data.value;
				if(results.length > 0){
					emptyConsoleTabs();
				}
				$.each(results,function(index, result){
					var console = newConsoleTab(index);
					if(result.type == 'query'){
						dynamicTable(result, console);
					}else if(result.type == 'update'){
						console.html("execute successfully. " + result.updateCount + " row updated");
					}else{
						//
					}
				});
			}else{
				$("#console").html(data.message);
			}
		},"json");
	});
	
	//根据查询结果动态输出表格
	function dynamicTable(mapQuery, console){
		var table = $("#tableModel").clone().attr("id",new Date().getTime());
		var columnRow = table.find("thead tr");
		$.each(mapQuery.columnNames,function(index,columnName){
			columnRow.append("<th title='"+columnName+"'>"+columnName+"</th>");
		});
		var tbody = table.find("tbody");
		$.each(mapQuery.results,function(i,item){
			var dataRow = $("<tr></tr>");
			$.each(mapQuery.columnNames,function(index,columnName){
				dataRow.append("<td title='"+item[columnName]+"'>"+item[columnName]+"</td>");
			});
			tbody.append(dataRow);
		});
		console.html(table);
	}
	
	//新建一个控制台选项卡 并返回 ,index不可重复
	function newConsoleTab(index){
		var tabId = "console-" + (index+1);
		var tab = $('<li class="nav-item"><a class="nav-link '+(index == 0 ? 'active' : '')+' id="'+tabId+'-tab" data-toggle="tab" href="#'+tabId+'" role="tab">'+tabId+'</a></li>');
		var tabContent = $('<div class="tab-pane fade '+(index == 0 ? 'show active' : '')+'" id="'+tabId+'" role="tabpanel"></div>');
		
		$("#consoleTabs").append(tab);
		$("#consoleTabContent").append(tabContent);
		return tabContent;
	}
	//清空控制台选项卡 
	function emptyConsoleTabs(){
		$("#consoleTabs").empty();
		$("#consoleTabContent").empty();
	}
	
	//为String原型提供 startWith 方法
	String.prototype.startsWith=function(str){
		if(!str){
			return false;
		}
		if(this.substr(0,str.length) == str){
			return true;
		}else{
			return false;
		}
	}
});
</script>
</head>
<body>
	<div class="container-fluid">
	  <div class="row main">
	    <div class="col-3 bg-light">
	    	<ul id="tableTree" class="ztree"></ul>
	    </div>
  		<div class="col-9" style="padding: 0px 5px;">
  			<div class="container-fluid">
  				<div class="row" style="height: 60%;">
  					<div class="col" style="box-sizing: border-box;padding-top: 36px;padding-bottom: 5px;">
  					  <nav class="bg-light" style="position: absolute;left: 0px;top: 0px;width: 100%;">
	  					  <ul class="sql-toolbar">
	  					  	<li><label id="executeSQL" class="btn bg-light text-primary"><span class="glyphicon glyphicon-play"></span> 运行</label></li>
	  					  </ul>
  					  </nav>
				      <textarea rows="" cols="" name="sql" style="width: 100%;height: 100%;resize: none;"></textarea>
				    </div>
  				</div>
  				<div class="row bg-white border-top border-secondary" style="height: 40%;">
  					<div class="col">
				      <ul class="nav nav-tabs" id="consoleTabs" role="tablist" style="position: absolute;left: 0px;top:0px;"></ul>
				      <div style="clear: both;"></div>
				      <div class="tab-content" id="consoleTabContent" style="overflow: auto;height: 100%;padding-top: 30px;"></div>
				    </div>
  				</div>
  			</div>
  		</div>
	  </div>
	</div>
	<div style="display: none;">
		<table id="tableModel" class="table table-bordered table-hover table-sm">
			<thead class="thead-light"><tr></tr></thead>
			<tbody></tbody>
		</table>
	</div>
</body>
</html>