package com.cskaoyan.mall.controller;

import com.cskaoyan.mall.bean.*;
import com.cskaoyan.mall.service.*;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wx/groupon")
public class WxGrouponController {

    @Autowired
    GrouponRulesService grouponRulesService;
    @Autowired
    GrouponService grouponService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    OrderGoodsService orderGoodsService;
    @Autowired
    OrderStatusService orderStatusService;

    @RequestMapping("list")
    public BaseReqVo list(Integer page,Integer size){
        List<GrouponRules> grouponsRulesList = grouponRulesService.queryGrouponsRulesList(page,size);

        //查询所有的条目数
        PageInfo<GrouponRules> pageInfo = new PageInfo<>(grouponsRulesList);
        long total = pageInfo.getTotal();
        ArrayList<Map> list = new ArrayList<>();
        for(GrouponRules grouponRules : grouponsRulesList){
            HashMap<String, Object> map = new HashMap<>();
            HashMap<String, Object> goodsMap = new HashMap<>();
            Integer id = grouponRules.getGoodsId();
            Integer rulesId = grouponRules.getId(); //关联groupon的rules_id
            BigDecimal discount =  grouponRules.getDiscount();

            //查询goods相关
            Goods goods = goodsService.queryGoods(id);
            goodsMap.put("id",id);
            goodsMap.put("name",goods.getName());
            goodsMap.put("brief",goods.getBrief());
            goodsMap.put("picUrl",goods.getPicUrl());
            goodsMap.put("counterPrice",goods.getCounterPrice());
            goodsMap.put("retailPrice",goods.getRetailPrice());

            //查询group_member团购人数相关
            Integer group_member = grouponService.selectGrouponMemberByRuleId(rulesId);
            //查询groupon_price现价相关
            BigDecimal groupon_price = goods.getRetailPrice().subtract(discount);
            map.put("goods",goodsMap);
            map.put("groupon_price",groupon_price);
            map.put("groupon_member",group_member);
            list.add(map);
        }

        BaseReqVo baseReqVo = new BaseReqVo();
        HashMap<String, Object> map = new HashMap<>();

        map.put("data",list);
        map.put("count",total);

        baseReqVo.setData(map);
        baseReqVo.setErrmsg("成功");
        baseReqVo.setErrno(0);
        return baseReqVo;
    }

    @RequestMapping("my")
    public BaseReqVo myGroupon(Integer showType){
        //登入用户信息
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Integer userId = user.getId(); //登入用户id

        //封装handleOption
        HashMap<String,Boolean> handleMap = new HashMap<>();
        handleMap.put("cancel",false);
        handleMap.put("comment",false);
        handleMap.put("confirm",false);
        handleMap.put("delete",false);
        handleMap.put("pay",false);
        handleMap.put("rebuy",false);
        handleMap.put("refund",false);

        BaseReqVo baseReqVo = new BaseReqVo();
        //外data的map
        HashMap<String, Object> map = new HashMap<>();
        //内data的list
        List<Map> list = new ArrayList<>();
        //list内的map
        HashMap<String, Object> grouponMap = null;

        List<Groupon> grouponList = null;
        boolean isCreator = true;
        if(showType == 0){ //登入用户是团购发起者
            grouponList = grouponService.selectGrouponByUId(userId);
        }else{  //登入用户是团购参与者
            grouponList = grouponService.selectGrouponByCUId(userId);
            isCreator = false;
        }

        for(Groupon groupon : grouponList){
            grouponMap = new HashMap<>();
            //根据团购订单获取团购发起者信息
            Integer creatorUserId = groupon.getCreatorUserId();
            User grouponUser = userService.queryUserByUserId(creatorUserId);
            //根据团购订单获取对应订单详情
            Order order = orderService.selectOrderByOrderId(groupon.getOrderId());
            //根据团购订单获取对应参团人数
            Integer joinerCount = grouponService.selectCountByGrouponId(groupon.getGrouponId());
            //根据团购订单获取订单号，再根据订单号获取对应的商品详情
            List<OrderGoods> orderGoodsList =
                    orderGoodsService.selectOrderGoodsByOrderId(groupon.getOrderId());
            HashMap<String, Object> goodsMap = null;
            for(OrderGoods orderGoods : orderGoodsList){
                goodsMap = new HashMap<>();
                goodsMap.put("goodsName",orderGoods.getGoodsName());
                goodsMap.put("id",orderGoods.getId());
                goodsMap.put("number",orderGoods.getNumber());
                goodsMap.put("picUrl",orderGoods.getPicUrl());
            }
            //根据orderStatus获取handleOption
            Short status = order.getOrderStatus();
            String statusName = orderStatusService.selectStatusByStatusId(status);
            if(status == 101){
                handleMap.replace("cancel",false,true);
                handleMap.replace("pay",false,true);
            }else if(status == 102 || status == 103){
                handleMap.replace("delete",false,true);
            }else if(status == 201){
                handleMap.replace("cancel",false,true);
                handleMap.replace("refund",false,true);
            }else if(status == 202){
                handleMap.replace("cancel",false,true);
            }else if(status == 203){
                handleMap.replace("delete",false,true);
                handleMap.replace("rebuy",false,true);
            }else if(status == 301){
                handleMap.replace("confirm",false,true);
            }else if(status == 401 || status == 402){
                handleMap.replace("comment",false,true);
                handleMap.replace("delete",false,true);
                handleMap.replace("rebuy",false,true);
            }
            //根据团购规则id获取团购规则
            GrouponRules grouponRules = grouponRulesService.selectRulesById(groupon.getRulesId());

            grouponMap.put("rules",grouponRules); //团购规则
            grouponMap.put("handleOption",handleMap); //不同商品状态可作处理
            grouponMap.put("goodsList",goodsMap); //获取订单内的商品详情
            grouponMap.put("groupon",groupon); //获取团购订单详情
            grouponMap.put("isCreator",isCreator); //是否是发起者
            grouponMap.put("creator",grouponUser.getNickname()); //获取发起人昵称
            grouponMap.put("id",groupon.getId()); //获取团购单号
            grouponMap.put("orderId",groupon.getOrderId()); //获取订单号
            grouponMap.put("orderSn",order.getOrderSn()); //获取订单编号
            grouponMap.put("acturalPrice",order.getActualPrice()); //实付金额
            grouponMap.put("joinerCount",joinerCount); //获取参团人数
            grouponMap.put("orderStatusText",statusName); //修改订单状态
        }

        //查询订单数量
        int count = grouponService.selectgrouponCount();

        map.put("data",list);
        map.put("count",count);

        baseReqVo.setData(map);
        baseReqVo.setErrmsg("成功");
        baseReqVo.setErrno(0);
        return baseReqVo;
    }
}