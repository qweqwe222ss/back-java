package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class SellerGoods  extends EntityObject {
    private static final long serialVersionUID = 8096944949372440876L;

//    @ApiModelProperty(value = "商品id")
//    @TableField("GOODS_ID")
    private SystemGoods systemGoods;
//    @ApiModelProperty(value = "系统商品id")
    private String goodsId;
    //    @ApiModelProperty(value = "商家id")
//    @TableField("SELLER_ID")
    private String sellerId;
    //类型id
    /**
     * 一级分类ID
     */
//	@ApiModelProperty(value = "商品类型")
    private String categoryId;

    /**
     * 二级分类ID
     */
    private String secondaryCategoryId;

//    @ApiModelProperty(value = "已售出")
//    @TableField("SOLD_NUM")
    private Integer soldNum;

//    @ApiModelProperty(value = "浏览量")
//    @TableField("VIEWS_NUM")
    private Integer viewsNum;

    //    @ApiModelProperty(value = "进货价")
//    @TableField("SYSTEM_PRIZE")
    private Double systemPrice;

//    @ApiModelProperty(value = "售卖价格")
//    @TableField("SELLING_PRICE")
    private Double sellingPrice;

    private Double profitRatio;

    private Double discountPrice;

    private Double orderPrice;

    private Double discountRatio;

//    @ApiModelProperty(value = "最后保存时间")
//    @TableField("UP_TIME")
    private Long upTime;

    // 第一次上架时间
    private Long firstShelfTime;

    /**
     * 创建时间
     */
    private Date createTime;
    //	@ApiModelProperty(value = "推荐时间（0=不推荐）")
    private Long recTime;

    //	@ApiModelProperty(value = "新品（0不是新品）")
    private Long newTime;

    //	@ApiModelProperty(value = "是否上架（1上架 0不上架）")
    private Integer isShelf;

    //	@ApiModelProperty(value = "系统推荐时间（0=不推荐）")
    private Long systemRecTime;

    //	@ApiModelProperty(value = "系统新品（0不是新品）")
    private Long systemNewTime;

    private Long stopTime;

    //最小购买数量（已废弃）
    private Integer buyMin;

    private Date discountStartTime;

    private Date discountEndTime;

    // 是否开通直通车
    private Integer isCombo;

    // 是否删删除 (有效1(未删除)  无效0(删除))
    private Integer isValid;

    //	@ApiModelProperty(value = "设置热销（0=不热销）")
    private Long sellWellTime;

    /**
     * 展示权重 1
     */
    private Long showWeight1;

    /**
     * 展示权重 2
     */
    private Long showWeight2;

    public Long getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getViewsNum() {
        return this.viewsNum;
    }

    public void setViewsNum(Integer viewsNum) {
        this.viewsNum = viewsNum;
    }
}
