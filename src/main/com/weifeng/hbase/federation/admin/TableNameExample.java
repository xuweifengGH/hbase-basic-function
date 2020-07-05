package com.weifeng.hbase.federation.admin;

import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class TableNameExample {

  private static void print(String tablename) {
    print(null, tablename);
  }

  private static void print(String namespace, String tablename) {
    System.out.print("Given Namespace: " + namespace +
      ", Tablename: " + tablename + " -> ");
    try {
      System.out.println(namespace != null ?
        TableName.valueOf(namespace, tablename) :
        TableName.valueOf(tablename));
    } catch (Exception e) {
      System.out.println(e.getClass().getSimpleName() +
        ": " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    print("testtable");
    print("testspace:testtable");
    print("testspace", "testtable");
    print("testspace", "te_st-ta.ble");
    print("", "TestTable-100");
    print("tEsTsPaCe", "te_st-table");

    print("");

    print(".testtable");
    print("te_st-space", "te_st-table");
    print("tEsTsPaCe", "te_st-table@dev");
  }
}
