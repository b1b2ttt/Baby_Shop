package com.willa.babytun.dao;

import com.willa.babytun.entity.Goods;

import java.util.List;

public interface GoodsDAO {
    public Goods findById(Long goodsId);
    public List<Goods> findAll();
    public List<Goods> findLast5M();

}
