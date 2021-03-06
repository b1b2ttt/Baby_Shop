package com.willa.babytun.scheduler;

import com.willa.babytun.entity.Goods;
import com.willa.babytun.service.GoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component //组件类，IOC容器扫描到后会自动实例化加载
public class StaticTask {
    @Resource
    private GoodsService goodsService;
    @Resource
    private Configuration freemarkerConfig;

    //秒 分 小时 日 月 星期
    //* 代表所有时间
    public void doStatic() throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate("goods.ftl");
        List<Goods> allGoods = goodsService.findLast5M();
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
            System.out.println(new Date());
            out.close();

        }
    }
}

