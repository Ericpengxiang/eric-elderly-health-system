package com.health.module.profile.service;

import com.health.module.profile.dto.ProfileVO;
import com.health.module.profile.entity.HealthProfile;

public interface ProfileService {

    ProfileVO getByElderUserId(Long elderUserId);

    HealthProfile saveOrUpdate(Long elderUserId, ProfileVO vo);
}
