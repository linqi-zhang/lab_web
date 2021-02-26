package com.benearyou.tab_web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.benearyou.tab_web.common.lang.Result;
import com.benearyou.tab_web.entity.Article;
import com.benearyou.tab_web.service.ArticleService;
import com.benearyou.tab_web.util.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author benearyou.com
 * @since 2020-07-23
 *
 * type:
 * summary
 * researcher
 * about
 * intro
 * contact
 * research
 * news
 *
 */
@RestController

public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/ArticleList")
    public Result articleList(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                       @RequestParam(value = "typeName", required=true) String typeName) {
        System.out.println(typeName);
        Page page = new Page(currentPage, 7);
        IPage pageData = articleService.page(page,
                new QueryWrapper<Article>()
                        .eq("type", typeName)
                        .orderByDesc("created"));
        return Result.succ(pageData);
    }


    @GetMapping("/ArticleGet/{id}")
    public Result articleGet(@PathVariable(name = "id") Long id) {
        Article article = articleService.getById(id);
        Assert.notNull(article, "该博客已被删除");
        return Result.succ(article);
    }


    @RequiresAuthentication
    @GetMapping("/ArticleDelete/{id}")
    public Result articleDelete(@PathVariable(name = "id") Long id) {
        articleService.remove(new QueryWrapper<Article>()
                .eq("id", id));
        return Result.succ(null);
    }

    @RequiresAuthentication
    @PostMapping("/ArticleEdit")
    public Result articleEdit(@Validated @RequestBody Article article) {
        System.out.println(article.getTitle());
        if(article.getId() == 0) {
            Article temp = new Article();
            temp.setTop(0);
            BeanUtil.copyProperties(article, temp, "id", "top");
            articleService.saveOrUpdate(temp);
        }
        else {
            articleService.saveOrUpdate(article);
        }
        article = articleService.getOne(
                new QueryWrapper<Article>().eq("title", article.getTitle())
        );
        return Result.succ(article);
    }



}
