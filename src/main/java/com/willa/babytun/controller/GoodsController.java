package com.willa.babytun.controller;


import com.willa.babytun.entity.Evaluate;
import com.willa.babytun.entity.Goods;
import com.willa.babytun.service.GoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

import org.slf4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class  GoodsController {
    Logger logger = LoggerFactory.getLogger(GoodsController.class);
    @Value("${server.port}")
    private String port;

    @Resource
    private GoodsService goodsService;
    //freemarker的核心配置类，用于动态生成模板对象，
    // 在springboot ioc容器初始化的时候自动configuration对象就被实例化了
    @Resource
    private Configuration freemarkerConfig;
    @GetMapping("/goods") //http://localhost/goods?gid=xxx
    public ModelAndView showGoods(Long gid){
        logger.info("port:" + port);

        ModelAndView mav = new ModelAndView("/goods");
        Goods goods = goodsService.getGoods(gid);
        mav.addObject("goods", goods);
        mav.addObject("covers", goodsService.findCovers(gid));
        mav.addObject("details",goodsService.findDetails(gid));
        mav.addObject("params",goodsService.findParams(gid));
        return mav;
    }

    @GetMapping("/static/{gid}")
    @ResponseBody
    public String doStatic(@PathVariable("gid") Long gid) throws IOException, TemplateException {
        //获取模板对象
        Template template = freemarkerConfig.getTemplate("goods.ftl");
        Map param = new HashMap();
        param.put("goods", goodsService.getGoods(gid));
        param.put("covers", goodsService.findCovers(gid));
        param.put("details", goodsService.findDetails(gid));
        param.put("params", goodsService.findParams(gid));
        File targetFile = new File("/Users/willa/Downloads/babytun/goods/" +  gid + ".html");
        FileWriter out = new FileWriter(targetFile);
        template.process(param, out);
        out.close();
        return targetFile.getPath();
    }

    @GetMapping("/static_all")
    @ResponseBody
    public String doStatic() throws IOException, TemplateException {
        //获取模板对象
        Template template = freemarkerConfig.getTemplate("goods.ftl");
        List<Goods> allGoods = goodsService.findAllGoods();
        for(Goods g : allGoods) {
            Long gid = g.getGoodsId();
            Map param = new HashMap();
            param.put("goods", goodsService.getGoods(gid));
            param.put("covers", goodsService.findCovers(gid));
            param.put("details", goodsService.findDetails(gid));
            param.put("params", goodsService.findParams(gid));
            File targetFile = new File("/Users/willa/Downloads/babytun/goods/" +  gid + ".html");
            FileWriter out = new FileWriter(targetFile);
            template.process(param, out);
            out.close();
        }
        return "ok";
    }

    @GetMapping("/evaluate/{gid}")
    @ResponseBody
    public List<Evaluate> findEvaluates(@PathVariable("gid") Long goodsId) {
        return goodsService.findEvaluates(goodsId);
    }

    @GetMapping("/login")
    @ResponseBody
    public String longin(String u, WebRequest request) {
        request.setAttribute("user", u, WebRequest.SCOPE_REQUEST);
        return "port:" + port + ",login success";

    }

    @GetMapping("/check")
    @ResponseBody
    public String checkUser(WebRequest request) {
        String user = (String) request.getAttribute("user", WebRequest.SCOPE_REQUEST);
        if(user != null) {
            return "port:" + port + ",user=" + user;
        }else {
            return "port:" + port + ", redirect to login!";
        }
    }
}
