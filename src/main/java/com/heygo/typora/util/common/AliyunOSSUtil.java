package com.heygo.typora.util.common;

import com.Entity.ResultEntity;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;

import java.io.InputStream;

/**
 * OSS 工具类
 */
public class AliyunOSSUtil {

    /**
     * 上传文件至 OSS 服务器
     * @param endpoint          OSS endpoint
     * @param accessKeyId       OSS accessKeyId
     * @param accessKeySecret   OSS accessKeySecret
     * @param bucketDomain      OSS bucketDomain
     * @param bucketName        OSS bucketName
     * @param inputStream       待上传文件的输入流对象
     * @param folderName        OSS 上的文件夹路径（你要把文件存在那个文件夹找中）
     * @param originalName      文件的原始名称
     * @return                  参考 ResultEntity
     */
    public static ResultEntity<String> uploadFileToOss(
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            String bucketDomain,
            String bucketName,
            InputStream inputStream,
            String folderName,
            String originalName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // folderName + originalName 获得文件在 OSS 上的存储路径
        String objectName = folderName + "/" + originalName;

        try {
            // 调用OSS客户端对象的方法上传文件并获取响应结果数据
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, inputStream);

            // 从响应结果中获取具体响应消息
            ResponseMessage responseMessage = putObjectResult.getResponse();

            // 根据响应状态码判断请求是否成功
            if (responseMessage == null) {

                // 获得刚刚上传的文件的路径
                String ossFileAccessPath = bucketDomain + "/" + objectName;

                // 当前方法返回成功
                return ResultEntity.successWithData(ossFileAccessPath);
            } else {
                // 获取响应状态码
                int statusCode = responseMessage.getStatusCode();

                // 如果请求没有成功，获取错误消息
                String errorMessage = responseMessage.getErrorResponseAsString();

                // 当前方法返回失败
                return ResultEntity.failed("当前响应状态码=" + statusCode + " 错误消息=" + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();

            // 当前方法返回失败
            return ResultEntity.failed(e.getMessage());
        } finally {
            if (ossClient != null) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }

    }

    /**
     * 查看文件是否已经存在于 OSS 服务器
     * @param endpoint          OSS endpoint
     * @param accessKeyId       OSS accessKeyId
     * @param accessKeySecret   OSS accessKeySecret
     * @param bucketName        OSS bucketName
     * @param folderName        文件夹路径
     * @param fileName          文件 file 对象
     * @return                  true：存在；false：不存在
     */
    public static Boolean isFileExitsOnOSS(
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            String bucketName,
            String folderName,
            String fileName
    ) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 拼接 objectName
        String objectName = folderName + "/" + fileName;

        // 是否找到文件
        boolean isFound = false;

        try {
            // 判断文件是否存在。doesObjectExist还有一个参数isOnlyInOSS，如果为true则忽略302重定向或镜像；如果为false，则考虑302重定向或镜像。
            isFound = ossClient.doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();

            // 抛异常则认为没找到
            isFound = false;
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }

        // 返回查询结果
        return isFound;
    }

}
