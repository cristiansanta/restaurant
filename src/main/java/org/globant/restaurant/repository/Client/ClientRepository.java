package org.globant.restaurant.repository.Client;

import org.globant.restaurant.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByDocument(String document);

    void deleteByDocument(String document);
}
