package club.goldri.web.system;

import club.goldri.core.constant.Constant;
import club.goldri.core.test.BaseTest;
import club.goldri.web.system.domain.SysUserRole;
import club.goldri.web.system.mapper.SysUserRoleMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

public class SysUserRoleTest extends BaseTest {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Test
    public void testDelete(){
//        int res = this.sysUserRoleMapper.deleteAAA("0","22487065df1711e7a40500163e0823ae", "0");
//        System.out.println(res);
        SysUserRole sysUserRole = new SysUserRole();
//        sysUserRole.setUserId("22487065df1711e7a40500163e0823ae");
//        sysUserRole.setRoleId("0");
        sysUserRole.setCreateBy("0");
        sysUserRole.setDelFlag(Constant.DEL_FLAG_DELETE);
        sysUserRole.setCreateDate(new Date());
        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId","22487065df1711e7a40500163e0823ae");
        criteria.andNotEqualTo("delFlag", Constant.DEL_FLAG_DELETE);
//        criteria.andEqualTo("roleId","0");

        int res = this.sysUserRoleMapper.updateByExampleSelective(sysUserRole, example);
        assert res == 1;
    }

}
