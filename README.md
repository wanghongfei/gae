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
```
curl -X POST \
  http://127.0.0.1:9000/ \
  -H 'content-type: application/json' \
  -d '{
	"requestId": "hello",
	"auth": {
		"tid": "tid",
		"token": "token"
	},
	
	"slots": [
		{
			"slotId": "广告位id",
			"slotType": 1,
			"w": 1920,
			"h": 1080
		},
		{
			"slotId": "广告位id2",
			"slotType": 1,
			"w": 1920,
			"h": 1080
		}
	]
}'
```