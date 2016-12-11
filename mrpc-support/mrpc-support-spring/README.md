# mrpc-support-spring docs

## 添加maven 依赖
```
<dependency>
	<groupId>org.pretent.open</groupId>
	<artifactId>mrpc-core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
	<groupId>org.pretent.open</groupId>
	<artifactId>mrpc-core-support</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
## 服务提供者
### 配置注册中心和所要发布服务的包
```
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mrpc="http://blog.csdn.net/pretent/schema/mrpc"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://blog.csdn.net/pretent/schema/mrpc
    http://blog.csdn.net/pretent/schema/mrpc.xsd">

    <!-- 配置注册中心 -->
    <mrpc:register address="zookeeper://127.0.0.1:2181"/>
    
    <!-- 此包下发布服务,需要@Service注解-->
    <mrpc:annotation package="org.pretent.server.interfaces.impl"/>
</beans>
```
### 定义接口
```
public interface UserService {
    public Person insert(Person person) throws Exception;
}
```
### 接口实现
```
@Service
public class UserServiceImpl implements UserService {
    ...
}
```

## 服务消费者
### 配置注册中心

```
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mrpc="http://blog.csdn.net/pretent/schema/mrpc"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://blog.csdn.net/pretent/schema/mrpc
    http://blog.csdn.net/pretent/schema/mrpc.xsd">

    <!-- 配置注册中心 -->
    <mrpc:register address="zookeeper://127.0.0.1:2181"/>
</beans>
```
### 注入服务
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