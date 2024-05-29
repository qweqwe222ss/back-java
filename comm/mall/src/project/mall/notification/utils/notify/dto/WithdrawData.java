package project.mall.notification.utils.notify.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WithdrawData implements Serializable {
    // 提现金额
    private double amount;

    // 提现用户ID
    private String withdrawUserId;
}
