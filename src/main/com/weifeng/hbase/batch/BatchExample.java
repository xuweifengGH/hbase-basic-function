package com.weifeng.hbase.batch;

import com.weifeng.hbase.helper.HBaseHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchExample {

  private final static byte[] ROW1 = Bytes.toBytes("row1");
  private final static byte[] ROW2 = Bytes.toBytes("row2");
  private final static byte[] COLFAM1 = Bytes.toBytes("colfam1");
  private final static byte[] COLFAM2 = Bytes.toBytes("colfam2");
  private final static byte[] QUAL1 = Bytes.toBytes("qual1");
  private final static byte[] QUAL2 = Bytes.toBytes("qual2");

  public static void main(String[] args) throws IOException, InterruptedException {
    Configuration conf = HBaseConfiguration.create();

    HBaseHelper helper = HBaseHelper.getHelper(conf);
    helper.dropTable("testtable");
    helper.createTable("testtable", "colfam1", "colfam2");
    helper.put("testtable",
      new String[] { "row1" },
      new String[] { "colfam1" },
      new String[] { "qual1", "qual2", "qual3" },
      new long[] { 1, 2, 3 },
      new String[] { "val1", "val2", "val3" });
    System.out.println("Before batch call...");
    helper.dump("testtable", new String[] { "row1", "row2" }, null, null);

    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    List<Row> batch = new ArrayList<Row>();

    Put put = new Put(ROW2);
    put.addColumn(COLFAM2, QUAL1, 4, Bytes.toBytes("val5"));
    batch.add(put);

    Get get1 = new Get(ROW1);
    get1.addColumn(COLFAM1, QUAL1);
    batch.add(get1);

    Delete delete = new Delete(ROW1);
    delete.addColumns(COLFAM1, QUAL2);
    batch.add(delete);

    Get get2 = new Get(ROW2);
    get2.addFamily(Bytes.toBytes("BOGUS"));
    batch.add(get2);

    Object[] results = new Object[batch.size()];
    try {
      table.batch(batch, results);
    } catch (Exception e) {
      System.err.println("Error: " + e);
    }

    for (int i = 0; i < results.length; i++) {
      System.out.println("Result[" + i + "]: type = " +
        results[i].getClass().getSimpleName() + "; " + results[i]);
    }

    table.close();
    connection.close();
    System.out.println("After batch call...");
    helper.dump("testtable", new String[]{"row1", "row2"}, null, null);
    helper.close();
  }
}
