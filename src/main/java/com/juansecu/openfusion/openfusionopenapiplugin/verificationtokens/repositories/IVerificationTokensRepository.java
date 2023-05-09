package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Repository
public interface IVerificationTokensRepository
    extends CrudRepository<VerificationTokenEntity, Integer>,
    PagingAndSortingRepository<VerificationTokenEntity, Integer> {
    Optional<VerificationTokenEntity> findByTokenAndTypeAndAccount(
        UUID decryptedToken,
        EVerificationTokenType type,
        AccountEntity account
    );
}
