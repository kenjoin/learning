package chiyue.learning.crawler.service.impl;

import java.io.IOException;
import java.util.List;
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
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import chiyue.learning.crawler.entry.ZFWAdEntry;
import chiyue.learning.crawler.service.CrawlerService;

public class CrawlerServiceImpl implements CrawlerService<ZFWAdEntry> {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CrawlerServiceImpl.class);
	
	@Override
	public List<ZFWAdEntry> getData(String url, Map<String, Object> paramMap) throws ClientProtocolException, IOException {
		
        CloseableHttpClient client = HttpClients.createDefault();
        
        HttpGet get = new HttpGet(url);

        get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(30000).build());
        
        HttpResponse response = client.execute(get);
        
        HttpEntity entity = response.getEntity();
        
        String content = EntityUtils.toString(entity, "GBK");
        
        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        
        Elements es = doc.getElementsByTag("title");
        
        logger.info("data： {}", es.get(0)); 
        
        
		return null;
	}


}
