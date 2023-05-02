package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities;

import java.util.Collection;
import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountLevel;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.TimeUtil;

@Entity(name = "Accounts")
@Getter
@Setter
public class AccountEntity implements UserDetails {
    @Column(
        columnDefinition = "INTEGER",
        name = "AccountID",
        nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer userId;
    @Column(
        columnDefinition = "TEXT COLLATE NOCASE",
        name = "Login",
        nullable = false,
        unique = true
    )
    private String username;
    @Column(
        columnDefinition = "TEXT",
        name = "Password",
        nullable = false
    )
    private String password;
    @Column(
        columnDefinition = "INTEGER DEFAULT 1",
        name = "Selected",
        nullable = false
    )
    private boolean isSelected = true;
    @Column(
        columnDefinition = "INTEGER",
        name = "AccountLevel",
        nullable = false
    )
    @Enumerated(EnumType.ORDINAL)
    private EAccountLevel accountLevel = EAccountLevel.USER;
    @Column(
        columnDefinition = "INTEGER DEFAULT (strftime('%s', 'now'))",
        name = "Created",
        nullable = false,
        updatable = false
    )
    private long createdAt;
    @Column(
        columnDefinition = "INTEGER DEFAULT (strftime('%s', 'now'))",
        name = "LastLogin",
        nullable = false
    )
    private long lastLogin;
    @Column(
        columnDefinition = "INTEGER DEFAULT 0",
        name = "BannedUntil",
        nullable = false
    )
    private long bannedUntil = 0;
    @Column(
        columnDefinition = "INTEGER DEFAULT 0",
        name = "BannedSince",
        nullable = false
    )
    private long bannedSince = 0;
    @Column(
        columnDefinition = "TEXT DEFAULT ''",
        name = "BanReason",
        nullable = false
    )
    private String banReason = "";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        final long today = new Date().getTime();
        return TimeUtil.secondsToMilliseconds(this.bannedUntil) < today;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isAccountNonLocked();
    }
}
