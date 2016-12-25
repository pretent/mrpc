spring-boot-starter-mrpc docs 

## 快速开始

### 安装

使用git下载或者下载zip编译安装

* git clone

```
$ git clone https://github.com/pretent/mrpc.git
$ cd mrpc
$ mvn clean install
```

* 下载zip

```
$ unzip master.zip
$ cd master
$ mvn clean install
```

### 添加maven 依赖
```
<dependency>
	<groupId>org.pretent.open</groupId>
	<artifactId>spring-boot-starter-mrpc</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
### 服务提供者
#### 配置注册中心和所要发布服务的包
application.properties
```
mrpc.register=zookeeper://192.168.34.132:2181
mrpc.packageName=impl
```
#### 定义接口
```
public interface UserService {
    public Person insert(Person person) throws Exception;
}
```
#### 接口实现
```
@Service
public class UserServiceImpl implements UserService {
    ...
}
```

### 服务消费者
#### 配置注册中心

application.properties
```
mrpc.register=zookeeper://192.168.34.132:2181
```
#### 注入服务
```
public class UserAction {
    @Reference
    private UserService userService;
    ...
}
```


感谢宁儿的大力支持和无私奉献
欢迎共同进步

E-Mail:353115817@qq.com

Blog:http://blog.csdn.net/pretent