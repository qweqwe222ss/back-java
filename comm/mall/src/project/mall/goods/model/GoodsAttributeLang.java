package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class GoodsAttributeLang  extends EntityObject {

    /**
     * 名称
     */
    private String name;

    /**
     * 语言
     */
    private  String lang;

    /**
     * 假删除 0-存在 1-删除
     */
    private  int type;

    /**
     * 属性ID
     */
    private  String attrId;
}
