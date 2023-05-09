package com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;

@Repository
public interface IAccountsRepository
    extends CrudRepository<AccountEntity, Integer>,
    PagingAndSortingRepository<AccountEntity, Integer> {
    Optional<AccountEntity> findByEmailIgnoreCase(String email);

    Optional<AccountEntity> findByUsernameIgnoreCase(String username);
}
