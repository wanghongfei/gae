# GAE - General Advertising Engine
netty + SpringBoot

## 功能
![function](http://ovbyjzegm.bkt.clouddn.com/GAE.png)

## 构建运行
```
mvn clean package
java -jar target/gae.jar --gae.server.port=9000
```

## 测试
```
curl -X POST \
  http://127.0.0.1:9000/ \
  -H 'content-type: application/json' \
  -d '{
	"requestId": "hello",
	"auth": {
		"tid": "test",
		"token": "test"
	}
}'
```