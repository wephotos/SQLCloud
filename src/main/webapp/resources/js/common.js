/**
 * JQuery Global Setting
 * @returns
 */
$(function(){
	var pathname = document.location.pathname;
	var index = pathname.substr(1).indexOf("/");
	var path = index === -1 ? pathname : pathname.substr(0, index + 1);
	window.path = (path === '/views' ? "/" : path);
	$.ajaxSetup({
	    complete: function(xhr, status) {
	        var sessionStatus = xhr.getResponseHeader('session-status');
	        if(sessionStatus == 'timeout') {
	            window.location.href = path;
	        }
	    }
	});
});
//为String添加endsWith 方法
String.prototype.endsWith = function(text){
	if(!text){
		return false;
	}
	if(text.length > this.length){
		return false;
	}
	return this.substr(this.length - text.length) === text;
}