/**
 * JQuery Global Setting
 * @returns
 */
$(function(){
	$.ajaxSetup({
	    complete: function(xhr, status) {
	        var sessionStatus = xhr.getResponseHeader('session-status');
	        if(sessionStatus == 'timeout') {
	            window.location.href = "/";
	        }
	    }
	});
});