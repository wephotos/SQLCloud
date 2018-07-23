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
<script type="text/javascript" src="${path}/resources/js/common.js"></script>
<script src="https://cdn.bootcss.com/zTree.v3/3.5.33/js/jquery.ztree.all.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.serializeJSON/2.9.0/jquery.serializejson.min.js"></script>
<script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<!-- load ace -->
<script src="https://cdn.bootcss.com/ace/1.3.3/ace.js"></script>
<script src="https://cdn.bootcss.com/ace/1.3.3/ext-language_tools.js"></script>
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
.ztree li {
	margin: 2px 1px;
}
</style>

<script type="text/javascript">
$(function(){
	//ztree setting
	var setting = {
		async:{
			enable: true,
			url: "${path}/meta/topTreeNodes",
			dataFilter:function(treeId, parentNode, data) {
				if(data.code == 200){
					return data.value.map(function(node){
						var icon = "";
						if(node.type == 'catalog' || node.type == 'schema'){
							icon = "${path}/resources/img/database_21px.png";
						}
						return {
							id : -1,
							icon:icon,
							isParent : true,
							type: node.type,
							name : node.name,
							comment:"click use database"
						};
					});
				} else {
					alert(data.message);
					return [];
				}
			}
		},
		view : {
			showLine : false,
			selectedMulti:false,
			fontCss:function(treeId, treeNode){
				var nodes = objectTree.getSelectedNodes();
				for(var i=0;i<nodes.length;i++){
					if(nodes[i] == treeNode){
						return {"font-weight":"bold","color":"blue"};
					}
				}
				return {"font-weight":"normal","color":"inherit"};
			}
		},
		data : {
			simpleData : {
				enable : true,
				idKey : "id",
				pIdKey : "pId",
				rootPId : -1
			},
			key : {
				title : "comment"
			}
		},
		callback : {
			beforeAsync : function(treeId, treeNode) {
				return treeNode == null;
			},
			beforeExpand : function(treeId, node) {//展开表格加载列
				if ($.isArray(node.children)) {
					return true;
				}
				if (node.type == 'catalog' || node.type == 'schema') {
					$.post("${path}/meta/tables", {
						database : node.name
					}, function(data, status, xhr) {
						if (data.code != 200) {
							return false;
						}
						var tables = data.value.map(function(table) {
							return {
								isParent : true,
								type: table.type,
								name : table.name,
								comment : table.remarks,
								database:table.tableCat?table.tableCat:table.tableSchem,
							};
						});
						objectTree.addNodes(node, tables);
					}, "json");
				} else if (node.type == 'table') {
					$.post("${path}/meta/columns", {
						database : node.database,
						tableName : node.name
					}, function(data, status, xhr) {
						if (data.code != 200) {
							return false;
						}
						var columns = data.value.map(function(column) {
							var nullable = "";
							if (column.nullable === 1) {
								nullable = ",Nullable";
							}
							var icon_pk = "";
							if (column.primaryKey === true) {
								icon_pk = "${path}/resources/img/key.png";
							}
							return {
								type:column.type,
								value:column.name,
								icon:icon_pk,
								comment : column.remarks,
								name : (column.name + "," + column.typeName + ("("+column.columnSize+")") + nullable)
							};
						});
						objectTree.addNodes(node, columns);
					}, "json");
				} else {
					return true;
				}
			},
			onClick : function(event, treeId, treeNode) {
				//切换数据源 
				if(treeNode.type == 'catalog' || treeNode.type == 'schema'){
					$.post("${path}/jdbc/useDatabase",{database:treeNode.name},function(data, status, xhr){
						if(data.code != 200){
							alert("切换数据库失败:" + data.message);
						}else{
							aceAddCompleterTables(treeNode.name);
							/**设置当前节点为选中样式**/
							objectTree = $.fn.zTree.getZTreeObj("objectTree");
							var nodes = objectTree.getNodesByFilter(function(node){return node.level == 0;});
							for(var i=0;i<nodes.length;i++){
								objectTree.updateNode(nodes[i]);
							}
						}
					},"json");
					return false;
				}
				//单击插入编辑器光标处
				var cursor = editor.selection.getCursor();
				var upText = editor.session.getTextRange({
					start : {
						row : cursor.row,
						column : 0
					},
					end : cursor
				}).toUpperCase().trim();
				var insertText = (typeof treeNode.value === 'undefined' ? treeNode.name : treeNode.value);
				if (upText) {
					var digit = upText.charAt(upText.length - 1);
					if (digit !== ',' && !upText.endsWith("SELECT")
							&& !upText.endsWith("FROM")) {
						insertText = ", " + insertText;
					}
				}
				editor.insert(insertText);
			},
			onRightClick : function(event, treeId, treeNode) {//实现右键菜单
				if(!treeNode){
					return false;
				}
				if (treeNode.type != 'table') {
					return false;
				}
				var trm = $("#table-right-menu");
				trm.css({
					"left" : event.clientX + "px",
					"top" : event.clientY + "px"
				}).slideDown("fast");
				//打开表 
				$("#open-table", trm).unbind("click").on("click",function() {
					executeSQL("SELECT * FROM " + treeNode.name);
				});
				//改变表
				$("#alter-table", trm).unbind("click").on("click",function() {
					alert("开发中...");
				});
				//点击菜单以外区域隐藏 
				$(document.body).bind("mousedown",function mousedown(event) {
					if (event.target.id !== 'table-right-menu') {
						trm.fadeOut("fast");
						$("body").unbind("mousedown", mousedown);
					}
				});
			}
		}
	};
	//加载对象树 
	var objectTree = $.fn.zTree.init($("#objectTree"), setting, []);

	//执行SQL
	$("#executeSQL").on("click", function() {
		var range = editor.getSelectionRange();
		var sql = editor.session.getTextRange(range);
		//选中 > 光标 > 内容 
		if (!sql.replace(/^\s+/, '')) {
			sql = editor.getTextCursorRange();
			if (!sql.replace(/^\s+/, '')) {
				sql = editor.getValue();
			}
		}
		if (!sql.replace(/^\s+\n+\r+/, '')) {
			return false;
		}
		executeSQL(sql);
	});
	//执行SQL语句
	function executeSQL(sql) {
		var that = this;
		$(this).addClass("disabled").css("pointer-events", "none");
		sql = sql.toUpperCase().replace(/^\s+/, '');//转成大写，去除开头空格
		$.post('${path}/sql/execute', {
			sql : sql
		}, function(data, status, xhr) {
			emptyConsoleTabs();
			if (data.code == 200) {
				var results = data.value;
				$.each(results, function(index, result) {
					var console = newConsoleTab(index);
					if (result.type == 'query') {
						dynamicTable(result, console);
					} else if (result.type == 'update') {
						console.html("execute successfully. " + result.updateCount + " row updated");
					} else {
						//
					}
				});
			} else {
				newConsoleTab(0).html(data.message);
			}
			$(that).removeClass("disabled").css("pointer-events", "auto");
		}, "json");
	}

	//根据查询结果动态输出表格
	function dynamicTable(mapQuery, $console) {
		var table = $("#tableModel").clone().attr("id", new Date().getTime());
		var columnRow = table.find("thead tr");
		columnRow.append("<th>序号</th>");
		$.each(mapQuery.columnNames, function(index, columnName) {
			var aliasName = columnName.split('.')[2];
			columnRow.append("<th title='"+aliasName+"'>" + aliasName + "</th>");
		});
		var tbody = table.find("tbody");
		var dataRowsHtml = [];//表格行HTML数组 
		$.each(mapQuery.results, function(i, item) {
			dataRowsHtml.push("<tr>");
			dataRowsHtml.push("<td>" + (i + 1) + "</td>");
			$.each(mapQuery.columnNames,function(index, columnName) {
				var content = item[columnName];
				if (content === true) {
					content = "TRUE";
				} else if (content === false) {
					content = "FALSE";
				} else if (content === null) {
					content = "(Null)";
				} else {
					content = content.toString();
				}
				content = content.replace(/</g,'&lt;').replace(/>/g,'&gt;');
				var popover = "";
				if (content != '(Null)' && content) {
					popover = " data-toggle='popover' data-placement='top' data-content='"+ content + "' ";
				}
				dataRowsHtml.push("<td" + popover + ">" + content + "</td>");
			});
			dataRowsHtml.push("</tr>");
		});
		tbody.html(dataRowsHtml.join(""));
		//bg-secondary text-white
		var statusbar = $('<ul class="nav nav-tabs bg-light" style="position: absolute;left: 0px;bottom:0px;width:100%;"></ul>');
		statusbar.append('<li class="nav-item" style="font-size:12px;">总条数 ' + mapQuery.total + '行 已提取  ' + mapQuery.results.length + ' 行</li>');
		var tableContent = $("<div style='height:100%;overflow: auto;'></div>");
		$console.html(tableContent.append(table));
		$console.append(statusbar);
		$console.css({
			"height" : "100%",
			"padding-bottom" : "20px",
			"box-sizing" : "border-box"
		});
		//Enable popovers everywhere
		$('td[data-toggle="popover"]').popover();
		$("div.popover").css("pointer-events", "none");
	}

	//新建一个控制台选项卡 并返回 ,index不可重复
	function newConsoleTab(index) {
		var tabId = "console-" + (index + 1);
		var tab = $('<li class="nav-item"><a class="nav-link ' + (index == 0 ? 'active' : '') 
				+ ' id="' + tabId + '-tab" data-toggle="tab" href="#' + tabId + '" role="tab">' + tabId + '</a></li>');
		var tabContent = $('<div class="tab-pane fade ' + (index == 0 ? 'show active' : '') + 
				'" id="' + tabId + '" role="tabpanel"></div>');

		$("#consoleTabs").append(tab);
		$("#consoleTabContent").append(tabContent);
		return tabContent;
	}
	//清空控制台选项卡 
	function emptyConsoleTabs() {
		$("#consoleTabs").empty();
		$("#consoleTabContent").empty();
		$('td[data-toggle="popover"]').popover('dispose');
	}

	//点击其它区域 隐藏 popover
	$(document).on("click", function(event) {
		if (!$(event.target).hasClass("popover-body")) {
			$('td[data-toggle="popover"]').popover('hide');
			if ($(event.target).attr("data-toggle") == 'popover') {
				$(event.target).popover('toggle');
			}
		}
	});
});
</script>
</head>
<body>
	<div class="container-fluid">
	  <div class="row main">
	    <div class="col-3 bg-light" style="overflow: auto;">
	    	<ul id="objectTree" class="ztree"></ul>
	    </div>
  		<div class="col-9" style="padding: 0px 5px;">
  			<div class="container-fluid">
  				<div class="row" style="height: 55%;">
  					<div class="col" style="box-sizing: border-box;padding-top: 36px;padding-bottom: 5px;">
  					  <nav class="bg-light" style="position: absolute;left: 0px;top: 0px;width: 100%;">
	  					  <ul class="sql-toolbar">
	  					  	<li>
	  					  		<label id="executeSQL" class="btn bg-light text-primary">
	  					  			<span class="glyphicon glyphicon-play"></span> 
	  					  			<span style="vertical-align: middle;">运行</span>
	  					  		</label>
	  					  	</li>
	  					  </ul>
  					  </nav>
				      <%--textarea rows="" cols="" name="sql" style="width: 100%;height: 100%;resize: none;"></textarea>--%>
						<pre id="editor" class="ace_editor" style="height: 100%;width: 100%;"></pre>
				    </div>
  				</div>
  				<div class="row bg-white border-top border-secondary" style="height: 45%;">
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
			<thead class="thead-light position-static"><tr></tr></thead>
			<tbody></tbody>
		</table>
	</div>
	<!-- 表格右键菜单 -->
	<div id="table-right-menu" class="bg-dark" style="position: absolute;display: none;">
		<div class="btn-group-vertical btn-group-sm" role="group" style="padding: 10px;">
			<button id="open-table" type="button" class="btn btn-dark">打 开 表</button>
  			<button id="alter-table" type="button" class="btn btn-dark">改 变 表</button>
		</div>
	</div>
	<script type="text/javascript">
        // trigger extension
        var langTools = ace.require("ace/ext/language_tools");
        var editor = ace.edit("editor");
        editor.session.setMode("ace/mode/mysql");
        editor.setTheme("ace/theme/github");
        editor.setFontSize(18);
        // enable autocompletion and snippets
        editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true
        });
        //获取光标处SQL语句块
        editor.getTextCursorRange = function(){
        	//行数 
        	var lines = this.session.getLength();
        	//光标行 
        	var line = this.selection.getCursor().row;
        	//结束位置 
        	var end = {
        		row:lines,
        		column:0
        	};
        	for(var i=line;i<lines;i++){
				var lineRange = {
					start : {
						row : i,
						column : 0
					},
					end : {
						row : i + 1,
						column : 0
					}
				};
				var lineText = this.session.getTextRange(lineRange);
				/* if (!lineText.replace(/^\s+/, '')) {
					return "";
				} */
				if (lineText.indexOf(';') > -1) {
					end = lineRange.end;
					break;
				}
			}
			var start = {
				row : 0,
				column : 0
			};
			for (var i = line; i > 0; i--) {
				var lineRange = {
					start : {
						row : i - 1,
						column : 0
					},
					end : {
						row : i,
						column : 0
					}
				};
				var lineText = this.session.getTextRange(lineRange);
				if (lineText.indexOf(';') > -1) {
					start = lineRange.end;
					break;
				}
			}
			return this.session.getTextRange({
				start : start,
				end : end
			});
		};
		//添加用于自动补全的表
		var ace_tables = [];
		function aceAddCompleterTables(database){
			$.post('${path}/meta/tables',{database:database},function(data, status, xhr){
	            if(data.code == 200){
	            	ace_tables = data.value;
	                if(window.isAceAddCompleterTables){
	    				return false;
	    			}
	                langTools.addCompleter({
	                    getCompletions: function(editor, session, pos, prefix, callback) {
	                        if (prefix.length === 0) {
	                        	callback(null, []);
	                        }else{
	                            callback(null,ace_tables.map(function(table){
	                            	return {name: table.tableName, value: table.tableName, score: 10000, meta: "table"};
	                            }));
	                        }
	                    }
	                });
	    			window.isAceAddCompleterTables = true;
	            }
	        },"json");
		}
		aceAddCompleterTables("");
	</script>
</body>
</html>