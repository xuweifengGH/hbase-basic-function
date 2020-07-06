package com.weifeng.hbase.federation.crud;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class GetExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    if (!helper.existsTable("testtable")) {
      helper.createTable("testtable", "colfam1");
    }
    try {
      Connection connection = ConnectionFactory.createConnection(hConf);
      Table table = connection.getTable(TableName.valueOf("testtable"));
      Get get = new Get(Bytes.toBytes("row1"));
      get.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
      Result result = table.get(get);
      byte[] val = result.getValue(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));

      System.out.println("Value: " + Bytes.toString(val));

      table.close();
      connection.close();
      helper.close();
    }catch (IOException e) {
      e.printStackTrace();
    }

  }
}
