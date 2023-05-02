package com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;

import java.util.Optional;

@Repository
public interface IAccountsRepository
    extends CrudRepository<AccountEntity, Integer>,
    PagingAndSortingRepository<AccountEntity, Integer> {
    AccountEntity findByEmail(String email);

    Optional<AccountEntity> findByUsernameIgnoreCase(String username);
}
