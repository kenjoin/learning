package chiyue.learning.crawler.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BrowserUtils {

	private BrowserUtils() {
		
		throw new IllegalAccessError("can't instantiation!");
	}
	
	public static Document getDoc(String url, Map<String, Object> paramMap) throws ClientProtocolException, IOException {

		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet get = new HttpGet(url);

		get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(30000).build());

		HttpResponse response = client.execute(get);

		HttpEntity entity = response.getEntity();

		String content = EntityUtils.toString(entity, "GBK");

		org.jsoup.nodes.Document doc = Jsoup.parse(content);

		return doc;
	}
}
