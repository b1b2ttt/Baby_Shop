package com.willa.babytun.service;

import com.willa.babytun.dao.OrderDAO;
import com.willa.babytun.dao.PromotionSecKillDAO;
import com.willa.babytun.entity.Order;
import com.willa.babytun.entity.PromotionSecKill;
import com.willa.babytun.service.exception.SecKillsException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.ref.PhantomReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PromotionSecKillService {
    @Resource
    private PromotionSecKillDAO promotionSecKillDAO;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource //rabbitMQ客户端
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderDAO orderDAO;
    /**
     * @return: nothing
     * @param psid
     * @param userid
     * @param i
     */
    public void processSecKill(Long psid, String userid, int i) throws SecKillsException {
        PromotionSecKill ps = promotionSecKillDAO.findById(psid);
        if(ps == null) {
            //秒杀活动不存在
            throw new SecKillsException("该商品秒杀活动不存在");
        }

        if(ps.getStatus() == 0) {
            throw new SecKillsException("秒杀活动还未开始");
        }else if(ps.getStatus() == 2) {
            throw new SecKillsException("秒杀活动已经结束");
        }

        Integer goodsId = (Integer)redisTemplate.opsForList().leftPop("seckill:count:" + ps.getPsId());
        if(goodsId != null) { //本次得到了抢购这件商品的权利
            //判断是否已经抢购过
            boolean isExisted = redisTemplate.opsForSet().isMember("seckill:users:" + ps.getPsId(), userid);
            if(!isExisted){
                System.out.println("恭喜您抢到商品了，快去下单吧");
                redisTemplate.opsForSet().add("seckill:users:" + ps.getPsId(), userid);
            }else{
                redisTemplate.opsForList().rightPush("seckill:count:" + ps.getPsId(), ps.getGoodsId());
                throw new SecKillsException("抱歉，您已经参加过此活动，请勿重复抢购");
            }
        }else {
            throw new SecKillsException("该商品已经卖完,下次再来吧!");
        }
    }

    public String sendOrderToQueue(String userid) {
        System.out.println("准备向队列发送信息");
        //订单基本信息
        Map data = new HashMap();
        data.put("userid", userid);
        String orderNo = UUID.randomUUID().toString();
        data.put("orderNo", orderNo);
        //附加额外的订单信息

        rabbitTemplate.convertAndSend("exchange-order", null, data);
        return orderNo;

    }

    public Order checkOrder(String orderNo) {
        Order order = orderDAO.findByOrderNo(orderNo);
        return order;
    }
}
