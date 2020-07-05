package com.weifeng.hbase.federation.counter;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

public class IncrementMultipleExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "daily", "weekly");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    Increment increment1 = new Increment(Bytes.toBytes("20150101"));

    increment1.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("clicks"), 1);
    increment1.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("hits"), 1);
    increment1.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("clicks"), 10);
    increment1.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("hits"), 10);

    Map<byte[], NavigableMap<byte[], Long>> longs = increment1.getFamilyMapOfLongs();
    for (byte[] family : longs.keySet()) {
      System.out.println("Increment #1 - family: " + Bytes.toString(family));
      NavigableMap<byte[], Long> longcols = longs.get(family);
      for (byte[] column : longcols.keySet()) {
        System.out.print("  column: " + Bytes.toString(column));
        System.out.println(" - value: " + longcols.get(column));
      }
    }

    Result result1 = table.increment(increment1);

    for (Cell cell : result1.rawCells()) {
      System.out.println("Cell: " + cell +
        " Value: " + Bytes.toLong(cell.getValueArray(), cell.getValueOffset(),
        cell.getValueLength()));
    }

    Increment increment2 = new Increment(Bytes.toBytes("20150101"));

    increment2.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("clicks"), 5);
    increment2.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("hits"), 1);
    increment2.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("clicks"), 0);
    increment2.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("hits"), -5);

    Result result2 = table.increment(increment2);

    for (Cell cell : result2.rawCells()) {
      System.out.println("Cell: " + cell +
        " Value: " + Bytes.toLong(cell.getValueArray(),
          cell.getValueOffset(), cell.getValueLength()));
    }

    table.close();
    connection.close();
    helper.close();
  }
}
