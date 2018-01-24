package com.muhardin.endy.belajar.googleanalytics.belajargoogleanalytics.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleAnalyticsService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${googleapi.credential}")
    private String credentialFile;

    @Value("${spring.application.name}")
    private String applicationName;

    private AnalyticsReporting analyticsReporting;

    @PostConstruct
    public void inisialisasiGoogleApi() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = GoogleCredential
                    .fromStream(new FileInputStream(credentialFile))
                    .createScoped(AnalyticsReportingScopes.all());
            // Construct the Analytics Reporting service object.
            analyticsReporting = new AnalyticsReporting.Builder(httpTransport,
                    JSON_FACTORY, credential)
                    .setApplicationName(applicationName).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReport(String viewId) throws Exception {
        // Create the DateRange object.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate("30daysAgo");
        dateRange.setEndDate("today");

        // Create the Metrics object.
        Metric pageViews = new Metric()
                .setExpression("ga:pageviews")
                .setAlias("pageviews");



        Dimension pageTitle = new Dimension().setName("ga:pageTitle");
        Dimension browser = new Dimension().setName("ga:browser");
        Dimension os = new Dimension().setName("ga:operatingSystem");

        // Create the ReportRequest object.
        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(Arrays.asList(dateRange))
                .setMetrics(Arrays.asList(pageViews))
                .setDimensions(Arrays.asList(pageTitle, browser, os));

        ArrayList<ReportRequest> requests = new ArrayList<>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = analyticsReporting.reports()
                .batchGet(getReport).execute();

        for (Report report: response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows == null) {
                System.out.println("No data found for " + viewId);
                return;
            }

            for (ReportRow row : rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();

                for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    System.out.println(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }

                for (int j = 0; j < metrics.size(); j++) {
                    System.out.print("Date Range (" + j + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                        System.out.println(metricHeaders.get(k).getName() + ": " + values.getValues().get(k));
                    }
                }
            }
        }
    }
}
