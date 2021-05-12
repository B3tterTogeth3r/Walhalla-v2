package de.walhalla.app2.model;

public class SocialMedia {
    public static final String ICON = "icon";
    public static final String LINK = "link";
    public static final String NAME = "name";
    private String icon, link, name;

    public SocialMedia(){}
    public SocialMedia(String icon, String link, String name) {
        this.icon = icon;
        this.link = link;
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
