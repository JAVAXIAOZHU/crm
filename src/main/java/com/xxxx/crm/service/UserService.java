package com.xxxx.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.SaleChance;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd){
        //1. 验证参数是否为空
        checkLoginParams(userName,userPwd);
        //2.调用数据访问dao层，通过用户名查询用户记录仪，返回用户对象
        User user = userMapper.queryUserByName(userName);
        //3.判断用户对象是否存在 如果对象为空，抛出异常（异常被controller捕获并处理）
        AssertUtil.isTrue(null==user,"用户姓名不存在！");
        //4. 判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象的密码是否相等
        checkUserPwd(userPwd,user.getUserPwd());
        //返回构建对象
        return buildUserInfo(user);
    }

    /**
     * 修改密码
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    @Transactional(propagation = Propagation.REQUIRED)//增删改查都要加上这个事务处理
    public void updatePassword(Integer userId,String oldPwd,String newPwd,String repeatPwd){
        //1. 通过用户Id查询用户记录 返回用户对象
        User user=userMapper.selectByPrimaryKey(userId);
        //2. 判断用户记录是否存在
        AssertUtil.isTrue(null==user,"待更新记录不存在！");
        //3. 参数校验
        checkPasswordParams(user,oldPwd,newPwd,repeatPwd);
        //4. 设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //5. 执行更新,返回受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败！");
    }

    /**
     *修改密码的参数校验
     *   待更新用户记录是否存在（用户对象是否为空）
     *   判断原始密码是否为空
     *   判断原始密码是否正确（查询的用户对象中的用户密码是否与原始密码一致）
     *   判断新密码是否为空
     *   判断新密码是否与原始密码一致（不能一致）
     *   判断确认密码是否为空
     *   判断确认密码是否与新密码一致
     *
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        //待更新用户记录是否存在（用户对象是否为空）
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"原始密码不能为空!");
        //判断原始密码是否正确（查询的用户对象中的用户密码是否与原始密码一致）
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),"原始密码不正确!");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd),"新密码不能为空!");
        //判断新密码是否与原始密码一致（不能一致）
        AssertUtil.isTrue(oldPwd.equals(newPwd),"新密码不能与原始密码相同！");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd),"确认密码不能为空！");
        //判断确认密码是否与新密码一致
        AssertUtil.isTrue(!newPwd.equals(repeatPwd),"确认密码与新密码不一致！");
    }

    /**
     * 构建需要返回给客户端的用户对象
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel =new UserModel();
        //userModel.setUserId(user.getId());
        // 设置⽤户信息（将 userId 加密）
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象的密码是否相等
     * @param userPwd  前台传递的密码
     * @param userPwd1  正在数据库中通过用户名查到的密码
     */
    private void checkUserPwd(String userPwd, String userPwd1) {
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作⽐较
        userPwd = Md5Util.encode(userPwd);
        // ⽐较密码
        AssertUtil.isTrue(!userPwd.equals(userPwd1), "⽤户密码不正确！");
    }

    /**
     * 验证⽤户登录参数
     * @param userName
     * @param userPwd
     */
    private void  checkLoginParams(String userName, String userPwd) {
        //判断姓名是否为空
        //如果用户姓名为空，则StringUtils.isBlank(userName)是true   然后isTrue抛出参数异常，被controller接收
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空！");
        //判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");
    }

    /**
     * 查询所有的销售⼈员
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 多条件分页查询用户数据
     * @param userQuery
     * @return
     */
    public Map<String , Object> queryUserByParams(UserQuery userQuery){
        Map<String , Object> map=new HashMap<>();
        PageHelper.startPage(userQuery.getPage(), userQuery.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(userQuery));
        map.put("code",0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
      添加⽤户
      1. 参数校验
          ⽤户名 ⾮空 唯⼀性
          邮箱 ⾮空
          ⼿机号 ⾮空 格式合法
      2. 设置默认参数
          isValid 1
          creteDate 当前时间
          updateDate 当前时间
          userPwd 123456 -> md5加密
      3. 执⾏添加，判断结果
     */
    /**
     * 添加⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        // 1. 参数校验
        checkParams(user.getUserName(), user.getEmail(), user.getPhone(),null);
        // 2. 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //默认密码  123456
        user.setUserPwd(Md5Util.encode("123456"));
        // 3. 执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user) == null, "⽤户添加失败！");
        /*用户角色关联*/
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户角色关联
     * 从添加之后的返回值的拿去添加成功后的主键id值，也就是userId
     * 然后再拿添加数据或者更新数据的时候的下拉框中的角色id值，也就是roleId
     * 这两个id在t_user_role 中
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //通过用户id查询角色记录
        Integer count=userRoleMapper.countUserRoleByUserId(userId);
        //判断角色记录是否存在
        if (count>0){
            //角色记录存在  则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count ,"用户角色分配失败！");
        }
        //判断角色ID是否存在  如果存在 则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)){
            //将用户角色数据设置到集合中 执行批量添加
            List<UserRole> userRoleList=new ArrayList<>();
            //将角色Id字符串转换成数组
            String[] roleIdsArray =roleIds.split(",");
            //遍历数组 得到对应的用户角色对象  并设置到集合中
            for (String roleId : roleIdsArray){
                UserRole userRole=new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //设置到集合中
                userRoleList.add(userRole);
            }
            //批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size() , "用户添加失败！");
        }
    }

    /**
     * 参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone,Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户名不能为空！");
        // 验证⽤户名是否存在
        User temp = userMapper.queryUserByName(userName);
        // 如果是添加操作，数据库是没有数据的，数据库中只要查询到⽤户记录就表示不可⽤
        // 如果是修改操作，数据库是有数据的，查询到⽤户记录就是当前要修改的记录本身就表示可⽤，否则不可⽤
        // 数据存在，且不是当前要修改的⽤户记录，则表示其他⽤户占⽤了该⽤户名
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(userId)), "该⽤户已存在！");
        AssertUtil.isTrue(StringUtils.isBlank(email), "请输⼊邮箱地址！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "⼿机号码格式不正确！");
    }



    /**
      更新⽤户
      1. 参数校验
          id ⾮空 记录必须存在
          ⽤户名 ⾮空 唯⼀性
          email ⾮空
          ⼿机号 ⾮空 格式合法
      2. 设置默认参数
          updateDate
      3. 执⾏更新，判断结果
      @param user
     */
    /**
     * 更新⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        // 1. 参数校验
        //判断用户Id是否为空 且数据存在
        AssertUtil.isTrue(null == user.getId(),"带更新记录不存在！");
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "⽤户更新失败！");
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 删除⽤户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null==ids || ids.length == 0,"请选择待删除的⽤户记录!");
        AssertUtil.isTrue(deleteBatch(ids) != ids.length,"⽤户记录删除失败!");
        //遍历用户Id的数组
        for (Integer userId : ids){
            //通过用户Id查询对应的用户角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            if (count>0){
                //通过用户Id删除对应的用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) !=count,"删除用户失败！");
            }
        }
    }
}
