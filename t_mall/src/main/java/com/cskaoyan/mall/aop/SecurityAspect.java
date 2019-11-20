package com.cskaoyan.mall.aop;

import com.cskaoyan.mall.bean.Admin;
import com.cskaoyan.mall.bean.BaseReqVo;
import com.cskaoyan.mall.bean.Log;
import com.cskaoyan.mall.mapper.LogMapper;
import com.cskaoyan.mall.utils.AopUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

@Component
@Aspect
public class SecurityAspect {

    @Autowired
    LogMapper logMapper;

    private String admin;
    private String ipAddress;
    private int type = 1;   //安全操作
    private String action;
    private boolean status;
    private String result;

    @Pointcut("@annotation(com.cskaoyan.mall.aopAnnotation.Security)")
    public void securityPointcut(){}

    @Before(value = "securityPointcut()")
    public void myBefore(JoinPoint joinPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI().toString();
        if("/admin/admin/update".equals(uri)){
            Object[] args = joinPoint.getArgs();
            Admin admin = (Admin) args[0];
            result = "新名称:" + admin.getUsername();
            System.out.println("操作结果:" + result);
        }else if("/admin/admin/create".equals(uri)){
            Object[] args = joinPoint.getArgs();
            Admin admin = (Admin) args[0];
            result = "新添管理员名称:" + admin.getUsername();
            System.out.println("操作结果:" + result);
        }else if("/admin/admin/delete".equals(uri)){
            Object[] args = joinPoint.getArgs();
            Admin admin = (Admin) args[0];
            result = "被删除管理员名称："+ admin.getUsername();
            System.out.println("操作结果:" + result);
        }else if("/admin/auth/login".equals(uri)){
            result = null;
        }
    }

    @After("securityPointcut()")
    public void myafter(){
    }

    @Around(value = "securityPointcut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //ip,uri,action,admin
        ipAddress = AopUtils.getIpAddress();
        String uri = request.getRequestURI().toString();
        //action可能为null
        action = urlToAction(uri);
        admin = (String) session.getAttribute("username");
        System.out.println(admin);

/*        System.out.println("ip地址:" + ipAddress);
        System.out.println("URI:" + uri);
        System.out.println("操作动作:" + action);
        System.out.println("管理员:" + admin);*/

        Object proceed = joinPoint.proceed();
        return proceed;
    }

    //获得被代理方法的返回值
    @AfterReturning(value = "securityPointcut()",returning = "baseReqVo")
    public void myafterReturning(Object baseReqVo){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI().toString();
        BaseReqVo reqVo = (BaseReqVo) baseReqVo;
        if("/admin/admin/update".equals(uri)){
            if("成功".equals(reqVo.getErrmsg())){
                status = true;
            }else{
                status = false;
                result = null;
            }
            //System.out.println("操作状态:" + status);
        }
        if("/admin/admin/delete".equals(uri)){
            if("成功".equals(reqVo.getErrmsg())){
                status = true;
            }else{
                status = false;
                result = null;
            }
            //System.out.println("操作状态:" + status);
        }
        if("/admin/admin/create".equals(uri)){
            if("成功".equals(reqVo.getErrmsg())){
                status = true;
            }else{
                status = false;
                result = null;
            }
            //System.out.println("操作状态:" + status);
        }
        if("/admin/auth/login".equals(uri)){
            //登录逻辑还没做好，每次都会成功的
            status = true;
            //System.out.println("操作状态:" + status);
            //status = false;
            //result = null;
        }

        //被代理方法执行完毕，把操作记录写入操作日志
        Log log = new Log();
        log.setAdmin(admin);
        log.setIp(ipAddress);
        log.setType(type);
        log.setAction(action);
        log.setStatus(status);
        log.setResult(result);
        log.setDeleted(false);

        Date date = new Date();
        log.setAddTime(date);
        log.setUpdateTime(date);

        logMapper.insertLogger(log);
    }

    @AfterThrowing(value = "securityPointcut()",throwing = "exceptionz")
    public void myafterThrowing(Throwable exceptionz){
        //System.out.println("afterThrowing:" + exceptionz.getMessage());
    }


    /**
     * 根据uri获得操作动作
     * @param uri
     * @return
     */
    public String urlToAction(String uri){
        if("/admin/admin/update".equals(uri)){
            return "编辑管理员";
        }else if("/admin/admin/create".equals(uri)){
            return "添加管理员";
        }else if("/admin/admin/delete".equals(uri)){
            return "删除管理员";
        }else if("/admin/auth/login".equals(uri)){
            return "登录";
        }
        return null;
    }

}

