## 描述

1.本工程为针对Java api的hbase基础功能测试。JDK版本使用1,8,jar包为开源hbase-client包，hbase-server包，以及hbase-federation-1.0-SNAPSHOT.jar包。hbase-client包和hbase-server包使用maven管理添加，hbase-federation-1.0-SNAPSHOT.jar包需手动添加至工程中,jar包放在工程federation-jar文件夹下
## 注意点

1.hbase-cluster-site.xml必须直接放在resource下面，不能放在hyperase1/hbase下面

2.value标签可以指定绝对路径或者相对路径；如果用相对路径，注意src前面不应该有/

绝对路径方式参考如下：
```
<property>
    <name>hbase.cluster.configuration.hyperbase1</name>
    <value>/Users/xuweifeng/weifeng/git/hbase-basic-function/src/resources/hyperbase1</value>
</property>
<property>
    <name>hbase.cluster.configuration.hbase</name>
    <value>/Users/xuweifeng/weifeng/git/hbase-basic-function/src/resources/hbase</value>
</property>

```
相对路径方式参考如下：
```
<property>
    <name>hbase.cluster.configuration.hyperbase1</name>
    <value>src/resources/hyperbase1</value>
</property>
<property>
    <name>hbase.cluster.configuration.hbase</name>
    <value>src/resources/hbase</value>
</property>
```
3.配置文件中使用hostname，需本地配置hosts文件，例如添加：
```

172.26.4.29 linu-4-29
172.26.4.30 linu-4-30
172.26.4.33 linu-4-33
```
