package com.weifeng.hbase.federation.crud;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class PutExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");
    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "colfam1");
    try {
      Connection connection = ConnectionFactory.createConnection(hConf);
      Table table = connection.getTable(TableName.valueOf("testtable"));
      Put put = new Put(Bytes.toBytes("row1"));
      put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
      put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"), Bytes.toBytes("val2"));
      table.put(put);

      table.close();
      connection.close();
      helper.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

