# 将mantis轻量级bug管理工具中的bug同步到禅道管理工具中
##实现原理：  
1.首选将mantis中的bug以.xml格式的文件导出  
2.使用JDOM解析.xml文件，并与禅道中的bug表单格式进行匹配，生成符合格式要求新的bug表单  
3.运用fiddler抓包工具，抓取禅道系统登录请求和bug提交请求  
4.将步骤2中新的bug表单，以post请求方式发送给服务器，自动提交到禅道中  
