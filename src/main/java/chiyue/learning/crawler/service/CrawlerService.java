package chiyue.learning.crawler.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface CrawlerService<E> {
	
	List<E> getData(String url, Map<String, Object> paramMap) throws ClientProtocolException, IOException;
	
}
