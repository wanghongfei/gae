[![Build Status](https://travis-ci.org/OpenAdv/gae.svg?branch=master)](https://travis-ci.org/OpenAdv/gae)
# GAE-DSP - General Advertising Engine

GAE是[Advertising Develop Kit](https://github.com/ad-dev-kit)中的广告投放引擎，业务模型适合于DSP(需求方平台)。



技术层面上，GAE的设计语言虽然是Java, 但架构上尽量做到轻量化，其轻量化体现在非必要时**不进行过度工程化(over engineering)**、使用HTTP协议但**摒弃Servlet**、使用Spring但**摒弃SpringMVC**。最终GAE使用了Vert.x + SpringBoot。使用vert.x是因为它是Java生态中性能突出([Techempower Benchmark](https://www.techempower.com/benchmarks/#section=data-r14&hw=ph&test=db&l=8vn05b))且编程模型简单的异步框架，使用SpringBoot则是为了利用其 all-in-one 的可执行jar和灵活的配置文件等特性方便部署。



业务层面上，GAE旨在创建一个**开箱即用**的通用广告投放引擎,只需要按规定格式灌输索引文件即可直接上线使用,且具备基本的定向功能。GAE中所有的创意都需要进行外审，即媒体方审核通过后才能正常投放。可与数据传输系统[GAE-DAS](https://github.com/wanghongfei/gae-das)配合使用, 通过监听mysql binlog来自动生成投放引擎所需的增量索引数据。



## 通过Docker运行

拉取镜像:

```dockerfile
docker pull registry.cn-beijing.aliyuncs.com/open-adv/gae:1.2
```

启动容器:

```dockerfile
docker run -p 9000:9000 -d f2f2df6361e4 /opt/gae/start.sh
```

此时可通过`9000`端口访问容器中的服务, 镜像中默认已经有1.5w条随机生成的广告索引。

一次可请求多个广告位:

```
curl -X POST \
  http://127.0.0.1:9000/ \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -d '{
    "request_id": "hello",
    "auth": {
        "tid": "tid",
        "token": "token"
    },
    "device": {
        "ip": "61.135.169.78",
        "mac": "AA:BB:CC:DD:EE:FF",
        "id": "IDFA",
        "type": 1
    },
    "slots": [
        {
            "slot_id": "广告位id",
            "slot_type": 1,
            "w": 1920,
            "h": 1080,
            "material_type": [1,2]
        },
        {
            "slot_id": "广告位id2",
            "slot_type": 1,
            "w": 1920,
            "h": 1080,
            "material_type": [1,2]
        }
    ]
}'
```

响应:

```
{
    "code": 0,
    "result": {
        "ads": [
            {
                "ad_code": "adCode1",
                "h": 1080,
                "land_url": "http://www.163.com",
                "material_type": 1,
                "show_urls": [
                    "http://www.gae.com/showMonitor.gif?sid=a",
                    "http://www.gae.com/showMonitor.gif?sid=b"
                ],
                "slot_id": "广告位id",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            },
            {
                "ad_code": "adCode1",
                "h": 1080,
                "land_url": "http://www.163.com",
                "material_type": 1,
                "show_urls": [
                    "http://www.gae.com/showMonitor.gif?sid=a",
                    "http://www.gae.com/showMonitor.gif?sid=b"
                ],
                "slot_id": "广告位id2",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            }
        ],
        "request_id": "hello"
    }
}
```

>  可以通过`IndexGenerator.genIndex()`方法随机生成全量索引文件进行测试



## 关于定向

- 地域定向

GAE通过请求参数中的IP字段实现按地域匹配广告功能,**需要下载ip字典**
- 人群标签定向

GAE支持人群标签定向,但标签的获取需要自己实现(如可以调DMP服务, 已预留出接口)。标签包括`type`和`id`两个属性, 分别表示标签类型(如年龄性别)和标签id。
GAE并没有规定必须用哪些类型的标, 只负责通过标签进行触发和过虑广告。



## 关于索引
索引分成两类，**全量索引**和**增量索引**. GAE在启动时一次性读取全部全量索引, 在运行期间会监控增量索引并实时加载更新.

全量索引只能从文件中读取，增量索引可以从文件也可以从kafka中读取. 如果从文件中读取增量, 则先会从第0个文件开始读取, 当下一个文件出现时自动切换至新文件。
例如,只有`gae.idx.incr.0`存在时则会一直监控、读取该文件，当`gae.idx.incr.1`出现时会切换从新文件开始读取。



目前全部索引数据都是加载到内存当中的，如果索引数据过大，可以考虑将索引分片存储或者直接扔到Redis里去。后续会实现上述功能。



## 功能
![function](http://ovbyjzegm.bkt.clouddn.com/gae-route2.png)

## 线程模型

vert.x版(Now)：

![vertx](http://ovbyjzegm.bkt.clouddn.com/vertx.png)



Netty版(old)：

![thread](http://ovbyjzegm.bkt.clouddn.com/thread.png)

其中业务线程池的大小和堵塞队列的配置对GAE整体吞吐起着决定性作用。



## 业务流程

![biz](http://ovbyjzegm.bkt.clouddn.com/biz.png)



## 构建运行

构建：

```
mvn clean package -Dmaven.test.skip=true
```

GAE运行有几项重要配置，分别为

- IP字典路径 gae.dict.ip
- 全量索引所在目录 gae.index.file.path
- 全量索引文件名 gae.index.file.name
- 增量索引所在目录 gae.index.incr-path
- 增量索引文件名 gae.index.incr-name



### 下载IP字典

```
wget http://ovbyjzegm.bkt.clouddn.com/ipdict.tar.gz
tar -zxvf ipdict.tar.gz
```


### 运行

GAE的增量索引加载有两种方式，监控索引文件增量或从kafka中读取. 当从文件中加载时, 需在运行时打开kafka开关(kafka连接配置详见`application.yaml`)，下面kafka配置示例：

```
java -jar target/gae.jar \
--gae.server.port=9000 \
--gae.index.kafka=true \
--gae.index.file.path=./ \
--gae.index.file.name=gae.idx \
--gae.dict.ip=IP字典文件名
```
其中`--gae-index.kafka`控制是否从kafka中读取增量索引，当指定从kafka读取时不需要指定增量文件相关配置。

地域ID与城市名称的对应关系见: `wget http://ovbyjzegm.bkt.clouddn.com/reg.txt`

当从文件中加载增量索引时, 则不需要打开kafka开关(无需指定`--gae.index.kafka=true`), 默认为关闭，但必须指定`gae.index.incr-path`和`gae.index.incr-name`


## 模块说明 org.fh.gae.*
- net

HTTP网络通讯逻辑, vertx-web启动入口`net.GaeHttpServer`

- query

广告检索逻辑


## 测试

