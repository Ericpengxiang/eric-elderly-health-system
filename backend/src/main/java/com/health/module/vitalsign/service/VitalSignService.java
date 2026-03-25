package com.health.module.vitalsign.service;

import com.health.module.vitalsign.dto.VitalSignAddDTO;
import com.health.module.vitalsign.entity.VitalSign;

import java.util.List;

public interface VitalSignService {

    VitalSign add(VitalSignAddDTO dto);

    List<VitalSign> list(Long elderUserId, String signType);

}
