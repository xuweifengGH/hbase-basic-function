package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;

import java.io.IOException;

public class CreateTableWithRegionsExample {

  private static Configuration conf = null;
  private static Connection connection = null;

  private static void printTableRegions(String tableName) throws IOException {
    System.out.println("Printing regions of table: " + tableName);
    TableName tn = TableName.valueOf(tableName);
    RegionLocator locator = connection.getRegionLocator(tn);
    Pair<byte[][], byte[][]> pair = locator.getStartEndKeys();
    for (int n = 0; n < pair.getFirst().length; n++) {
      byte[] sk = pair.getFirst()[n];
      byte[] ek = pair.getSecond()[n];
      System.out.println("[" + (n + 1) + "]" +
        " start key: " +
        (sk.length == 8 ? Bytes.toLong(sk) : Bytes.toStringBinary(sk)) +
        ", end key: " +
        (ek.length == 8 ? Bytes.toLong(ek) : Bytes.toStringBinary(ek)));
    }
    locator.close();
  }

  public static void main(String[] args) throws IOException {
    // 1. 获取Hyperase集群的conf
    //conf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    conf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");
    connection = ConnectionFactory.createConnection(conf);
    HbaseUtil helper = HbaseUtil.getHelper(conf);
    helper.dropTable("testtable1");
    helper.dropTable("testtable2");
    Admin admin = connection.getAdmin();

    HTableDescriptor desc = new HTableDescriptor(
      TableName.valueOf("testtable1"));
    HColumnDescriptor coldef = new HColumnDescriptor(
      Bytes.toBytes("colfam1"));
    desc.addFamily(coldef);

    admin.createTable(desc, Bytes.toBytes(1L), Bytes.toBytes(100L), 10);
    printTableRegions("testtable1");

    byte[][] regions = new byte[][] {
      Bytes.toBytes("A"),
      Bytes.toBytes("D"),
      Bytes.toBytes("G"),
      Bytes.toBytes("K"),
      Bytes.toBytes("O"),
      Bytes.toBytes("T")
    };
    HTableDescriptor desc2 = new HTableDescriptor(
      TableName.valueOf("testtable2"));
    desc2.addFamily(coldef);
    admin.createTable(desc2, regions);
    printTableRegions("testtable2");
  }
}
