package com.willa.babytun.controller.controller;

import com.willa.babytun.entity.Order;
import com.willa.babytun.service.PromotionSecKillService;
import com.willa.babytun.service.exception.SecKillsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SecKillController {
    @Resource
    PromotionSecKillService promotionSecKillService;
    @RequestMapping("/seckill")
    @ResponseBody

    public Map processSecKill(Long psid, String userid)  {
        Map result = new HashMap();
        try {
            promotionSecKillService.processSecKill(psid, userid, 1);
            String orderNo = promotionSecKillService.sendOrderToQueue(userid);
            Map data = new HashMap();
            data.put("orderNo", orderNo);
            result.put("code", "0"); //0代表操作成功
            result.put("message", "success");
            result.put("data", data);
        } catch (SecKillsException e) {
            result.put("code","500");
            result.put("message", e.getMessage());
            //e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/checkorder")
    public ModelAndView checkOrder(String orderNo) {
        Order order = promotionSecKillService.checkOrder(orderNo);
        ModelAndView mav = new ModelAndView();
        if(order != null) { //订单已经创建好了
            mav.addObject("order", order);
            mav.setViewName("/order");
        }else { //创建订单
            mav.addObject("order", orderNo);
            mav.setViewName("/wait.ftl");
        }
        return mav;
    }
}
