package com.demo.reggie.controller;

import com.demo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String BasePath;

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取文件名后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //拼接uuid和后缀名
        String fileName = UUID.randomUUID().toString() + suffix;

        File baseDir = new File(BasePath);

        if(!baseDir.exists()) baseDir.mkdirs();

        try {
            //将文件转存到指定位置
            file.transferTo(new File(BasePath + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 图片下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //输入流
            inputStream = new FileInputStream(BasePath + name);
            //输出流
            outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1024];

            while((len = inputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null){
                outputStream.close();
                }
                if(inputStream != null){
                inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
