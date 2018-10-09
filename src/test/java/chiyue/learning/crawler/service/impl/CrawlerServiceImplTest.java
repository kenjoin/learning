package chiyue.learning.crawler.service.impl;

import java.io.IOException;

import org.junit.Test;

import chiyue.learning.crawler.service.CrawlerService;
import chiyue.learning.crawler.service.impl.CrawlerServiceImpl;

public class CrawlerServiceImplTest {

	private CrawlerService crawlerService = new CrawlerServiceImpl();
	
	@Test
	public void testGetData() {

		try {
			crawlerService.getData("http://cq1.t5fs.com/", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("ok");
		
	}

}
