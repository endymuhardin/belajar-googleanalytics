package com.muhardin.endy.belajar.googleanalytics.belajargoogleanalytics;

import com.muhardin.endy.belajar.googleanalytics.belajargoogleanalytics.service.GoogleAnalyticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BelajarGoogleanalyticsApplicationTests {

	@Autowired private GoogleAnalyticsService googleAnalyticsService;

	@Test
	public void testGenerateReport() throws Exception {
		googleAnalyticsService.getReport("65778193");
	}

}
