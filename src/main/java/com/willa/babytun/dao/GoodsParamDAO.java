package com.willa.babytun.dao;

import com.willa.babytun.entity.GoodsDetail;
import com.willa.babytun.entity.GoodsParam;

import java.util.List;

public interface GoodsParamDAO {
    public List<GoodsParam> findByGoodsId(Long goodsId);
}
