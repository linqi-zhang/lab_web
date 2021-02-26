package com.benearyou.tab_web.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benearyou.tab_web.entity.Article;
import com.benearyou.tab_web.entity.Setting;
import com.benearyou.tab_web.mapper.ArticleMapper;
import com.benearyou.tab_web.mapper.SettingMapper;
import com.benearyou.tab_web.service.ArticleService;
import com.benearyou.tab_web.service.SettingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest

public class TestMybatisPlus {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SettingService settingService;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method testSelect ------"));
        List<Article> articles = articleMapper.selectList(null);
        articles.forEach(System.out::println);
    }

    @Test
    public void testSetting() {
        Setting setting = settingService.getOne(new QueryWrapper<Setting>().eq("username", "markerhub"));
        System.out.println(setting.getId());
        System.out.println("--------------------------------------- test");
    }

}
