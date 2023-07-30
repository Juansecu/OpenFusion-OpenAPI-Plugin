package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;

@Entity(name = "VerificationTokens")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
public class VerificationTokenEntity {
    @Column(
        columnDefinition = "INTEGER",
        name = "VerificationTokenID",
        nullable = false
    )
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Integer verificationTokenId;
    @Column(
        columnDefinition = "TEXT",
        name = "Token",
        nullable = false,
        unique = true
    )
    @NonNull
    private UUID token;
    @Column(
        columnDefinition = "TEXT",
        name = "Type",
        nullable = false
    )
    @Enumerated(EnumType.STRING)
    @NonNull
    private EVerificationTokenType type;
    @Column(
        columnDefinition = "INTEGER",
        name = "UsesCount",
        nullable = false
    )
    private int usesCount = 0;
    @JoinColumn(
        columnDefinition = "INTEGER",
        name = "AccountID",
        nullable = false
    )
    @ManyToOne(cascade = CascadeType.REMOVE)
    @NonNull
    private AccountEntity account;
    @Column(
        columnDefinition = "INTEGER DEFAULT (strftime('%s', 'now'))",
        name = "Created",
        nullable = false,
        updatable = false
    )
    private long createdAt;
    @Column(
        columnDefinition = "INTEGER",
        name = "Expires",
        nullable = false
    )
    @NonNull
    private long expiresAt;
}
