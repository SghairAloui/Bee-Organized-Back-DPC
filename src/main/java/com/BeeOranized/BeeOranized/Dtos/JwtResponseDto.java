package com.BeeOranized.BeeOranized.Dtos;

import java.util.List;


public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private List<String> roles;



    private String name;
    private  String userCity;

    public JwtResponseDto(String accessToken, String username,String name,String userCity, List<String> roles, Long userId) {
        this.token = accessToken;
        this.roles = roles;
        this.userId = userId;
        this.name = name;
        this.userCity = userCity;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public JwtResponseDto(String accessToken) {
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

