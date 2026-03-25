package com.health.module.user.service;

import com.health.module.user.dto.LoginDTO;
import com.health.module.user.dto.LoginVO;
import com.health.module.user.dto.RefreshTokenDTO;
import com.health.module.user.dto.RegisterDTO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    LoginVO refresh(RefreshTokenDTO dto);

    void register(RegisterDTO dto);
}
