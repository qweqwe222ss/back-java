package project.mall.auto;

import java.io.InputStream;
import java.util.Properties;

public class AutoConfig {

    private static String CONFIG_FILENAME = "config.properties";
    private static Properties prop = null;


    public static String attribute(String key){
        try {
            //Open the props file
            InputStream is= AutoConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            prop = new Properties();
            //Read in the stored properties
            prop.load(is);
        }
        catch (Exception e) {
            System.err.println("读取配置文件失败！！！");
            prop = null;
        }
        return prop.getProperty(key);
    }
    /**
     * 商品导入url
     */
    public static String goodsUrl = "https://qfushj.site//api/item/collect";

    /**
     * 分销接口
     */
    public static String distribution = "https://qfushj.site/api/promote/distribution";

}