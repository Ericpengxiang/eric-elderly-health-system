package com.health.module.assessment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 第三方风险评估接口（演示环境默认不可达，走降级本地规则引擎）
 */
@FeignClient(name = "assessmentRemote", url = "${assessment.remote-url:http://127.0.0.1:19998}")
public interface AssessmentFeignClient {

    @PostMapping("/risk/evaluate")
    Map<String, Object> evaluate(@RequestBody Map<String, Object> payload);
}
