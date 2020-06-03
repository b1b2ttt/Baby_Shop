package com.willa.babytun.dao;

import com.willa.babytun.entity.PromotionSecKill;

import java.util.List;

public interface PromotionSecKillDAO {
    public List<PromotionSecKill> findUnstartSecKill();
    void update(PromotionSecKill ps);
    PromotionSecKill findById(Long psId);
    List<PromotionSecKill> findExpireSecKill();
}
