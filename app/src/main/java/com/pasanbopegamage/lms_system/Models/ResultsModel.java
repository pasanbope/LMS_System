package com.pasanbopegamage.lms_system.Models;

public class ResultsModel {

    private String id,moduleId,moduleName,resultDescription,resultType,uid,excelUrl;
    long timestamp;

    public ResultsModel() {
    }

    public ResultsModel(String id, String moduleId, String moduleName, String resultDescription, String resultType, String uid, String excelUrl, long timestamp) {
        this.id = id;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.resultDescription = resultDescription;
        this.resultType = resultType;
        this.uid = uid;
        this.excelUrl = excelUrl;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExcelUrl() {
        return excelUrl;
    }

    public void setExcelUrl(String excelUrl) {
        this.excelUrl = excelUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
