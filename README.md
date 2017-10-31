# GAE-DSP - General Advertising Engine
GAE旨在创建一个**开箱即用**的通用广告投放引擎,业务模型适用于DSP平台,只需要按规定格式灌输索引文件即可直接上线使用,且具备基本的定向功能。
网络通讯层使用Netty,应用层SpringBoot。

# 关于定向
- 地域定向
GAE通过请求参数中的IP字段实现按地域匹配广告功能,**需要下载ip字典**
- 人群标签定向
GAE支持人群标签定向,但标签的获取需要自己现(已预留出接口),GAE只负责通过标签进行触发和过虑广告。

## 功能
![function](http://ovbyjzegm.bkt.clouddn.com/GAE.png)

## 构建运行
### 下载IP字典
```
wget http://ovbyjzegm.bkt.clouddn.com/ipdict.tar.gz
tar -zxvf ipdict.tar.gz
```
### 运行
```
mvn clean package
java -jar target/gae.jar --gae.server.port=9000 --gae.index.path=./ --gae.index.name=gae.idx --gae.dict.ip=IP字典文件名
```
其中gae.idx为索引文件名. 地域ID与城市名称的对应关系见: `wget http://ovbyjzegm.bkt.clouddn.com/reg.txt`

## 项目进度
基本完成:
授权, 索引, 检索, 日志(proto格式)

未完成:
前端反作弊

## 模块说明 org.fh.gae.*
- net

HTTP网络通讯逻辑, netty启动入口`net.GaeHttpServer`

- query

广告检索逻辑



## 测试
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