package club.goldri.web.system.domain.enums;

public enum LogType {
    EXCEPTION("异常"), NORMAL("正常");

    LogType(String description) {
        this.description = description;
    }

    //枚举的描述
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}