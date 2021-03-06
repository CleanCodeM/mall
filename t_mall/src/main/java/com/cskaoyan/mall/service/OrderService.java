package com.cskaoyan.mall.service;

import com.cskaoyan.mall.bean.BaseReqVo;
import com.cskaoyan.mall.bean.Order;

public interface OrderService {
    BaseReqVo list(Integer page, Integer limit, String sort, String order, Integer[] orderStatusArray, Integer userId, String orderSn);

    BaseReqVo detailOrder(Integer id);

    Order selectOrderByOrderId(Integer orderId);

    int ship(Integer orderId, String shipChannel, String shipSn);

    int replyComment(Integer commentId, String content);

    int queryOrderByUserIdAndStatus(Integer id, Integer i);
}
