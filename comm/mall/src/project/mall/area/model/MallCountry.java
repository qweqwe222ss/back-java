package project.mall.area.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallCountry extends EntityObject {
    private static final long serialVersionUID = 8096944949372440876L;

    private String countryNameCn;

    private String countryNameEn;

    private String countryNameTw;
    private Integer flag;
    //创建时间

    //创建时间
    private Date updatedAt;

   // private String updatedBy;

}
