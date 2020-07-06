package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseConfigurationFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class CreateTableExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Admin admin = connection.getAdmin();

    TableName tableName = TableName.valueOf("testtable");
    HTableDescriptor desc = new HTableDescriptor(tableName);

    HColumnDescriptor coldef = new HColumnDescriptor(Bytes.toBytes("colfam1"));
    desc.addFamily(coldef);

    admin.createTable(desc);

    boolean avail = admin.isTableAvailable(tableName);
    System.out.println("Table available: " + avail);
  }
}
