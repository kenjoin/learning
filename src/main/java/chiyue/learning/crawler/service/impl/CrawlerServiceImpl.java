package chiyue.learning.crawler.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import chiyue.learning.crawler.entry.ZFWAdEntry;
import chiyue.learning.crawler.service.CrawlerService;
import chiyue.learning.crawler.utils.BrowserUtils;

public class CrawlerServiceImpl implements CrawlerService<ZFWAdEntry> {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CrawlerServiceImpl.class);
	
	@Override
	public List<ZFWAdEntry> getData(String url, Map<String, Object> paramMap) throws ClientProtocolException, IOException {

		Document doc = BrowserUtils.getDoc(url, paramMap);
		
        Elements es = doc.select(".wrap>dl");
        
        
        for(Element element : es) {
        	
        	logger.info("data： {}-{}-{}-{}-{}-{}-{}", 
        			element.select("dd.c1").text(), 
        			element.select("dd.c2").text(), 
        			element.select("dd.c3").text(), 
        			element.select("dd.c4").text(), 
        			element.select("dd.c5").text(), 
        			element.select("dd.c6").text(), 
        			element.select("dd.c7>a").attr("href"));
        }
        
        logger.info("first: {}", es.get(0));
		return null;
	}


}
