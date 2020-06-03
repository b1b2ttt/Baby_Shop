# 基于SpringBoot秒杀模块
## 开发工具
idea 2020.1

## 开发环境
|  JDK | Maven| Mysql  |  SpringBoot |  redis | RabbitMQ  |
| ------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
|  1.8 |  3.6.3 |  5.7 |  2.1.14.RELEASE |  3.2 | 4.X  |

## Jmeter压力测试结果
![](https://github.com/b1b2ttt/images/blob/master/miaosha_1.png?raw=true)
### 结果显示：最长等待时间为11s.

------------


# 优化
##1.静态数据优化—redis缓存
###1.1对于稳定的数据，常用两种方式进行高并发处理：
- 利用缓存（Redis, Ehcache, Memcached...)
- 利用静态化技术（staticize)转化为html

### 1.2 Redis在高并发场景常用与缓存数据

#### 1.2.1 加入redis缓存

`@Cacheable(value = "covers", key = "#goodsId")`
![](https://github.com/b1b2ttt/images/blob/master/miaosha_3.png?raw=true)
#### 1.2.2 Jmeter压力测试结果

![](https://github.com/b1b2ttt/images/blob/master/miaosha_2.png?raw=true)

#### 1.2.3 结果分析：

最长响应时间为330MS，吞吐量为1061/s，原因是redis缓存中（db3)中已经缓存了商品信息，在程序运行的时候，所有的缓存都被加载，因此在实际使用时不再是直接读取数据库，而是从缓存中读取，进而提高了效率。
##2.稳定数据优化：静态化技术
### 什么是页面静态化
页面静态化指的是将动态的页面如jsp, freemarker等变成html静态网页。用web  server或者container直接访问html，提高速度。
![](https://github.com/b1b2ttt/images/blob/master/miaosha_4.png?raw=true)
### Nginx是一款轻量级的web服务器/反向代理服务器
使用Nginx对静态网页进行绑定

### Jmeter压测结果
![](https://github.com/b1b2ttt/images/blob/master/miaosha_html.png?raw=true)

吞吐量提高了！原因是不需要底层tomcat处理， 完全就是静态化页面

##3. 自动静态化处理
- 在实际开发中，不可能每次都手动的调用，进行页面静态化处理，变成自动任务比较好。

- 对于静态页面，如果针对少量的静态页面是没有问题的，但是静态页面特别多，要对页面进行静态化，将会特别耗时。

- 解决办法：spring中的任务调度模块

`@Scheduled(cron = "0 0/5 * * * ？") //每秒执行一次`


## 4.动静数据分离
### 问题：
页面静态化执行效率虽然高，但是在页面中也存在动态的数据，比如评论等，不能对这些数据进行静态化处理。这些动态数据需要在静态页面中使用ajax动态加载产生的数据。

![](https://github.com/b1b2ttt/images/blob/master/miaosha_6.png?raw=true)

###  解决办法：
对于静态数据生成在html中，对于动态数据，由前端向nginx发起ajax请求，再由nginx作为代理服务器，将请求转交给tomcat,当tomca获取到请求之后，从数据库中把数据提取出来，再由原路径回填到html页面。

## 5.解决秒杀超卖现象：使用redis解决超发问题

### 5.1 秒杀中的挑战
- 高并发，QPS峰值可到100W+。
- 避免超卖问题，如何避免购买商品人数不超过商品数量上限。

### 5.2 超卖产生的原因
![](https://github.com/b1b2ttt/images/blob/master/miaosha_%E8%B6%85%E5%8D%96.png?raw=true)

### 5.3 解决办法
#### 5.3.1 为什么选择redis?
- 单线程
- 内存存储，高达10W QPS
- 天然分布式支持

#### 5.3.2 解决超发的思路
![](https://github.com/b1b2ttt/images/blob/master/redis_%E8%B6%85%E5%8F%91.png?raw=true)

## 6.利用MQ进行订单流量削峰

### 6.1 为什么要削峰限流？

前台和后台处理业务处理能力不对称造成的，造成短时间的流量冲击。

![](https://github.com/b1b2ttt/images/blob/master/miaosha_%E6%B5%81%E9%87%8F%E5%86%B2%E5%87%BB.png?raw=true)

对于redis，处理用户确权效率是很高的，但是对于后台的支付订单，要与各类系统进行对接，处理压力比较大。

### 解决办法：

#### 利用RabbitMQ进行订单流量削峰
![](https://github.com/b1b2ttt/images/blob/master/miaosha_8_MQ.png?raw=true)

将用户确权的数据推入到MQ消息队列中，支付订单系统到MQ中进行数据的提取。

#### 程序处理思路：
![](https://github.com/b1b2ttt/images/blob/master/miaosha_9.png?raw=true)

## 7. Nginx负载均衡 Load Balance
### 7.1 负载均衡的分类：
软负载均衡：Nginx(http), Haproxy(tcp)
硬负载均衡：F5
### 7.2 Nignx六种负载均衡策略：
- Default:轮询策略
- Weighted:权重策略
- Least connected: 最少连接策略
- fair: 按响应时间(第三方)
- IP Hash: IP绑定策略
- url hash:按url分配策略(第三方)

实验：启动四个application来模拟四个服务器

### 7.3 Nignx 心跳检查机制
`server 127.0.0.1:8080 max_fals = 3 fail_timeout = 30s;`

max_fails 代表最大失败次数
fail_timeout代表单次链接超时时间

## 8. 设置Nginx集群Session共享
### Nginx环境下存在session不同步的问题：
因为默认情况下， session是存在某一台服务器的主机中的，这些数据很难在多台服务器中进行同步。
### 改进办法：
利用redis共享session,将session转存在dedis中实现session“共享访问”。
![](https://github.com/b1b2ttt/images/blob/master/miaosha_redis_session.png?raw=true)
### 代码实现：
#### spring session:
- spring session提供了管理用户session的API实现
- spring session可用于spring与springboot环境
- springboot提供了对应的starter集成
