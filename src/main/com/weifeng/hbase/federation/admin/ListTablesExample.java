package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseConfigurationFactory;

import java.io.IOException;

public class ListTablesExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取Hyperbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable1");
    helper.dropTable("testtable2");
    helper.dropTable("testtable3");
    helper.createTable("testtable1", "colfam1", "colfam2", "colfam3");
    helper.createTable("testtable2", "colfam1", "colfam2", "colfam3");
    helper.createTable("testtable3", "colfam1", "colfam2", "colfam3");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Admin admin = connection.getAdmin();

    HTableDescriptor[] htds = admin.listTables();
    System.out.println("Printing all tables...");
    for (HTableDescriptor htd : htds) {
      System.out.println(htd);
    }

    HTableDescriptor htd1 = admin.getTableDescriptor(
      TableName.valueOf("testtable1"));
    System.out.println("Printing testtable1...");
    System.out.println(htd1);

    HTableDescriptor htd2 = admin.getTableDescriptor(
      TableName.valueOf("testtable10"));
    System.out.println("Printing testtable10...");
    System.out.println(htd2);
  }
}
