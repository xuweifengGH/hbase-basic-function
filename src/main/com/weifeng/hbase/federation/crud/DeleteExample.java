package com.weifeng.hbase.federation.crud;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class DeleteExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hyperbase1集群的conf
    //Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", 100, "colfam1", "colfam2");
    helper.put("testtable",
      new String[] { "row1" },
      new String[] { "colfam1", "colfam2" },
      new String[] { "qual1", "qual1", "qual2", "qual2", "qual3", "qual3" },
      new long[]   { 1, 2, 3, 4, 5, 6 },
      new String[] { "val1", "val1", "val2", "val2", "val3", "val3" });
    System.out.println("Before delete call...");
    helper.dump("testtable", new String[]{ "row1" }, null, null);

    Connection connection = ConnectionFactory.createConnection(hConf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    Delete delete = new Delete(Bytes.toBytes("row1"));

    delete.setTimestamp(1);

    delete.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
    delete.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual3"), 3);

    delete.addColumns(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
    delete.addColumns(Bytes.toBytes("colfam1"), Bytes.toBytes("qual3"), 2);

    delete.addFamily(Bytes.toBytes("colfam1"));
    delete.addFamily(Bytes.toBytes("colfam1"), 3);

    table.delete(delete);

    table.close();
    connection.close();
    System.out.println("After delete call...");
    helper.dump("testtable", new String[] { "row1" }, null, null);
    helper.close();
  }
}
