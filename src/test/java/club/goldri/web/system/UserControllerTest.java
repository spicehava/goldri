package club.goldri.web.system;

import club.goldri.core.util.TestUtil;
import club.goldri.web.system.domain.SysOrg;
import club.goldri.web.system.domain.SysUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testSaveUser() throws Exception {
        SysUser user = new SysUser();

        user.setUsername("testUsername");
        user.setEmail("test@12.com");
        user.setPassword("xxx");
        user.setState("NORMAL");
        user.setSalt("xxx");

        List<SysOrg> orgList = new ArrayList<SysOrg>();
        SysOrg sysOrg1 = new SysOrg();
        sysOrg1.setId("2");
        sysOrg1.setName("软件部");
        orgList.add(sysOrg1);

        SysOrg sysOrg2 = new SysOrg();
        sysOrg2.setId("4");
        sysOrg2.setName("综合部");
        orgList.add(sysOrg2);

        user.setOrgList(orgList);


        String Authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTUyODgxODAxMCwibmJmIjoxNTEzMDUwMDEwfQ.J6yt62rsuBiqjsJkWJg7CJ70Ko7Hq9SYKw0bOb63-sY";
        String username = "admin";
        HttpHeaders headers = TestUtil.getHeaders(Authorization, username);

        String request = "/user/adduser";

        SysUser user1 = restTemplate.getForObject("/user/0", SysUser.class);
        System.out.println(user1.toString());
        mockMvc.perform(
                get(request).headers(headers)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.writeValueAsString(user))
        )
                //判断返回值，是否达到预期，测试示例中的返回值的结构如下{"errcode":0,"errmsg":"OK","p2pdata":null}
                .andExpect(status().isOk());
    }

    /**
     * 测试用例：
     * 修改用户时，缓存清空，包括id和username
     * 修改包括：修改用户信息、修改密码（自己的、他人的）
     */
}
