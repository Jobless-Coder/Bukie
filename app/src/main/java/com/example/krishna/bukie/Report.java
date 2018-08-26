package com.example.krishna.bukie;

public class Report
{
    private String reportedbyuid,reportedbyname,reason,additional_details,timestamp;

    public Report(String reportedbyuid, String reportedbyname, String reason, String additional_details, String timestamp) {
        this.reportedbyuid = reportedbyuid;
        this.reportedbyname = reportedbyname;
        this.reason = reason;
        this.additional_details = additional_details;
        this.timestamp = timestamp;
    }

    public String getReportedbyuid() {
        return reportedbyuid;
    }

    public String getReportedbyname() {
        return reportedbyname;
    }

    public String getReason() {
        return reason;
    }

    public String getAdditional_details() {
        return additional_details;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
