package com.cskaoyan.mall.service;

import com.cskaoyan.mall.bean.BaseReqVo;
import com.cskaoyan.mall.bean.User;
import com.cskaoyan.mall.mapper.UserMapper;
import com.cskaoyan.mall.shiro.CustomToken;
import com.github.pagehelper.PageHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> queryUsers(Integer page, Integer limit, String sort, String order, String username, String mobile) {
        PageHelper.startPage(page, limit);
        List<User> users = userMapper.queryUsers("%" + username + "%", mobile);
        return users;
    }

    @Override
    public int update(User user) {
        Date date = new Date();
        user.setLastLoginTime(date);
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    public BaseReqVo login(User user) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if (user.getUsername() == null){
            baseReqVo.setErrmsg("参数不对");
            baseReqVo.setErrno(0);
            return baseReqVo;
        }
        Subject subject = SecurityUtils.getSubject();
        CustomToken token = new CustomToken(user.getUsername(), user.getPassword(), "wx");
        try {
            subject.login(token);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseReqVo.fail();
        }
        User userFromDb = (User) subject.getPrincipal();
        HashMap<String, Object> dataMap = new HashMap<>();
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickName", userFromDb.getNickname());
        userInfo.put("avatarUrl", userFromDb.getAvatar());
        dataMap.put("userInfo", userInfo);
        //计算过期时间
        Calendar instance = Calendar.getInstance();
        Date date = new Date();
        instance.setTime(date);
        instance.add(Calendar.HOUR_OF_DAY, 12);
        Date time = instance.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tokenExpire = format.format(time);
        dataMap.put("tokenExpire", tokenExpire);
        //获取sessionId
        String sessionId = subject.getSession().getId().toString();
        dataMap.put("token", sessionId);
        baseReqVo.setData(dataMap);
        baseReqVo.setErrno(0);
        baseReqVo.setErrmsg("成功");
        return baseReqVo;
    }

    public BaseReqVo register(User user) {
        BaseReqVo<Object> baseReqVo = new BaseReqVo<>();
        if (user.getUsername() == null){
            baseReqVo.setErrmsg("参数不对");
            baseReqVo.setErrno(0);
            return baseReqVo;
        }
        Subject subject = SecurityUtils.getSubject();
        CustomToken token = new CustomToken(user.getUsername(), user.getPassword(), "wx");
        try {
            subject.login(token);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseReqVo.fail();
        }
        HashMap<String, Object> dataMap = new HashMap<>();
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickName", user.getNickname());
        userInfo.put("avatarUrl", user.getAvatar());
        dataMap.put("userInfo", userInfo);
        //计算过期时间
        Calendar instance = Calendar.getInstance();
        Date date = new Date();
        instance.setTime(date);
        instance.add(Calendar.HOUR_OF_DAY, 12);
        Date time = instance.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tokenExpire = format.format(time);
        dataMap.put("tokenExpire", tokenExpire);
        //获取sessionId
        String sessionId = subject.getSession().getId().toString();
        dataMap.put("token", sessionId);
        baseReqVo.setData(dataMap);
        baseReqVo.setErrno(0);
        baseReqVo.setErrmsg("成功");
        return baseReqVo;
    }
    @Override
    public User queryUserByUserId(Integer creatorUserId) {
        return userMapper.selectByPrimaryKey(creatorUserId);
    }
}
