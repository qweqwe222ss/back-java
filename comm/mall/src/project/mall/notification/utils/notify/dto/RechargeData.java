package project.mall.notification.utils.notify.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeData implements Serializable {
    // 充值金额
    private double amount;

    // 充值用户ID
    private String rechargeUserId;

}
