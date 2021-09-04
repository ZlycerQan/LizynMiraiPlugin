package org.zlycerqan.mirai.lizyn.services.score.connection.model.scoremodel;

public class UserModel {

    private boolean monitor;
    private int roleCount;
    private String roleKeys;
    private String roleValues;
    private int status;
    private boolean usable;
    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }
    public boolean getMonitor() {
        return monitor;
    }

    public void setRoleCount(int roleCount) {
        this.roleCount = roleCount;
    }
    public int getRoleCount() {
        return roleCount;
    }

    public void setRoleKeys(String roleKeys) {
        this.roleKeys = roleKeys;
    }
    public String getRoleKeys() {
        return roleKeys;
    }

    public void setRoleValues(String roleValues) {
        this.roleValues = roleValues;
    }
    public String getRoleValues() {
        return roleValues;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }
    public boolean getUsable() {
        return usable;
    }

}