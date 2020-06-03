package com.willa.babytun.dao;

import com.willa.babytun.entity.Evaluate;

import java.util.List;

public interface EvaluateDAO {
    public List<Evaluate> findByGoodsId(Long goodsId);

}
