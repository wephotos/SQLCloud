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
	padding:8px;
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
			beforeExpand: function(treeId, table){
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
		var url;
		var sql = sql.toUpperCase().replace(/^\s+/,'');//转成大写，去除开头空格
		if(sql.startWith('SELECT')){
			url = '${path}/sql/executeQuery';
		}else if(sql.startWith('UPDATE')){
			url = '${path}/sql/executeUpdate';
		}else{
			$("#console").html(sql + ' is not supported');
			return false;
		}
		$.post(url,{sql:sql},function(data, status, xhr){
			if(data.code == 200){
				if(sql.startWith('SELECT')){
					dynamicTable(data.value);
				}else{
					$("#console").html("execute successfully. " + data.value + " row updated");
				}
			}else{
				$("#console").html(data.message);
			}
		},"json");
	});
	
	//根据查询结果动态输出表格
	function dynamicTable(mapQuery){
		var table = $("#tableModel").clone().attr("id",new Date().getTime());
		var columnRow = table.find("thead tr");
		$.each(mapQuery.columnNames,function(index,columnName){
			columnRow.append("<td>"+columnName+"</td>");
		});
		var tbody = table.find("tbody");
		$.each(mapQuery.results,function(i,item){
			var dataRow = $("<tr></tr>");
			$.each(mapQuery.columnNames,function(index,columnName){
				dataRow.append("<td>"+item[columnName]+"</td>");
			});
			tbody.append(dataRow);
		});
		$("#console").html(table);
	}
	
	//为String原型提供 startWith 方法
	String.prototype.startWith=function(str){
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
  					<div class="col" style="box-sizing: border-box;padding-top: 50px;padding-bottom: 5px;">
  					  <nav class="bg-light" style="position: absolute;left: 0px;top: 0px;width: 100%;">
	  					  <ul class="sql-toolbar">
	  					  	<li><label id="executeSQL" class="btn bg-light text-primary"><span class="glyphicon glyphicon-play"></span> 运行</label></li>
	  					  </ul>
  					  </nav>
				      <textarea rows="" cols="" name="sql" style="width: 100%;height: 100%;resize: none;"></textarea>
				    </div>
  				</div>
  				<div class="row bg-white border border-secondary" style="height: 40%;padding: 5px;">
  					<div id="console" class="col">
				      
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