package com.benearyou.tab_web.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benearyou.tab_web.common.lang.Result;
import com.benearyou.tab_web.entity.Article;
import com.benearyou.tab_web.entity.Imglist;
import com.benearyou.tab_web.mapper.ImglistMapper;
import com.benearyou.tab_web.service.ImglistService;
import com.hotstrip.utils.FileUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/9/6.
 * 上传图片 Controller 类
 */
@RestController
public class ImglistController {
    private static Logger logger = LoggerFactory.getLogger(ImglistController.class);

    @Autowired
    private ImglistService imglistService;

    @GetMapping(value = "/ImageListGet")
    public Result list(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage) {
        Page<Imglist> page = new Page<Imglist>(currentPage, 7);
        IPage pageData = imglistService.page(page,
                new QueryWrapper<Imglist>().isNotNull("id"));
        return Result.succ(pageData);
    }

    @GetMapping(value = "/CoverImageGet/{article_id}")
    public Result coverImageGet(@PathVariable(name = "article_id") Long article_id) {
        Imglist imglist = imglistService.getOne(
                new QueryWrapper<Imglist>()
                        .eq("article_id", article_id)
                        .orderByDesc("id"));
        Assert.notNull(imglist, "该图片已被删除");
        return Result.succ(imglist);
    }


    @RequiresAuthentication
    @GetMapping("/ImageDelete/{article_id}")
    public Result imageDelete(@PathVariable(name = "article_id") Long article_id) {
        imglistService.remove(new QueryWrapper<Imglist>()
                .eq("article_id", article_id));
        return Result.succ(null);
    }

    @RequiresAuthentication
    @PostMapping("/ImageSave")
    public Result imageSave(@RequestBody String[] strings) {
        Integer id = Integer.parseInt(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            imglistService.remove(new QueryWrapper<Imglist>()
                    .eq("url", strings[i]));
            Imglist imglist = new Imglist();
            imglist.setArticleId(id);
            imglist.setUrl(strings[i]);
            imglistService.save(imglist);
        }
        return Result.succ(null);
    }


    /**
     * 上传图片接口地址
     * @param multipartHttpServletRequest
     * @return
     */
    @RequiresAuthentication
    @PostMapping(value = "/ImageUpload")
    public Object uploadImage(MultipartHttpServletRequest multipartHttpServletRequest){
        // 图片存储路径
        String path = "target/classes/static/upload_image";
        // 返回值
        HashMap<String, Object> map = new HashMap<>();
        List<String> data = new ArrayList<>();
        // 取得request中的所有文件名
        Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
        // 遍历
        while (iterator.hasNext()) {
            // 取得上传文件
            MultipartFile multipartFile = multipartHttpServletRequest.getFile(iterator.next());
            if (multipartFile != null) {
                // 文件名
                String fileName = multipartFile.getOriginalFilename();
                // 获取文件拓展名
                String extName = FileUtil.getExtName(fileName);
                if (StringUtils.isEmpty(extName)) {
                    logger.error("文件后缀名称为空，文件可能有问题...");
                    map.put("errno", 1);
                    map.put("data", data);
                    return map;
                }
                // 保证 文件夹存
                File fileDir = new File(path);
                if (!fileDir.exists()){
                    fileDir.mkdirs();
                }
                File file = new File(fileDir, fileName);
                // 拷贝文件流  到上面的文件
                FileUtil.copyInputStreamToFile(multipartFile, file);
                // 新文件名称
                String newFileName = System.currentTimeMillis() + FileUtil.randomValue(10000)+ "." + extName;
                File finalFile = new File(fileDir, newFileName);
                if(file.renameTo(finalFile)){
                    logger.info("上传文件信息接口......重命名文件成功.........【新文件名称：{}】", finalFile.getName());
                }
                // 构建图片的可访问地址
                String webUrl = multipartHttpServletRequest.getScheme()
                        + "://" + multipartHttpServletRequest.getServerName()
                        + ":" + multipartHttpServletRequest.getServerPort()
                        + multipartHttpServletRequest.getContextPath();
                String imageUrl = finalFile.getPath().substring(path.length()).replaceAll("\\\\", "/");
                logger.info("文件路径： {}", webUrl + "/upload_image" + imageUrl);
                // 添加到数组中

                data.add(webUrl + "/upload_image" + imageUrl);
            }
        }
        // 返回前端需要的格式
        map.put("errno", 0);
        map.put("data", data);
        return map;
    }
}
