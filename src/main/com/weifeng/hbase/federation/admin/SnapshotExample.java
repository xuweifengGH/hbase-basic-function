package com.weifeng.hbase.federation.admin;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

public class SnapshotExample {

  public static void main(String[] args)
    throws IOException, InterruptedException {
    // 1. 获取Hyperbase集群的conf
    Configuration hConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
    hConf.setInt("hbase.client.retries.number", 1);

    HbaseUtil helper = HbaseUtil.getHelper(hConf);
    helper.dropTable("testtable");
    helper.dropTable("testtable2");
    helper.dropTable("testtable3");
    helper.createTable("testtable", 3, "colfam1", "colfam2");
    helper.put("testtable", new String[]{"row1"},
      new String[]{"colfam1", "colfam2"},
      new String[]{"qual1", "qual1", "qual2", "qual2", "qual3", "qual3"},
      new long[]{1, 2, 3, 4, 5, 6},
      new String[]{"val1", "val1", "val2", "val2", "val3", "val3"});
    System.out.println("Before snapshot calls...");
    helper.dump("testtable", new String[]{"row1"}, null, null);

    Connection connection = ConnectionFactory.createConnection(hConf);
    TableName tableName = TableName.valueOf("testtable");
    Table table = connection.getTable(tableName);
    Admin admin = connection.getAdmin();

    admin.deleteSnapshots("snapshot.*");

    admin.snapshot("snapshot1", tableName);

    List<HBaseProtos.SnapshotDescription> snaps = admin.listSnapshots();
    System.out.println("Snapshots after snapshot 1: " + snaps);

    Delete delete = new Delete(Bytes.toBytes("row1"));
    delete.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
    table.delete(delete);

    admin.snapshot("snapshot2", tableName,
      HBaseProtos.SnapshotDescription.Type.SKIPFLUSH);
    admin.snapshot("snapshot3", tableName,
      HBaseProtos.SnapshotDescription.Type.FLUSH);

    snaps = admin.listSnapshots();
    System.out.println("Snapshots after snapshot 2 & 3: " + snaps);

    Put put = new Put(Bytes.toBytes("row2"))
      .addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual10"),
        Bytes.toBytes("val10"));
    table.put(put);

    HBaseProtos.SnapshotDescription snapshotDescription =
      HBaseProtos.SnapshotDescription.newBuilder()
      .setName("snapshot4")
      .setTable(tableName.getNameAsString())
      .build();
    admin.takeSnapshotAsync(snapshotDescription);

    snaps = admin.listSnapshots();
    System.out.println("Snapshots before waiting: " + snaps);

    System.out.println("Waiting...");
    while (!admin.isSnapshotFinished(snapshotDescription)) {
      Thread.sleep(1 * 1000);
      System.out.print(".");
    }
    System.out.println();
    System.out.println("Snapshot completed.");
    snaps = admin.listSnapshots();
    System.out.println("Snapshots after waiting: " + snaps);

    System.out.println("Table before restoring snapshot 1");
    helper.dump("testtable", new String[]{"row1", "row2"}, null, null);

    admin.disableTable(tableName);
    admin.restoreSnapshot("snapshot1");
    admin.enableTable(tableName);

    System.out.println("Table after restoring snapshot 1");
    helper.dump("testtable", new String[]{"row1", "row2"}, null, null);

    admin.deleteSnapshot("snapshot1");
    snaps = admin.listSnapshots();
    System.out.println("Snapshots after deletion: " + snaps);

    admin.cloneSnapshot("snapshot2", TableName.valueOf("testtable2"));
    System.out.println("New table after cloning snapshot 2");
    helper.dump("testtable2", new String[]{"row1", "row2"}, null, null);
    admin.cloneSnapshot("snapshot3", TableName.valueOf("testtable3"));
    System.out.println("New table after cloning snapshot 3");
    helper.dump("testtable3", new String[]{"row1", "row2"}, null, null);

    table.close();
    connection.close();
    helper.close();
  }
}
