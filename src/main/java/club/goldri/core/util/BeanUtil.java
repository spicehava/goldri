package club.goldri.core.util;

import club.goldri.web.system.domain.SysUser;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BeanUtil {

    public static final String ADD = "ADD";
    public static final String REMOVE = "REMOVE";
    public static final String NORMAL = "NORMAL";

    //创建对象时设置用户信息
    public static void setCreateUser(HttpServletRequest request, Object obj){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        ReflectUtil.setFieldValue(obj ,"createBy", currentUser.getId());
        ReflectUtil.setFieldValue(obj ,"creater", currentUser.getName());
        ReflectUtil.setFieldValue(obj ,"createDate", new Date());
    }

    //创建对象时设置用户信息
    public static void setUpdateUser(HttpServletRequest request, Object obj){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        ReflectUtil.setFieldValue(obj ,"updateBy", currentUser.getId());
        ReflectUtil.setFieldValue(obj ,"updater", currentUser.getName());
        ReflectUtil.setFieldValue(obj ,"updateDate", new Date());
    }

    //创建对象时设置用户信息
    public static void setCreateUser(SysUser sysUser, Object obj){
        ReflectUtil.setFieldValue(obj ,"createBy", sysUser != null ? sysUser.getId() : "0");
        ReflectUtil.setFieldValue(obj ,"creater", sysUser != null ? sysUser.getName() : "系统");
        ReflectUtil.setFieldValue(obj ,"createDate", new Date());
    }

    //创建对象时设置用户信息
    public static void setUpdateUser(SysUser sysUser, Object obj){
        ReflectUtil.setFieldValue(obj ,"updateBy", sysUser != null ? sysUser.getId() : "0");
        ReflectUtil.setFieldValue(obj ,"updater", sysUser != null ? sysUser.getName() : "系统");
        ReflectUtil.setFieldValue(obj ,"updateDate", new Date());
    }

    /**
     * 比较原List与现List的差异，返回Map
     * Map的三个KEY值：ADD(新增)\REMOVE（删除）\NORMAL（相同）
     * @param compareList 需要比较的list
     * @param currList 当前的list
     * @return
     */
    public static Map diffList(Set<String> compareList, Set<String> currList){

        if(CollectionUtils.isEmpty(compareList) && CollectionUtils.isEmpty(currList)){
            return null;
        }

        Map<String,Set<String>> result = new HashMap<String,Set<String>>();

        result.put(ADD, new HashSet<String>());
        result.put(REMOVE, new HashSet<String>());
        result.put(NORMAL, new HashSet<String>());

        if(CollectionUtils.isEmpty(compareList)){
            result.put(REMOVE,currList);
        } else if(CollectionUtils.isEmpty(currList)){
            result.put(ADD,compareList);
        } else {
            for(String comStr : compareList){
                if(currList.contains(comStr)){
                    result.get(NORMAL).add(comStr);
                } else {
                    result.get(ADD).add(comStr);
                }
            }

            for(String currStr : currList){
                if(compareList.contains(currStr)){
                    result.get(NORMAL).add(currStr);
                } else {
                    result.get(REMOVE).add(currStr);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Set<String> compareList = new HashSet<String>();
        compareList.add("1");
        compareList.add("2");
        compareList.add("3");
        Set<String> currList = new HashSet<String>();
//        currList.add("1");
//        currList.add("2");
//        currList.add("4");
        Map map = diffList(compareList,currList);
        System.out.println(map);
    }
}
