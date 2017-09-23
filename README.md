# GAE - General Advertising Engine
GAE旨在创建一个**开箱即用**的通用广告投放引擎,只需要按规定格式灌输索引文件,即可直接上线使用,同时预留出开发者自定义的空间。
网络通讯层使用Netty,应用层SpringBoot。

## 功能
![function](http://ovbyjzegm.bkt.clouddn.com/GAE.png)

## 构建运行
```
mvn clean package
java -jar target/gae.jar --gae.server.port=9000 --gae.index.path=./ --gae.index.name=gae.idx
```
其中gae.idx为索引文件名

## 项目进度
基本完成(能跑通):
授权, 索引, 检索

未完成:
日志,前端反作弊

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
        "ip": "102.168.1.1",
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
                "ad_id": "idea1",
                "h": 1080,
                "land_url": "http://www.163.com",
                "material_type": 1,
                "slot_id": "广告位id",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            },
            {
                "ad_id": "idea1",
                "h": 1080,
                "land_url": "http://www.163.com",
                "material_type": 1,
                "slot_id": "广告位id2",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            }
        ],
        "request_id": "hello"
    }
}
```