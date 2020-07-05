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
import org.apache.hadoop.hbase.util.Pair;

import java.io.IOException;

public class ModifyTableExample {

  public static void main(String[] args) throws IOException, InterruptedException {
    // 1. 获取Hyperbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Admin admin = connection.getAdmin();
    TableName tableName = TableName.valueOf("testtable");
    HColumnDescriptor coldef1 = new HColumnDescriptor("colfam1");
    HTableDescriptor desc = new HTableDescriptor(tableName)
      .addFamily(coldef1)
      .setValue("Description", "ModifyTableExample: Original Table");

    admin.createTable(desc, Bytes.toBytes(1L), Bytes.toBytes(10000L), 50);

    HTableDescriptor htd1 = admin.getTableDescriptor(tableName);
    HColumnDescriptor coldef2 = new HColumnDescriptor("colfam2");
    htd1
      .addFamily(coldef2)
      .setMaxFileSize(1024 * 1024 * 1024L)
      .setValue("Description",
        "ModifyTableExample: Modified Table");

    admin.disableTable(tableName);
    admin.modifyTable(tableName, htd1);

    Pair<Integer, Integer> status = new Pair<Integer, Integer>() {{
      setFirst(50);
      setSecond(50);
    }};
    for (int i = 0; status.getFirst() != 0 && i < 500; i++) {
      status = admin.getAlterStatus(desc.getTableName());
      if (status.getSecond() != 0) {
        int pending = status.getSecond() - status.getFirst();
        System.out.println(pending + " of " + status.getSecond()
          + " regions updated.");
        Thread.sleep(1 * 1000l);
      } else {
        System.out.println("All regions updated.");
        break;
      }
    }
    if (status.getFirst() != 0) {
      throw new IOException("Failed to update regions after 500 seconds.");
    }

    admin.enableTable(tableName);

    HTableDescriptor htd2 = admin.getTableDescriptor(tableName);
    System.out.println("Equals: " + htd1.equals(htd2));
    System.out.println("New schema: " + htd2);
  }
}
