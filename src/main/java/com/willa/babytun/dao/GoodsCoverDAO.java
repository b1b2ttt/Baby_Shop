package com.willa.babytun.dao;

import com.willa.babytun.entity.GoodsCover;

import java.util.List;

public interface GoodsCoverDAO {
    public List<GoodsCover> findByGoodsId(Long goodsId);
}
