# GAE - General Advertising Engine
netty + SpringBoot

## 功能
![function](http://ovbyjzegm.bkt.clouddn.com/GAE.png)

## 构建运行
```
mvn clean package
java -jar target/gae.jar --gae.server.port=9000 --gae.index.path=./ --gae.index.name=gae.idx
```
其中gae.idx为索引文件名

## 测试
一次可请求多个广告位:
```
curl -X POST \
  http://127.0.0.1:9000/ \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -d '{
    "requestId": "hello",
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
            "slotId": "广告位id",
            "slotType": 1,
            "w": 1920,
            "h": 1080,
            "materialType": [1,2]
        },
        {
            "slotId": "广告位id2",
            "slotType": 1,
            "w": 1920,
            "h": 1080,
            "materialType": [1,2]
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
                "adId": "idea2",
                "h": 1080,
                "landUrl": "http://www.126.com",
                "materialType": 2,
                "slotId": "广告位id",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            },
            {
                "adId": "idea2",
                "h": 1080,
                "landUrl": "http://www.126.com",
                "materialType": 2,
                "slotId": "广告位id2",
                "url": "http://www.baidu.com/xxx.jpg",
                "w": 1920
            }
        ],
        "requestId": "hello"
    }
}
```