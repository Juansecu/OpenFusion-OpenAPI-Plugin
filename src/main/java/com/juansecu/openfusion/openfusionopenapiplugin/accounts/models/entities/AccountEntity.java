package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountLevel;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.TimeUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Entity(name = "Accounts")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
public class AccountEntity implements UserDetails {
    @Column(
        columnDefinition = "INTEGER",
        name = "AccountID",
        nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer accountId;
    @Column(
        columnDefinition = "TEXT COLLATE NOCASE",
        name = "Login",
        nullable = false,
        unique = true
    )
    @NonNull
    private String username;
    @Column(
        columnDefinition = "TEXT COLLATE NOCASE DEFAULT ''",
        name = "Email",
        nullable = false
    )
    @NonNull
    private String email;
    @Column(
        columnDefinition = "TEXT",
        name = "Password",
        nullable = false
    )
    @NonNull
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
    private EAccountLevel accountLevel = EAccountLevel.USER;
    @Column(
        columnDefinition = "INTEGER DEFAULT 0",
        name = "Verified",
        nullable = false
    )
    private boolean isVerified = false;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "account")
    private List<VerificationTokenEntity> tokens;
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
        return List.of(new SimpleGrantedAuthority(this.accountLevel.toString()));
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
