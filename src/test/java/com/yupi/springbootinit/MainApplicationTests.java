package com.yupi.springbootinit;

import com.yupi.springbootinit.config.WxOpenConfig;
import javax.annotation.Resource;

import com.yupi.springbootinit.service.UserInterfaceInfoLinkageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Resource
    private UserInterfaceInfoLinkageService userInterfaceInfoLinkageService;

    @Test
    void contextLoads() {
        boolean b = userInterfaceInfoLinkageService.trackInterfaceUsageStats(1L, 1L);
        System.out.println(b);
    }

}
