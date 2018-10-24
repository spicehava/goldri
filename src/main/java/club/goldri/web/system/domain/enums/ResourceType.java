package club.goldri.web.system.domain.enums;

public enum ResourceType {
    MENU("菜单"),BUTTON("按钮");

    ResourceType(String description) {
        this.description = description;

    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
