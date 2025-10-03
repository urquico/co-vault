package com.example.covault.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class RefreshTokensId implements Serializable {

    private Long userId;

    private String device;

    public RefreshTokensId() {
    }

    public RefreshTokensId(Long userId, String device) {
        this.userId = userId;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokensId that = (RefreshTokensId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, device);
    }
}