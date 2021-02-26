package com.benearyou.tab_web.util;

import com.benearyou.tab_web.entity.Setting;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    public static Setting getProfile() {
        return (Setting) SecurityUtils.getSubject().getPrincipal();
    }

}
