package com.health.module.vitalsign.dto;

import com.health.common.enums.SignTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VitalSignAddDTO {

    /** 为空则默认当前登录老年人本人 */
    private Long userId;

    @NotNull(message = "体征类型不能为空")
    private SignTypeEnum signType;

    private BigDecimal valueSystolic;
    private BigDecimal valueDiastolic;
    private BigDecimal valueMain;

    private String unit;

    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordTime;

    private String deviceSource;
    private String remark;
}
