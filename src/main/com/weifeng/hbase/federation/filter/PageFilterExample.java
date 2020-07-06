package com.weifeng.hbase.federation.filter;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class PageFilterExample {

  private static final byte[] POSTFIX = new byte[] { 0x00 };

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    //sConfiguration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "colfam1");
    System.out.println("Adding rows to table...");
    helper.fillTable("testtable", 1, 100, 10, "colfam1");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    Filter filter = new PageFilter(15);

    int totalRows = 0;
    byte[] lastRow = null;
    while (true) {
      Scan scan = new Scan();
      scan.setFilter(filter);
      if (lastRow != null) {
        byte[] startRow = Bytes.add(lastRow, POSTFIX);
        System.out.println("start row: " +
          Bytes.toStringBinary(startRow));
        scan.setStartRow(startRow);
      }
      ResultScanner scanner = table.getScanner(scan);
      int localRows = 0;
      Result result;
      while ((result = scanner.next()) != null) {
        System.out.println(localRows++ + ": " + result);
        totalRows++;
        lastRow = result.getRow();
      }
      scanner.close();
      if (localRows == 0) break;
    }
    System.out.println("total rows: " + totalRows);

  }
}
