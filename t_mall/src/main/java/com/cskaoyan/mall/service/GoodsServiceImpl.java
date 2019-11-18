package com.cskaoyan.mall.service;

import com.cskaoyan.mall.bean.*;
import com.cskaoyan.mall.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.logging.Level;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GoodsAttributeMapper goodsAttributeMapper;
    @Autowired
    GoodsProductMapper goodsProductMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    BrandMapper brandMapper;

    //分页获取商品
    @Override
    public BaseReqVo list(Integer page, Integer limit, String sort, String order, String name, Long goodsSn) {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();
        //分页工具
        PageHelper.startPage(page, limit);
        //根据条件查询商品
        List<Goods> goodsList = goodsMapper.selectGoodsByQueryCondition(sort, order, "%" + name + "%", goodsSn);
        //获取查询数目
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsList);
        long total = pageInfo.getTotal();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("total", total);
        dataMap.put("items", goodsList);
        baseReqVo.setData(dataMap);
        baseReqVo.setErrmsg("成功");
        baseReqVo.setErrno(0);
        return baseReqVo;
    }

    //显示商品详情
    @Override
    public BaseReqVo detail(int id) {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        int[] categoryIds = {goods.getCategoryId()};
        List<GoodsAttribute> attributes = goodsAttributeMapper.selectByGoodsId(id);
        List<GoodsProduct> products = goodsProductMapper.selectByGoodsId(id);
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("categoryIds", categoryIds);
        dataMap.put("goods", goods);
        dataMap.put("attributes", attributes);
        dataMap.put("products", products);
        baseReqVo.setData(dataMap);
        baseReqVo.setErrmsg("成功");
        baseReqVo.setErrno(0);
        return baseReqVo;
    }

    //获取所有分类和品牌信息
    @Override
    public BaseReqVo catAndBrand() {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();

        List<Category> categories = categoryMapper.selectAllCat();
        ArrayList<Category> listL1 = new ArrayList<>();
        for (Category category : categories) {
            if (category.getLevel().equals("L1")){
                listL1.add(category);
            }
        }
        ArrayList<Map> categoryList = new ArrayList<>();
        for (Category category : listL1) {
            HashMap<String, Object> categoryListMap = new HashMap<>();
            categoryListMap.put("value", category.getId());
            categoryListMap.put("label", category.getName());
            ArrayList<Map> children = new ArrayList<>();
            HashMap<String, Object> childrenMap = null;
            for (Category category1 : categories) {
                if ((category1.getSortOrder() == category.getSortOrder()) && (category1.getId() != category.getId())){
                    childrenMap = new HashMap<>();
                    childrenMap.put("value", category1.getId());
                    childrenMap.put("label", category1.getName());
                    children.add(childrenMap);
                }
            }
            categoryListMap.put("children", children);
            categoryList.add(categoryListMap);
        }
        List<Brand> brands = brandMapper.selectAllBrandNoParm();
        ArrayList<Map> brandList = new ArrayList<>();
        for (Brand brand : brands) {
            HashMap<String, Object> brandMap = new HashMap<>();
            brandMap.put("value", brand.getId());
            brandMap.put("label", brand.getName());
            brandList.add(brandMap);
        }
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("categoryList", categoryList);
        dataMap.put("brandList", brandList);
        baseReqVo.setErrno(0);
        baseReqVo.setData(dataMap);
        baseReqVo.setErrmsg("成功");
        return baseReqVo;
    }
}
