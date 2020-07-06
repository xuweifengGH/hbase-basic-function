package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseConfigurationFactory;

import java.io.IOException;
import java.util.regex.Pattern;

public class ListTablesExample2 {

  private static void print(HTableDescriptor[] descriptors) {
    for (HTableDescriptor htd : descriptors) {
      System.out.println(htd.getTableName());
    }
    System.out.println();
  }

  public static void main(String[] args) throws IOException {
    // 1. 获取Hyperbase集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

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
    HTableDescriptor[] htds = admin.listTables(".*");
    print(htds);
    System.out.println("List: .*, including system tables");
    htds = admin.listTables(".*", true);
    print(htds);

    System.out.println("List: hbase:.*, including system tables");
    htds = admin.listTables("hbase:.*", true);
    print(htds);

    System.out.println("List: def.*:.*, including system tables");
    htds = admin.listTables("def.*:.*", true);
    print(htds);

    System.out.println("List: test.*");
    htds = admin.listTables("test.*");
    print(htds);

    System.out.println("List: .*2, using Pattern");
    Pattern pattern = Pattern.compile(".*2");
    htds = admin.listTables(pattern);
    print(htds);

    System.out.println("List by Namespace: testspace1");
    htds = admin.listTableDescriptorsByNamespace("testspace1");
    print(htds);
  }
}
