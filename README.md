# typora-tools

一款用 Java 编写的 Typora 小工具

# 实现功能

1. 删除 typora 本地无用的图片
2. 实现 Markdown 标题自动编号
3. 图片同步至阿里云 OSS 图床

# 如何使用？

在 IDEA 中导入此 Maven 项目，并根据需要修改 ypora-tools\src\main\resources\typora-tool.properties 配置文件

```properties
# 是否需要进行图片清理
isNeedCleanPic=true
# 是否需要进行标题编号
isNeedTiltleAutoNo=true
# 是否需要进行图片同步
isNeedPicSyncOSS=true
# 笔记的根目录（也可以填入单个的 .md 文件）
noteRootPath=<输入你的笔记存储路径>

# 阿里云 OSS 配置信息
endPoint=<输入你的 endpoint>
bucketName=<输入你的 bucketName>
accessKeyId=<输入你的 accessKeyId>
accessKeySecret=<输入你的 accessKeySecret>
bucketDomain=<输入你的 bucketDomain>
```

