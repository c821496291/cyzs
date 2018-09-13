<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%  
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
%>

<!DOCTYPE html> 
<html lang="en"> 
<head> 
    <meta charset="utf-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1"> 
    <title>工程辖工长管理</title> 

<link href="<%=path%>/pages/css/base.css" rel="stylesheet">
<link rel="stylesheet" href="<%=path%>/custom/uimaker/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=path%>/custom/uimaker/icon.css">
<link rel="stylesheet" href="<%=path%>/pages/css/providers.css">
<link rel="stylesheet" href="<%=path%>/js/validation/validate.css">
</head> 
<body>
    <div class="container" style="height: 100%;">
       <table id="dg" style="width:100%;height:99%" title="工程辖工长管理信息列表" data-options="
                rownumbers:true,
                singleSelect:false,
                autoRowHeight:false,
                pagination:true,
                fitColumns:true,
                striped:true,
                checkOnSelect:false,
                selectOnCheck:false,
                collapsible:true,
                toolbar:'#tb',
                loadMsg:0">
        </table>
               <div id="tb" style="padding:0 30px;">
       <form>
		工程名称: <select name="prId" id="prId" style="width:170px;height:35px;line-height:35px;">
					  <option value="">--------请选择--------</option>
					  <c:forEach items="${prName}" var="pr">
                        <option value="${pr.prId}">${pr.prName}</option>
                      </c:forEach>
			</select>
        工长姓名: <input id='gzName' name='gzName' class="easyui-textbox" type="text" style="width:166px;height:35px;line-height:35px;"></input>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="select()">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="reset()">重置</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="add()">添加</a>
		<input id="res" name="res" type="reset" style="display:none;"/>
		</form>
      </div>
    </div>
    <script type="text/javascript" src="<%=path%>/custom/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path%>/custom/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=path%>/custom/easyui-lang-zh_CN.js"></script>
	<script src="<%=path%>/js/validation/jquery.validate.js" type="text/javascript"></script>
	<script src="<%=path%>/js/validation/jquery.metadata.js" type="text/javascript"></script>
	<script src="<%=path%>/js/validation/custom.validate.js" type="text/javascript"></script>

    
  

    
<script type="text/javascript"> 
  
getData();
function select(){
  $("#dg").datagrid("load",{                          
	 prId: $("#prId").val(),
	 gzName: $("#gzName").val(),
 });
}

function reset(){
  $("input[name='res']").click();
}
 function getData(){
 $("#dg").datagrid({  
         url: "selectProjectIndexSList1",
         queryParams://每次请求的参数
             {
        	 prId: $("#prId").val(),
        	 gzName: $("#gzName").val(),
             },
         pagination: true,//表示在datagrid设置分页              
         rownumbers: false,  
         singleSelect: true,  
         pageSize: 15,
         pageList: [15, 20, 30,],
         columns: [[  
               { field: 'prName', title: '工程名称', width: '35%', align: 'center' },  
               { field: 'uName', title: '工长姓名', width: '30%', align: 'center' },  
               {  
                field: 'prSId',  
                title: '操作',  
                width: '34%',  
                align: 'center',  
                formatter: function(value, row, index) {   

                var a = "<a href='javascript:void(0)' onclick='edit(\""+value+"\")'>修改</a>"
                var b = "<a href='javascript:void(0)' onclick='del(\""+value+"\")'>删除</a>"   
                   return a +"     ||     "+ b; 

                 }  
               }           
           ]],  
              
       });

 }

 

  function reloadtb(){
   $("#dg").datagrid("reload",{                          
         prId: $("#prId").val(),
         gzName: $("#gzName").val(),
    });
 } 

 function edit(prSId){
	   var _url = "${pageContext.request.contextPath }/projectIndexS/edit/" + prSId;
	   $('<div></div>').dialog({
	                    id : 'newDialog',
	                    title : '修改工程辖工长信息',
	                    width : 602,
	                    height : 480,
	                    closed : false,
	                    cache : false,
	                    href : _url,
	                    modal : true,
	                    collapsible:false, //设置可折叠  
	                    minimizable:false,  
	                    maximizable:false,  
	                    resizable:false, 
onLoad : function() {
                    
                    $("#editPrIndexS").validate({
                           onsubmit:true,// 是否在提交是验证   
                           onfocusout:false,// 是否在获取焦点时验证   
                           onkeyup :false,// 是否在敲击键盘时验证   
                           rules: { 
                            uId:{required: true,
                            	remote:{     //验证工程、工长对应关系是否存在
                          			type:"POST",
                          			url:"editTest",   
                          			data:{ prId:function(){return $("#prName2").val();},
                                			uId:function(){return $("#gzName2").val();}
                              		} 
                            	}
                            },
                            prId:{required: true},
                          },
                          messages: {
                          		uId:{remote:"该工程、工长对应关系已存在！"}
                          },   
                         submitHandler: function(form) {  //通过之后回调   
                         var postdata = $("#editPrIndexS").serialize();
                         $.ajax({  
                           type:"POST",  
                           url:"editPrIndexS1",
                           data:postdata,
                           dataType:"text", 
                           async: false,
                           success:function(data){
                           if(data==1){
                            $('#newDialog').dialog('close');
                            reloadtb();
                           }else{alert("修改失败："+data);}	
                           },  
                           error:function(e) {  
                           alert("修改失败："+e);  
                           }  
                         });
                        
                      },  
                    invalidHandler: function(form, validator) {  //不通过回调   
                    return false;   
                    }   
                   });    //初始化表单数据
                    },
                    onClose : function() {
                        $(this).dialog('destroy');
                    },
                    buttons:[{  
                        text:'提交',
                        iconCls: 'icon-ok',
                        handler:function(){
                        $("#submit2").click(); 
                        }  
                      },
                      {  
                        text:'重置',
                        iconCls: 'icon-reload',
                        handler:function(){
                         $("#res2").click();  
                        }  
                      },
                      {  
                        text:'取消',
                        iconCls: 'icon-cancel',
                        handler:function(){
                         $('#newDialog').dialog('close'); 
                        }  
                      }
                      ]                               
	                });
}
 function del(prSId){
 var flag = confirm('是否确认删除？');
   if (flag) {
	$.ajax({  
            contentType: 'application/json',
            type:"POST",  
            url:"delete1",
            data:prSId,
            dataType:"text", 
            async: false,
            success:function(data){
            if(data==1){
              reloadtb();
            }else{alert("删除失败："+e);}	
            },  
            error:function(e) {  
              alert("删除失败："+e);  
            }  
        });
	} else {
		return;
	}
 }	                   
 function add(){
	   var _url = "${pageContext.request.contextPath }/projectIndexS/add";
	   $('<div></div>').dialog({
	                    id : 'newDialog',

	                    title : '添加工程辖工长信息',
	                    width : 602,
	                    height : 480,
	                    closed : false,
	                    cache : false,
	                    href : _url,
	                    modal : true,
	                    collapsible:false, //设置可折叠  
	                    minimizable:false,  
	                    maximizable:false,  
	                    resizable:false, 
	                    onLoad : function() { 
                        $("#addPrIndexS").validate({
                           onsubmit:true,// 是否在提交是验证   
                           onfocusout:false,// 是否在获取焦点时验证   
                           onkeyup :false,// 是否在敲击键盘时验证   
                           rules: { 
                            uId:{required: true,
                            	remote:{     //验证工长、工程对应关系是否重复
                          			type:"POST",
                          			url:"addTest",   
                          			data:{ prId:function(){return $("#prName1").val();},
                                			uId:function(){return $("#gzName1").val();}
                              		} 
                            	}
                            }, 
                            prId:{required: true}, 
                          }, 
                          messages: {
                          	uId:{remote:"该工程、工长对应已存在！"}
                          },

                         submitHandler: function(form) {  //通过之后回调   
                         var postdata = $("#addPrIndexS").serialize();   
                         $.ajax({   
                          type:"POST",  
                          url:"addPrIndexS1",
                          data:postdata,
                          dataType:"json", 
                          async: false,
                          success:function(data){
                          if(data==1){
                            reloadtb();
                           alert("成功");
                           $('#newDialog').dialog('close');
                         }else{alert("添加失败："+data);}	
                        },  
                          error:function(e) {  
                        alert("添加失败："+e);  
                        }    
                        });
                      },  
                    invalidHandler: function(form, validator) {  //不通过回调   
                    return false;   
                    }   
                   });     
	              },
	                 onClose : function() {
	                        $(this).dialog('destroy');
	                  },
	                    buttons:[{  
                        text:'提交',
                        iconCls: 'icon-ok',
                        handler:function(){
                        $("#submit1").click();
                        }  
                      },
                      {  
                        text:'重置',
                        iconCls: 'icon-reload',
                        handler:function(){
                         $("#res1").click();  
                        }  
                      }]         
	                });
	 }         
 </script>  
</body> 
</html>