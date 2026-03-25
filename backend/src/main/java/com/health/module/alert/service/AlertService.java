package com.health.module.alert.service;

import com.health.module.alert.entity.AlertRecord;
import com.health.module.vitalsign.entity.VitalSign;

import java.util.List;

public interface AlertService {

    AlertRecord createFromVitalSign(VitalSign vital, String alertType, String content,
                                    com.health.common.enums.AlertLevelEnum level);

    List<AlertRecord> listForCurrentUser(Long elderUserId);

    void markHandled(Long alertId, String remark);

    void markRead(Long alertId);
}
