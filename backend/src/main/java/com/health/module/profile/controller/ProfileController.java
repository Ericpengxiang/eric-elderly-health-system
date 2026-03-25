package com.health.module.profile.controller;

import com.health.common.Result;
import com.health.module.profile.dto.ProfileVO;
import com.health.module.profile.entity.HealthProfile;
import com.health.module.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public Result<ProfileVO> get(@RequestParam(required = false) Long elderUserId) {
        return Result.ok(profileService.getByElderUserId(elderUserId));
    }

    @PutMapping
    public Result<HealthProfile> save(@RequestParam(required = false) Long elderUserId,
                                      @Valid @RequestBody ProfileVO vo) {
        return Result.ok(profileService.saveOrUpdate(elderUserId, vo));
    }
}
