package chiyue.learning.aliyun.datahub;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.datahub.common.data.Field;
import com.aliyun.datahub.common.data.FieldType;
import com.aliyun.datahub.common.data.RecordSchema;
import com.aliyun.datahub.common.data.RecordType;
import com.aliyun.datahub.model.ListShardResult;
import com.aliyun.datahub.model.PutBlobRecordsResult;
import com.aliyun.datahub.model.PutRecordsResult;
import com.aliyun.datahub.model.RecordEntry;
import com.aliyun.datahub.model.ShardEntry;
import com.aliyun.datahub.model.GetCursorRequest.CursorType;

import junit.framework.Assert;

public class DataHubClientTest {

	@Test
	public void testCreateTopic() {

		new DataHubClient().createTopic("es_yxfbp_gw", "topic_example_1", "topic_example_Desc");

	}

	@Test
	public void testGetShardList() {

		ListShardResult listShardResult = new DataHubClient().getShardList("es_yxfbp_gw", "topic_example_1");

		for (Iterator<ShardEntry> it = listShardResult.getShards().iterator(); it.hasNext();) {

			ShardEntry shardEntry = it.next();

			System.out.println(JSONObject.toJSONString(shardEntry));
		}
	}
	
	@Test
	public void testWriteTupleData() {
		
		RecordSchema schema = new RecordSchema();
		
		schema.addField(new Field("id", FieldType.STRING));
		schema.addField(new Field("name", FieldType.STRING));
		schema.addField(new Field("desc", FieldType.STRING));
		
		RecordEntry recordEntry = new RecordEntry(schema);
		recordEntry.setShardId("0");
		recordEntry.setString(0, "2");
		recordEntry.setString("name", "tao");
		recordEntry.setString(2, "this is exmple desc for tao.");
		
		PutRecordsResult result = new DataHubClient()
				.writeTupleData("es_yxfbp_gw", "topic_example_1", recordEntry);
		
		if(result == null)
			return;

		System.out.println(JSONObject.toJSONString(result.getFailedRecordError()));
		
		for(Iterator<RecordEntry> it = result.getFailedRecords().iterator(); it.hasNext();) {
			
			RecordEntry recordEntry1 = it.next();
			
			System.out.println(JSONObject.toJSONString(recordEntry1));
		}
		
		Assert.assertNull(result);
	}
	
	@Test
	public void testReadTupleData() {
		List<RecordEntry> recordEntries = new DataHubClient()
				.readTupleData("es_yxfbp_gw", "topic_example_1", "0", CursorType.OLDEST);
		System.out.println(recordEntries.size());
		
		for(Iterator<RecordEntry> it = recordEntries.iterator(); it.hasNext();) {
			
			RecordEntry recordEntry = it.next();
			
			System.out.println(JSONObject.toJSONString(recordEntry));
			
			Field[] fields = recordEntry.getFields();
			
			for(Field field : fields)
				System.out.println(field.getName() +": "+recordEntry.get(field.getName()));
			
			System.out.println();
		}
	}
	
	@Test
	public void testAddColumn() {
		
		Field field = new Field("addr", FieldType.STRING);
		new DataHubClient().addColumn("es_yxfbp_gw", "topic_example_1", field, false);
		
	}
	
	@Test
	public void testPutBlobRecord() {
		
		String projectName = "es_yxfbp_gw";
		String topicName = "topic_example_1";
		
		PutBlobRecordsResult result = new DataHubClient()
				.putBlobRecord(projectName, topicName, 5, 2, 
						RecordType.BLOB, "putBlobRecord", "aaa");
		
		System.out.println(result);
	}
	
	
	
	@Test
	public void testCreateDataConnector() {
		
	}
	
	@Test
	public void testCreateADSDataConnector() {
		
		new DataHubClient().createADSDataConnector("es_yxfbp_gw", "topic_example_1");
		
	}
	

}
