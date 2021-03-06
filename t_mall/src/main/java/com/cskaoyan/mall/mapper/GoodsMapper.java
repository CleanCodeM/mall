package com.cskaoyan.mall.mapper;

import com.cskaoyan.mall.bean.Goods;
import com.cskaoyan.mall.bean.GoodsExample;
import com.cskaoyan.mall.bean.Groupon;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    long countByExample(GoodsExample example);

    int deleteByExample(GoodsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Goods record);

    int insertSelective(Goods record);

    List<Goods> selectByExampleWithBLOBs(GoodsExample example);

    List<Goods> selectByExample(GoodsExample example);

    Goods selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Goods record, @Param("example") GoodsExample example);

    int updateByExampleWithBLOBs(@Param("record") Goods record, @Param("example") GoodsExample example);

    int updateByExample(@Param("record") Goods record, @Param("example") GoodsExample example);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    //获取商品
    List<Goods> selectGoodsByQueryCondition(@Param("sort") String sort, @Param("order") String order, @Param("name") String name, @Param("goodsSn") Long goodsSn);

    void updateByGoodsMap(@Param("goods") Map goods, @Param("updateTime") String updateTime);

    void deleteByGoodsId(@Param("goodsId") int goodsId);

    int insertByGoodsMap(@Param("goods") Map goods, @Param("addTime") String addTime);

    void updateInfoOfLastInsert(@Param("goodsId") int goodsId, @Param("gallery") String gallery, @Param("shareUrl") String shareUrl);

    Goods queryGoodsInfoByGoodsId(@Param("goodsId") Integer id);

    List<Goods> selectNewGoods();

    List<Goods> selectHotGoods();

    List<Goods> selectByCategoryId(@Param("catagoryId") Integer catagoryId);

    List<Goods> queryRelatedGoods(@Param("categoryId") Integer categoryId);

    int queryGoodsNum();

    List<Goods> queryGoodsByCategoryId(@Param("categoryId") Integer categoryId);

    int queryGoodsNumByCategoryId(@Param("categoryId") Integer categoryId);

    List<Integer> queryCategoryIds(@Param("keyword") String keyword, @Param("isNew") Boolean isNew, @Param("isHot") Boolean isHot);

    List<Goods> queryGoods(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId,
                           @Param("sort") String sort, @Param("order") String order,
                           @Param("isNew") Boolean isNew, @Param("isHot") Boolean isHot,
                           @Param("brandId") Integer brandId);

    List<String> queryHelpers(@Param("keyword") String keyword);

    Goods queryCollection(@Param("goodsId") Integer goodsId);

    List<Groupon> queryGroupon();

    Goods selectByGoodsId(@Param("goodsId") Integer goodsId);

    Double selectPriceById(@Param("goodsId") Integer goodsId);

    int queryCategoryIdById(@Param("id") Integer id);

    int queryBrandIdById(@Param("id") Integer id);

}
