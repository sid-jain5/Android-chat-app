package com.example.siddh.talktry;

public class groupDetail {

    String groupId;
    String groupName;
    String creator;

    public groupDetail(String groupId, String groupName, String creator) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creator = creator;
    }

    public groupDetail() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
