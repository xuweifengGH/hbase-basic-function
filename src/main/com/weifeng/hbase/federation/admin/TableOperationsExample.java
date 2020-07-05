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

public class TableOperationsExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取Hyperbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Admin admin = connection.getAdmin();

    TableName tableName = TableName.valueOf("testtable");
    HTableDescriptor desc = new HTableDescriptor(tableName);
    HColumnDescriptor coldef = new HColumnDescriptor(
      Bytes.toBytes("colfam1"));
    desc.addFamily(coldef);
    System.out.println("Creating table...");
    admin.createTable(desc);

    System.out.println("Deleting enabled table...");
    try {
      admin.deleteTable(tableName);
    } catch (IOException e) {
      System.err.println("Error deleting table: " + e.getMessage());
    }

    System.out.println("Disabling table...");
    admin.disableTable(tableName);
    boolean isDisabled = admin.isTableDisabled(tableName);
    System.out.println("Table is disabled: " + isDisabled);

    boolean avail1 = admin.isTableAvailable(tableName);
    System.out.println("Table available: " + avail1);

    System.out.println("Deleting disabled table...");
    admin.deleteTable(tableName);

    boolean avail2 = admin.isTableAvailable(tableName);
    System.out.println("Table available: " + avail2);

    System.out.println("Creating table again...");
    admin.createTable(desc);
    boolean isEnabled = admin.isTableEnabled(tableName);
    System.out.println("Table is enabled: " + isEnabled);
  }
}
