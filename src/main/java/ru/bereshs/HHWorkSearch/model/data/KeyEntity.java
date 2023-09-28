package ru.bereshs.HHWorkSearch.model.data;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Entity
@Table(name = "keys")
@Data
public class KeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private LocalDateTime time;
    private String authorizationCode;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private String clientId;

    public boolean isExpired() {
        if (expiresIn == null) {
            expiresIn = 0L;
        }
        long expiredTime = Timestamp.valueOf(time).getTime() / 1000 + expiresIn;
        long nowTime = System.currentTimeMillis() / 1000;
        return nowTime >= expiredTime;
    }
}
