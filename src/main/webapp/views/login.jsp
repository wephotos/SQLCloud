<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<title>SQLCloud Sign In</title>
	<script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    
    <style type="text/css">
    	.form-control{
    		padding-left: 26px;
    	}
    	.form{
	    	background: rgba(255,255,255,0.2);
	    	width:400px;
	    	border-radius: 2px;
	    	position: fixed;
	    	top: 50%;
	    	left: 50%;
	    	margin-top: -100px;
	    	margin-left: -200px;
    	}
    	.fa{
	    	display: inline-block;
	    	top: 27px;
	    	left: 6px;
	    	position: relative;
	    	color: #ccc;
    	}
    	html,body{
    		height:100%;
    		background-repeat: no-repeat;
    		background-size:100% 100%;
    		-moz-background-size:100% 100%;
    		background-image: url("${path}/resources/img/login_bg.jpg");
    	}
    </style>
    <script type="text/javascript">
    	if(window.top != window){
    		window.top.location.href = "${path}/views/login.jsp";
    	}
    </script>
</head>
<body>
	<div class="container">
		<form action="${path}/user/login" method="post">
	    <div class="form row">
	        <div class="form-horizontal col-md-offset-2" id="login_form">
	            <h3 class="form-title">SQLCloud Login</h3>
	            <div class="col-md-10">
	                <div class="form-group">
	                    <i class="fa fa-user fa-lg"></i>
	                    <input class="form-control" required="required" type="text" autocomplete="off"
	                    	placeholder="Username" name="username" autofocus="autofocus" />
	                </div>
	                <div class="form-group">
	                        <i class="fa fa-lock fa-lg"></i>
	                        <input class="form-control" required="required" type="password" placeholder="Password" name="password"/>
	                </div>
	                <div class="form-group col-md-offset-9">
	                    <button type="submit" class="btn btn-success pull-right" name="submit">Sign In</button>
	                </div>
	            </div>
	        </div>
	    </div>
	    </form>
    </div>
</body>
</html>