package com.weifeng.hbase.federation;

import com.weifeng.hbase.helper.HBaseHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class TestGet {
    public static void main(String[] args) {
        // 1. 获取hbase集群的conf
        Configuration hbaseConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase1");
        //Configuration hyperbaseConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase2");
        try {
            HBaseHelper helper = HBaseHelper.getHelper(hbaseConf);
            if (!helper.existsTable("testtable")) {
                helper.createTable("testtable", "colfam1");
            }
            //2.创建Connection
            Connection hbaseConnect = ConnectionFactory.createConnection(hbaseConf);
            //Connection hyperbaseConnect = ConnectionFactory.createConnection(hyperbaseConf);
            Table table = hbaseConnect.getTable(TableName.valueOf("testtable"));
            Get get = new Get(Bytes.toBytes("row1"));
            get.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
            Result result = table.get(get);
            byte[] val = result.getValue(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"));
            System.out.println("Value: " + Bytes.toString(val));
            table.close();
            hbaseConnect.close();
            helper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
