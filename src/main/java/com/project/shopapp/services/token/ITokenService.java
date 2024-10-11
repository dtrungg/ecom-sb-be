package com.project.shopapp.services.token;

import com.project.shopapp.models.User;

public interface ITokenService {
    void addToken(User user, String token, boolean isMobileDevice);
}
