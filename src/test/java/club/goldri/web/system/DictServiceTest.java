package club.goldri.web.system;

import club.goldri.core.util.BeanUtil;
import club.goldri.core.test.BaseTest;
import club.goldri.web.system.domain.SysDict;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.DictService;
import club.goldri.web.system.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * 测试用例
 * 1. 按Type+Code查询后，缓存有记录
 * 2. 按Type查询后，缓存有list记录
 * 3. 添加可以添加成功
 */
public class DictServiceTest extends BaseTest {

    private SysUser currentUser;//模拟当前用户

    @Autowired
    private UserService userService;

    @Autowired
    private DictService dictService;

    @Before
    public void init(){
        this.currentUser = this.userService.selectUserById("0");

    }

    @Test
    @Rollback
    public void testSave(){
        SysDict sysDict = new SysDict();

        sysDict.setType("TestType");
        sysDict.setCode("TestCode");
        sysDict.setLabel("xxx");
        sysDict.setIsDesign("0");
        BeanUtil.setCreateUser(currentUser, sysDict);
        BeanUtil.setUpdateUser(currentUser, sysDict);

        SysDict dict = this.dictService.commonSave(currentUser, sysDict);
        System.out.println(dict.toString());
        assert dict.getType() != null && dict.getCode() != null;
    }
}
