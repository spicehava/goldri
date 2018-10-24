package club.goldri.web.system;

import club.goldri.GoldriApplication;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoldriApplication.class)
public class CacheTest {
    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void before() {
        SysUser user = new SysUser();
        user.setUsername("aaa");
        user.setPassword("aaa");
        user.setName("aaa");
        user.setSalt("aaa");
        user.setState("ss");
        user.setDelFlag("NORMAL");
        user.setCreateBy("0");
        user.setCreateDate(new Date());
        user.setUpdateBy("0");
        user.setUpdateDate(new Date());
        userService.save(user);

    }

    @Test
    @Transactional
    @Rollback
    public void test() throws Exception {

        SysUser u1 = this.userService.selectUserByUsername("aaa");
        System.out.println("第一次查询：" + u1.getName());

        SysUser u2 = this.userService.selectUserByUsername("aaa");
        System.out.println("第二次查询：" + u2.getName());

        u1.setName("bbb");
        this.userService.updateNotNull(u1);
        SysUser u3 = this.userService.selectUserByUsername("aaa");
        System.out.println("第三次查询：" + u3.getName());
    }
}
