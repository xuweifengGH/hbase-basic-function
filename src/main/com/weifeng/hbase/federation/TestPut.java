package com.weifeng.hbase.federation;

import com.weifeng.hbase.federation.helper.HbaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class TestPut {
    public static void main(String[] args) {
        // 1. 获取hbase集群的conf
        Configuration hbaseConf = HBaseConfigurationFactory.getHbaseConfiguration("hyperbase1");
        //Configuration hyperbaseConf = HBaseConfigurationFactory.getHbaseConfiguration("hbase2");
        try {
            HbaseUtil helper = HbaseUtil.getHelper(hbaseConf);
            helper.dropTable("testtable");
            helper.createTable("testtable", "colfam1");
            //2.创建Connection
            Connection hbaseConnect = ConnectionFactory.createConnection(hbaseConf);
            //Connection hyperbaseConnect = ConnectionFactory.createConnection(hyperbaseConf);
            Table table = hbaseConnect.getTable(TableName.valueOf("testtable"));
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
            put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"), Bytes.toBytes("val2"));
            table.put(put);
            table.close();
            hbaseConnect.close();
            helper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
