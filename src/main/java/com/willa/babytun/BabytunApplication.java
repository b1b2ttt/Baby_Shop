package com.willa.babytun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.willa.babytun") //mybatis在SpringBoot启动的时候自动扫描mybatis实现的接口
//@EnableCaching //开启声明书缓存，利用注解来控制缓存的读写
@EnableScheduling //自动任务调度
public class BabytunApplication {
	//修改默认的redisTemplate持久化方式
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		//设置value的序列化方式为json
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		//设置key的序列化方式为string
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(BabytunApplication.class, args);
	}

}
