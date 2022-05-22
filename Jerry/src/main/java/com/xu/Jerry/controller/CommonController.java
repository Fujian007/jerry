package com.xu.Jerry.controller;

import com.xu.Jerry.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${jerry.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info(file.toString());

        //获取原始文件名,作为保存的文件名，不建议这样做，因为有可能存在文件名相同的情况
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        //使用UUID生产文件名，防止出现重名的情况
        String fileName = UUID.randomUUID() + suffix;

        //判断文件夹是否存在，不存在就去创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        log.info("文件名：{}",basePath + fileName );
        try {
            //需要保存上传的临时文件到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    //文件下载的方法
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        //输入流，读取文件
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
        //构造输出流，写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应数据类型为图片
            response.setContentType("image/jpeg");

            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
