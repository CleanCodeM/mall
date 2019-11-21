package com.cskaoyan.mall.service;

import com.cskaoyan.mall.bean.GrouponRules;

import java.util.List;

public interface GrouponRulesService {
    List<GrouponRules> queryGrouponRulers(Integer page, Integer limit, Integer goodsId);

    int deleteGroupRules(Integer id);

    int updateGrouponRules(GrouponRules grouponRules);

    int insertGrouponRules(GrouponRules grouponRules);

    List<GrouponRules> queryGrouponsRulesList(Integer page, Integer size);

    GrouponRules selectRulesById(Integer rulesId);
}
