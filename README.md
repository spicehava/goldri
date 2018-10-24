方法列表
 public List<SysUser> listAll(SysUser sysUser);
 //更新缓存
 public SysUser commonSave(SysUser currentUser, SysUser sysUser)
 //添加缓存
 public SysUser selectUserById(String userId);
 //缓存清除
 public SysUser removeUserById(SysUser currentUser ,String userId);
 
 基础数据删除，需要将所有相关的数据的缓存清空，并且考虑失效顺序  
√ 资源删除：Evict：单个资源、所有用户资源、所有角色资源、所有用户、所有角色  
√ 角色删除：Evict：单个角色、所有用户、单个角色的资源  
√ 组织删除：Evict：单个组织、所有用户  
√ 用户删除：Evict：单个用户、单个用户的资源、单个用户的角色、单个用户的部门  

说明：清关所有用户、所有角色的原因，这些实体中记录了资源或部门  

 基础数据修改，需要更新或清空缓存   
√ 资源修改：Put：单个资源；Evict：所有用户资源、所有角色资源、所有用户、所有角色  （先Evict再Put）
 角色修改：Put：单个角色；Evict：所有用户 （角色资源不需要处理，记录的是角色ID）  
√ 组织修改：Put：单个组织；Evict：所有用户   
√ 用户修改：Put：单个用户；  
 
 角色资源修改：
 用户资源修改：
 用户部门修改：
 
 
## 规范
返回List的方法名称全部为 listXxx

返回对象的全部为 selectXxx

## 设计相关
前后端分离，shiro 不需要使用缓存，因为已经关闭了session

SysCache :用于记录系统信息，例如，用户登录尝试失败次数
```
|- 系统缓存
    |- 登录错误次数    key:error_count + username
    |- 是否已登陆（用于踢出其他已登陆用户）
    |- token是否已被使用
    
|- 用户缓存（userCache）
    |- 按id存储         key: userId- + id            value:SysUser
    |- 按username存储   key: username- + username    value:SysUser
|- 资源缓存
    |- 系统资源         key:id                        value:SysResource
    |- 用户资源         key:userid+id                 value:List<SysResource>
    |- 角色资源         key:roleId + id               value:List<SysResource>
|- 组织缓存
    |- 系统组织         key:id                        value:SysOrg
    |- 用户组织         key:userId- + id              value:List<SysOrg>
|- 角色缓存
    |- 系统角色         key:id                        value:SysRole
    |- 用户角色         key:userId- + id              value:List<SysRole>
|- 字典缓存
    |- typeAndCode      key:dictTc- + type + code         value:SysDict
    |- type             key:dictType- type                value:List<SysDict>
对于value为list的缓存，当该对象修改时失效处理，下次获取时重新更新
```
## 缓存

### 规定缓存使用规则

获取缓存后，通过"前缀+唯一标识"作为该缓存的key

前缀用于区分是哪个表的业务数据，标识可以是id可以是username等

### 规定缓存加载策略

#### 策略1
使用的时候加载，修改的时候更新
#### 策略2
启动的时候加载，修改的时候更新
系统启动时，加载所有需要缓存的数据
#### 现状
使用策略1

当缓存数据发生变化时，要做相应的更新
修改、删除、添加


## 用户缓存
    
    数据字典
    
    用户
        
    用户资源
    当用户的权限修改后，清空用户权限缓存
    角色
    
    角色资源
    
    组织
    
## 系统缓存

设定一些系统参数


## 使用缓存
直接注解在方法上，针对方法进行缓存
@Cacheable(value = "myCache", key = "'getOrderList'+#params['userId']")//设置缓存，key表示参数params（map）中的userId的值
@CachePut(value="myCache",key="'id:'+#p0['id']")//更新缓存，key表示第0个变量（map）中的id的值
@CacheEvict(value = "myCache", key = "'get'+#user.id")//删除指定缓存，key表示对象user中id的值
@CacheEvict(value = "myCache", allEntries = true)//删除全部缓存
颗粒度适中，方便使用