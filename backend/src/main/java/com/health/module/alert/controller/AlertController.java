package com.health.module.alert.controller;

import com.health.common.Result;
import com.health.module.alert.entity.AlertRecord;
import com.health.module.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public Result<List<AlertRecord>> list(@RequestParam(required = false) Long elderUserId) {
        return Result.ok(alertService.listForCurrentUser(elderUserId));
    }

    @PostMapping("/{id}/read")
    public Result<Void> read(@PathVariable Long id) {
        alertService.markRead(id);
        return Result.ok(null);
    }

    @PostMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id, @RequestParam String remark) {
        alertService.markHandled(id, remark);
        return Result.ok(null);
    }
}
