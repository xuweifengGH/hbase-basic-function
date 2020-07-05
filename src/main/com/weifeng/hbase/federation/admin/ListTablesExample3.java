package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseConfigurationFactory;

import java.io.IOException;
import java.util.regex.Pattern;

public class ListTablesExample3 {

  private static void print(TableName[] tableNames) {
    for (TableName name : tableNames) {
      System.out.println(name);
    }
    System.out.println();
  }

  public static void main(String[] args) throws IOException {
    // 1. 获取Hyperbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropNamespace("testspace1", true);
    helper.dropNamespace("testspace2", true);
    helper.dropTable("testtable3");
    helper.createNamespace("testspace1");
    helper.createNamespace("testspace2");
    helper.createTable("testspace1:testtable1", "colfam1");
    helper.createTable("testspace2:testtable2", "colfam1");
    helper.createTable("testtable3", "colfam1");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Admin admin = connection.getAdmin();

    System.out.println("List: .*");
    TableName[] names = admin.listTableNames(".*");
    print(names);
    System.out.println("List: .*, including system tables");
    names = admin.listTableNames(".*", true);
    print(names);

    System.out.println("List: hbase:.*, including system tables");
    names = admin.listTableNames("hbase:.*", true);
    print(names);

    System.out.println("List: def.*:.*, including system tables");
    names = admin.listTableNames("def.*:.*", true);
    print(names);

    System.out.println("List: test.*");
    names = admin.listTableNames("test.*");
    print(names);

    System.out.println("List: .*2, using Pattern");
    Pattern pattern = Pattern.compile(".*2");
    names = admin.listTableNames(pattern);
    print(names);

    System.out.println("List by Namespace: testspace1");
    names = admin.listTableNamesByNamespace("testspace1");
    print(names);
  }
}
