package chiyue.learning.aliyun.datahub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.datahub.DatahubClient;
import com.aliyun.datahub.DatahubConfiguration;
import com.aliyun.datahub.auth.AliyunAccount;
import com.aliyun.datahub.common.data.Field;
import com.aliyun.datahub.common.data.FieldType;
import com.aliyun.datahub.common.data.RecordSchema;
import com.aliyun.datahub.common.data.RecordType;
import com.aliyun.datahub.exception.DatahubClientException;
import com.aliyun.datahub.exception.InvalidCursorException;
import com.aliyun.datahub.model.AppendDataConnectorFieldRequest;
import com.aliyun.datahub.model.AppendFieldRequest;
import com.aliyun.datahub.model.BlobRecordEntry;
import com.aliyun.datahub.model.ConnectorType;
import com.aliyun.datahub.model.DatabaseDesc;
import com.aliyun.datahub.model.GetCursorRequest.CursorType;
import com.aliyun.datahub.model.GetCursorResult;
import com.aliyun.datahub.model.GetDataConnectorShardStatusResult;
import com.aliyun.datahub.model.GetRecordsResult;
import com.aliyun.datahub.model.ListShardResult;
import com.aliyun.datahub.model.OdpsDesc;
import com.aliyun.datahub.model.PutBlobRecordsResult;
import com.aliyun.datahub.model.PutRecordsResult;
import com.aliyun.datahub.model.RecordEntry;

public class DataHubClient {

	//初始化AliyunAccount与DatahubConfiguration
	String accessId = "swhkzbwwnuvx9XS2";
	String accessKey = "HIGJO0fRtx8EGH0R6IX57EZhGyEOWI";
	String endpoint = "https://dh-cn-shenzhen.aliyuncs.com";
	
	AliyunAccount account = new AliyunAccount(accessId, accessKey);
	DatahubConfiguration conf = new DatahubConfiguration(account, endpoint);

	public DatahubClient connector() {

		//初始化DataHubClient, DataHub服务所有操作均可用该client进行
		DatahubClient client = new DatahubClient(conf);
		
		return client;
	}
	
	public void createTopic(String projectName, String topicName, String topicDesc) {

		DatahubClient client = connector();
		
		//创建Tuple Topic
		RecordSchema schema = new RecordSchema();
		
		schema.addField(new Field("id", FieldType.STRING));
		schema.addField(new Field("name", FieldType.STRING));
		schema.addField(new Field("desc", FieldType.STRING));
		int shardCount = 5;
		int lifeCycle = 3;

		client.createTopic(projectName, topicName, shardCount, lifeCycle, RecordType.TUPLE, schema, topicDesc);
		//等待服务端通道打开
		client.waitForShardReady(projectName, topicName);
	}
	
	public ListShardResult getShardList(String projectName, String topicName) {
		
		DatahubClient client = connector();
		
		//获取Shard列表
		ListShardResult listShardResult = client.listShard(projectName, topicName);
		
		return listShardResult;
	}
	
	public PutRecordsResult writeTupleData(String projectName, String topicName, RecordEntry... recordEntry) {
		
		List<RecordEntry> recordEntries = Arrays.asList(recordEntry);
		
		DatahubClient client = connector();
		
		PutRecordsResult result = client.putRecords(projectName, topicName, recordEntries);

		if(result.getFailedRecordCount() != 0) {
			
			return result;
		}
		
		return null;
	}
	
	public List<RecordEntry> readTupleData(String projectName, String topicName, String shardId, CursorType type){
		
		DatahubClient client = connector();
		
		GetCursorResult cursorRes = client.getCursor(projectName, topicName, shardId, type);
		//GetCursorResult cursorRs = client.getCursor(projectName, topicName, shardId, System.currentTimeMillis() - 24 * 3600 * 1000 /* ms */); 可以获取到24小时内的第一条数据Cursor
		
		int count = 10;
		int limit = 100;
		String cursor = cursorRes.getCursor();

		List<RecordEntry> recordEntries = new LinkedList<>();

		RecordSchema schema = new RecordSchema();
		schema.addField(new Field("id", FieldType.STRING));
		schema.addField(new Field("name", FieldType.STRING));
		schema.addField(new Field("desc", FieldType.STRING));
		
		while(count-- > 0) {
			
			try {
				GetRecordsResult recordRes = client.getRecords(projectName, topicName, shardId, cursor, limit, schema);
				
				if(recordRes.getRecordCount() == 0) {
					//无最新数据
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						
					}
				}else {
					recordEntries.addAll(recordRes.getRecords());
				}
				
				cursor = recordRes.getNextCursor();
			} catch (InvalidCursorException e) {

		        // 非法游标或游标已过期，建议重新定位后开始消费
				cursorRes = client.getCursor(projectName, topicName, shardId, type);
				cursor = cursorRes.getCursor();
				
			} catch (DatahubClientException e) {
		        // 发生异常，需要重试
		        System.out.printf(e.getMessage());
		        e.printStackTrace();
			}
			
		}
		
		return recordEntries;
	}
	
	public void addColumn(String projectName, String topicName, Field field, boolean hasConnector) {
		DatahubClient client = connector();
		// 修改Datahub schema
		client.appendField(new AppendFieldRequest(projectName, topicName, field));
		
		if(hasConnector) {
			// 如果存在Connecotr，则需要调用如下接口
			client.appendDataConnectorField(new AppendDataConnectorFieldRequest(projectName, topicName, ConnectorType.SINK_ODPS, field.getName()));
		}
	}
	
	public PutBlobRecordsResult putBlobRecord(String projectName, String topicName, int shardCount, int lifeCycle, RecordType recordType, String desc, String data) {
		DatahubClient client = connector();
		
//		client.createTopic(projectName, topicName, shardCount, lifeCycle, recordType, desc);
		
		client.waitForShardReady(projectName, topicName);
		
		List<BlobRecordEntry> recordEntries = new ArrayList<>();
		
		String shardId = getShardList(projectName, topicName).getShards().get(0).getShardId();
		
		BlobRecordEntry entry = new BlobRecordEntry();
		entry.setData(data.getBytes());
		entry.setShardId(shardId);
		recordEntries.add(entry);
		
		PutBlobRecordsResult result = client.putBlobRecords(projectName, topicName, recordEntries);
		
		System.out.println("put result: "+ JSONObject.toJSONString(result));
		
		return result;
	}
	
	
	
	
	
	public void createDataConnector (String projectName, String topicName) {
	    // Create SinkOdps DataConnector
	    // ODPS相关配置设置
	    String odpsProject = "datahub_test";
	    String odpsTable = "test_table";
	    String odpsEndpoint = "http://service-all.ext.odps.aliyun-inc.com/api";
	    String tunnelEndpoint = "http://dt-all.ext.odps.aliyun-inc.com";
	    OdpsDesc odpsDesc = new OdpsDesc();
	    odpsDesc.setProject(odpsProject);
	    odpsDesc.setTable(odpsTable);
	    odpsDesc.setOdpsEndpoint(odpsEndpoint);
	    odpsDesc.setTunnelEndpoint(tunnelEndpoint);
	    odpsDesc.setAccessId(accessId);
	    odpsDesc.setAccessKey(accessKey);
	    odpsDesc.setPartitionMode(OdpsDesc.PartitionMode.USER_DEFINE);
	    // 顺序选中topic中部分列或全部列 同步到odps，未选中的列将不会同步
	    List<String> columnFields = new ArrayList<String>();
	    columnFields.add("f1");
	    // 默认是使用UserDefine 的分区模式，具体参见文档[https://help.aliyun.com/document_detail/47453.html?spm=5176.product53345.6.555.MpixiB]
	    // 如果需要使用SYSTEM_TIME或EVENT_TIME模式，需要如下设置
	    // 对于EVENT_TIME需要在schema中增加一个字段：
	    // "event_time"，类型是TIMESTAMP
	    // begin
	    int timeRange = 15;  // 分钟，分区时间间隔，最小15分钟
	    odpsDesc.setPartitionMode(OdpsDesc.PartitionMode.SYSTEM_TIME);
	    odpsDesc.setTimeRange(timeRange);
	    Map<String, String> partitionConfig = new LinkedHashMap<String, String>();
	    //目前仅支持 %Y%m%d%H%M 的组合，任意多级分区
	    partitionConfig.put("pt", "%Y%m%d");
	    partitionConfig.put("ct", "%H%M");
	    odpsDesc.setPartitionConfig(partitionConfig);
	    // end
	    
	    DatahubClient client = connector();
	    
	    client.createDataConnector(projectName, topicName, ConnectorType.SINK_ODPS, columnFields, odpsDesc);
	    // 特殊需求下可以间歇性 如每15分钟获取Connector状态查看是否有异常,遍历所有shard
	    String shard = "0";
	    GetDataConnectorShardStatusResult getDataConnectorShardStatusResult =
	        client.getDataConnectorShardStatus(projectName, topicName, ConnectorType.SINK_ODPS, shard);
	    System.out.println(getDataConnectorShardStatusResult.getCurSequence());
	    System.out.println(getDataConnectorShardStatusResult.getLastErrorMessage());
	}
	
	
	
	
	
	public void createADSDataConnector (String projectName, String topicName) {
	    // Create SinkADS/SinkMysql DataConnector
	    // ODPS相关配置设置
	    String dbHost = "dev-db1.eselltech.com";
	    int dbPort = 33306;
	    String dbName = "db_es_yxf";
	    String user = "ac_huangjp";
	    String password = "VTaAgGvsgG";
	    String tableName = "T_DATAHUB_TEST";
	    DatabaseDesc desc = new DatabaseDesc();
	    desc.setHost(dbHost);
	    desc.setPort(dbPort);
	    desc.setDatabase(dbName);
	    desc.setUser(user);
	    desc.setPassword(password);
	    desc.setTable(tableName);
	    // batchCommit大小，单位B
	    desc.setMaxCommitSize(512L);
	    // 是否忽略错误 采用insert ignore
	    desc.setIgnore(true);
	    // 顺序选中topic中部分列或全部列 同步到ads，未选中的列将不会同步,选中列必须存在于ADS、Mysql中
	    List<String> columnFields = new ArrayList<String>();
	    columnFields.add("name");
	    
	    DatahubClient client = connector();
	    
	    client.createDataConnector(projectName, topicName, ConnectorType.SINK_ADS, columnFields, desc);
	    // 特殊需求下可以间歇性 如每15分钟获取Connector状态查看是否有异常,遍历所有shard
	    String shard = "0";
	    GetDataConnectorShardStatusResult getDataConnectorShardStatusResult =
	        client.getDataConnectorShardStatus(projectName, topicName, ConnectorType.SINK_ADS, shard);
	    System.out.println(getDataConnectorShardStatusResult.getCurSequence());
	    System.out.println(getDataConnectorShardStatusResult.getLastErrorMessage());
	}
	
	
}
