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
			beforeExpand: function(treeId, treeNode){
				
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
});
</script>
</head>
<body>
	<div class="container-fluid">
	  <div class="row main">
	    <div class="col-3 bg-light">
	    	<ul id="tableTree" class="ztree"></ul>
	    </div>
  		<div class="col-9" style="padding: 5px;">
  			<div class="container-fluid">
  				<div class="row" style="height: 60%;">
  					<div class="col">
				      SQLEditor
				    </div>
  				</div>
  				<div class="row bg-light" style="height: 40%;">
  					<div class="col">
				      Console
				    </div>
  				</div>
  			</div>
  		</div>
	  </div>
	</div>
</body>
</html>