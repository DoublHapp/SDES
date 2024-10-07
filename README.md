# SDES
简单的SDES算法实践
## 简单的加解密模块

## 暴力破解SDES

## GUI设计

# ATM Project
本项目是一次计算机网络课程实验，根据自定义协议[RFC20222022](https://shimo.im/docs/d1hLMvSAfjJ7uq9l )编码实现。主要实现ATM机与服务器之间的简单业务。
## 数据库依赖
本项目使用了Mysql数据库，并使用Java的[mysql-connector-j-8.3.0.jar](lib/mysql-connector-j-8.3.0.jar)进行数据库连接。
## 客户端实现
[Login.java](Client/Login.java)：该文件中定义并实现了登录的GUI界面\
[MainClass.java](Client/MainClass.java)：该文件中定义并实现了登录成功后的操作GUI界面\
[MessageHandle.java](Client/MessageHandle.java)：该文件中实现了对服务端报文进行处理的类和方法\
[TCP_Connection.java](Client/TCP_Connection.java)：该文件为程序入口，实现了与服务端的连接
## 服务器端实现
[DatabaseConnection.java](Server/DatabaseConnection.java)：该文件中实现了与MySQL数据库的连接\
[MainClass.java](Server/MainClass.java)：该文件为程序入口，并实现了服务端的逻辑框架\
[MessageHandle.java](Server/MessageHandle.java)：该文件实现了处理客户端报文的类和方法
