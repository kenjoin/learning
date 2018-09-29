package chiyue.learning.aliyun.datahub;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.aliyun.datahub.DatahubClient;
import com.aliyun.datahub.DatahubConfiguration;
import com.aliyun.datahub.auth.AliyunAccount;
import com.aliyun.datahub.common.data.Field;
import com.aliyun.datahub.common.data.FieldType;
import com.aliyun.datahub.common.data.RecordSchema;
import com.aliyun.datahub.common.data.RecordType;
import com.aliyun.datahub.exception.DatahubClientException;
import com.aliyun.datahub.exception.InvalidCursorException;
import com.aliyun.datahub.model.GetCursorRequest.CursorType;
import com.aliyun.datahub.model.AppendDataConnectorFieldRequest;
import com.aliyun.datahub.model.AppendFieldRequest;
import com.aliyun.datahub.model.ConnectorType;
import com.aliyun.datahub.model.GetCursorResult;
import com.aliyun.datahub.model.GetRecordsResult;
import com.aliyun.datahub.model.ListShardResult;
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
	
}
