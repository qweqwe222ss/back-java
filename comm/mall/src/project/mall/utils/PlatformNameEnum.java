package project.mall.utils;




/**
 *  平台名称
 * @author axing
 * @since 2023/9/6
 **/

public enum PlatformNameEnum {

    TONGDA(1, "Tongda"),
    ARGOS(2, "Argos"),
    TIKTOK_MALL(3, "TikTokMall"),
    SHOP2U(4, "Shop2u"),
    LAZ_SHOP(5, "LazShop"),
    FAMILY_SHOP(6, "FamilyShop"),
    JUST_SHOP(7, "Justshop"),
    INCHIO(8, "Inchio"),
    HIVE(9, "Hive"),
    GREENMALL(10, "GreenMall"),
    ARGOSSHOP2(11, "ArgosShop2"),
    TIKTOK_WHOLESALE(12, "TikTokWholesale"),

    // 其他名称 后续补充...

    ;

    private int code;

    private String description;

    private PlatformNameEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}


