package club.goldri.core.util;

import club.goldri.web.system.domain.SysUser;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class PasswordUtil {

    private static String algorithmName = "md5";

    private static int hashIterations = 2;

    public static String encryptPassword(String password){
        String newPassword = new SimpleHash(algorithmName, password, hashIterations).toHex();

        return newPassword;
    }

    public static String encryptPassword(String password, String salt){
        if(salt == null || "".equals(salt)){
            return PasswordUtil.encryptPassword(password);
        }

        String newPassword = new SimpleHash(algorithmName, password,
                ByteSource.Util.bytes(salt), hashIterations).toHex();

        return newPassword;
    }

    public static void encryptPassword(SysUser user) {
        String newPassword = new SimpleHash(algorithmName, user.getPassword(),
                ByteSource.Util.bytes(user.getUsername() + user.getSalt()), hashIterations).toHex();
        user.setPassword(newPassword);
    }

    public static void main(String[] args) {
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setSalt("yan");
        PasswordUtil.encryptPassword(user);
        System.out.println(user.getPassword());
        String pwd = PasswordUtil.encryptPassword("admin","adminyan");
        System.out.println(pwd);
    }
}
