package com.weifeng.hbase.federation.filter;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class QualifierFilterExample {

  public static void main(String[] args) throws IOException {
    // 1. 获取hbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "colfam1", "colfam2");
    System.out.println("Adding rows to table...");
    helper.fillTable("testtable", 1, 10, 10, "colfam1", "colfam2");

    Connection connection = ConnectionFactory.createConnection(hConf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    Filter filter = new QualifierFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("col-2")));

    Scan scan = new Scan();
    scan.setFilter(filter);
    ResultScanner scanner = table.getScanner(scan);

    System.out.println("Scanning table... ");

    for (Result result : scanner) {
      System.out.println(result);
    }
    scanner.close();

    Get get = new Get(Bytes.toBytes("row-5"));
    get.setFilter(filter);
    Result result = table.get(get);
    System.out.println("Result of get(): " + result);

  }
}
