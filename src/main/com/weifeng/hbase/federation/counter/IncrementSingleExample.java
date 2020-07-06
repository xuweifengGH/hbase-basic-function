package com.weifeng.hbase.federation.counter;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseConfigurationFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class IncrementSingleExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "daily");
    Connection connection = ConnectionFactory.createConnection(hConf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    long cnt1 = table.incrementColumnValue(Bytes.toBytes("20110101"),
      Bytes.toBytes("daily"), Bytes.toBytes("hits"), 1);
    long cnt2 = table.incrementColumnValue(Bytes.toBytes("20110101"),
      Bytes.toBytes("daily"), Bytes.toBytes("hits"), 1);

    long current = table.incrementColumnValue(Bytes.toBytes("20110101"),
      Bytes.toBytes("daily"), Bytes.toBytes("hits"), 0);

    long cnt3 = table.incrementColumnValue(Bytes.toBytes("20110101"),
      Bytes.toBytes("daily"), Bytes.toBytes("hits"), -1);

    System.out.println("cnt1: " + cnt1 + ", cnt2: " + cnt2 +
      ", current: " + current + ", cnt3: " + cnt3);
  }
}
