package com.atguigu.tingshu.album.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.tingshu.album.config.MinioConstantProperties;
import com.atguigu.tingshu.album.service.FileUploadService;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.result.ResultCodeEnum;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * @author admin
 * @version 1.0
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConstantProperties minioConstantProperties;

    /**
     * 上传文件到MinIO服务器
     *
     * @param file 上传的文件
     * @return 上传成功后文件的访问URL
     * @throws RuntimeException 如果文件不是图片或者上传过程中发生错误，则抛出运行时异常
     */
    @Override
    public String fileUpload(MultipartFile file) {
        try {
            // 校验上传文件是否为图片
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                throw new GuiguException(ResultCodeEnum.ARGUMENT_VALID_ERROR);
            }
            String today = DateUtil.today();
            String extName = FileUtil.extName(file.getOriginalFilename());
            String uuid = IdUtil.randomUUID();
            String fileName = today + "/" + uuid + "." + extName;
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConstantProperties.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioConstantProperties.getEndpointUrl() + "/" + minioConstantProperties.getBucketName() + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
