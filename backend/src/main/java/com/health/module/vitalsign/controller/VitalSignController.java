package com.health.module.vitalsign.controller;

import com.health.common.Result;
import com.health.module.vitalsign.dto.VitalSignAddDTO;
import com.health.module.vitalsign.entity.VitalSign;
import com.health.module.vitalsign.service.VitalSignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vital-signs")
@RequiredArgsConstructor
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @PostMapping
    public Result<VitalSign> add(@Valid @RequestBody VitalSignAddDTO dto) {
        return Result.ok(vitalSignService.add(dto));
    }

    @GetMapping
    public Result<List<VitalSign>> list(
            @RequestParam(required = false) Long elderUserId,
            @RequestParam(required = false) String signType) {
        return Result.ok(vitalSignService.list(elderUserId, signType));
    }
}
