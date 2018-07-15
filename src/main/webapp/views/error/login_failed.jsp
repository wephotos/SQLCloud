<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>SQLCloud Login Failed</title>
	<script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    
    <style type="text/css">
    	html,body{
    		height:100%;
    		background-repeat: no-repeat;
    		background-size:100% 100%;
    		-moz-background-size:100% 100%;
    		background-image: url("${path}/resources/img/login_bg.jpg");
    	}
    	.row{background: rgba(255,255,255,0.2);width:400px;margin:300px auto;border-radius: 2px;}
    </style>
</head>
<body>
	<div class="container">
	    <div class="row">
	        <div class="form-horizontal col-md-offset-2">
	            <h3 class="form-title">SQLCloud Login Failed</h3>
	            <div class="col-md-10">
		            <div class="form-group col-md-offset-9">
	                    <button class="btn btn-success pull-right" onclick="javascript:history.back();">重试</button>
	                </div>
                </div>
	        </div>
	    </div>
    </div>
</body>
</html>