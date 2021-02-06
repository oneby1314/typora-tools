# typora-tools

一款用 Java 编写的 Typora 小工具

# 实现功能

1. 删除 typora 本地无用的图片
2. 实现 Markdown 标题自动编号
3. 图片同步至阿里云 OSS 图床
4. 将带图片水印的笔记内容复制到粘贴板中

# 如何使用？

在 IDEA 中导入此 Maven 项目，并根据需要修改 ypora-tools\src\main\resources\typora-tool.properties 配置文件。水印参数具体设置可参考阿里云 OSS 官网，其中 `type_d3F5LXplbmhlaQ` 中 `d3F5LXplbmhlaQ` 为 Oneby's Blog 的 Base64 编码

```properties
# 是否需要进行图片清理
isNeedCleanPic=true
# 是否需要进行标题编号
isNeedTiltleAutoNo=true
# 是否需要上传图片至图床
isNeedPicSyncOSS=true
# 图片水印配置
isNeedWaterMark=true
# 笔记的存储路径（支持递归）
noteRootPath=<输入你的笔记存储路径>

# 阿里云 OSS 配置信息
endPoint=<输入你的 endpoint>
bucketName=<输入你的 bucketName>
accessKeyId=<输入你的 accessKeyId>
accessKeySecret=<输入你的 accessKeySecret>
bucketDomain=<输入你的 bucketDomain>
# 图片水印参数
waterMarkParams=?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,text_T25lYnkncyBCbG9n,size_20,g_center,color_FFFFFF,shadow_100,t_100,g_se,x_15,y_15
```

将 Java 程序打包成可执行 jar 包，并根据 jar 包存放路径修改【typora-tools.bat】批处理程序，

最后执行【!添加∕删除右键菜单.bat】批处理程序将此工具添加到右键菜单栏中

