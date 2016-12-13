mrpc-support-spring docs 

## 快速开始

### 添加maven 依赖
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
### 服务提供者
#### 配置注册中心和所要发布服务的包
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
#### 注入服务
```
public class UserAction {
    @Reference
    private UserService userService;
    ...
}
```
## 配置
### 服务提供者
#### 支持使用@Service注解和service xml 配置
##### @Service注解
指定扫描的包,类需要加@Service注解
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mrpc="http://blog.csdn.net/pretent/schema/mrpc"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd
    http://blog.csdn.net/pretent/schema/mrpc
    http://blog.csdn.net/pretent/schema/mrpc.xsd">

    <mrpc:register address="zookeeper://192.168.34.132:2181"/>

    <mrpc:annotation package="org.pretent.server.interfaces.impl"/>

    <context:component-scan base-package="org.pretent.server.interfaces.impl" />
</beans>
```

##### xml service标签配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mrpc="http://blog.csdn.net/pretent/schema/mrpc"
       xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd
    http://blog.csdn.net/pretent/schema/mrpc
    http://blog.csdn.net/pretent/schema/mrpc.xsd">

    <mrpc:register address="zookeeper://192.168.34.132:2181"/>

    <mrpc:service interface="org.pretent.server.interfaces.UserService" ref="userService"/>

    <mrpc:service interface="org.pretent.server.interfaces.OrderService" ref="orderService"/>

    <!-- 扫描注解spring组件,或者bean标签配置的组件 -->
    <!--
    <context:component-scan base-package="org.pretent.server.interfaces.impl" />
    -->

    <bean id="orderService" class="org.pretent.server.interfaces.impl.OrderServiceImpl"/>

    <bean id="userService" class="org.pretent.server.interfaces.impl.UserServiceImpl"/>

</beans>

```
### 服务消费者
#### 支持使用@Reference注解和reference xml标签
##### @Reference注解
```
public class UserAction {
    // 注解也支持放在setter方法上
    @Reference
    private UserService userService;
    ...
}
```
##### reference xml标签配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mrpc="http://blog.csdn.net/pretent/schema/mrpc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans-2.5.xsd 
	http://blog.csdn.net/pretent/schema/mrpc 
	http://blog.csdn.net/pretent/schema/mrpc.xsd">

	<mrpc:register address="zookeeper://127.0.0.1:2181"/>
	
	<mrpc:reference id="userService" interface="server.UserService"/>
	
	<!-- 为UserAction注入服务 -->
	<bean id="userAction" class="client.UserAction" >
		<property name="userService" ref="userService" />
	</bean>
</beans>  
```


感谢宁儿的大力支持和无私奉献
欢迎共同进步

E-Mail:353115817@qq.com

Blog:http://blog.csdn.net/pretent