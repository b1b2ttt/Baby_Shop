package com.willa.babytun.dao;

import com.willa.babytun.entity.GoodsDetail;

import java.util.List;

public interface GoodsDetailDAO {
    public List<GoodsDetail> findByGoodsId(Long goodsId);
}
