package org.globant.restaurant.service.Client;

import org.globant.restaurant.entity.ClientEntity;
import org.globant.restaurant.mapper.ClientConverter;
import org.globant.restaurant.model.ClientDto;
import org.globant.restaurant.repository.Client.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ClientServiceImpl implements IClientService {

    ClientRepository clientRepository;

    ClientConverter converter;


    public ClientServiceImpl(ClientRepository clientRepository, ClientConverter converter) {
        this.clientRepository = clientRepository;
        this.converter = converter;
    }


    @Override
    public ResponseEntity<?> save(ClientDto client) {
        try {
            clientRepository.save(converter.convertClientDTOToClientEntity(client));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(clientRepository.findByDocument(client.getDocument()));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Internal server error: " + e);
        }
    }

    @Override
    public ResponseEntity<?> updateByDocument(ClientDto clientDto) {
        Optional<ClientEntity> OptionalClient = clientRepository.findByDocument(clientDto.getDocument());

        if (OptionalClient.isPresent()) {
            ClientEntity clientEntity = converter.convertClientDTOToClientEntity(clientDto);
            clientEntity.setUuid(OptionalClient.get().getUuid());
            clientRepository.save(clientEntity);
            return ResponseEntity.ok().body(clientEntity);
        }
        return ResponseEntity.badRequest().body("Client with document not found");
    }

    @Override
    public ResponseEntity<?> deleteClientByDocument(String document) {
        Optional<ClientEntity> optionalClient = clientRepository.findByDocument(document);
        if (optionalClient.isPresent()) {
            clientRepository.deleteByDocument(document);
            return ResponseEntity.ok().body("Client deleted correctly");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }

    @Override
    public ResponseEntity<?> findClientByDocument(String document) {
        Optional<ClientEntity> optionalClient = clientRepository.findByDocument(document);
        if (optionalClient.isPresent()) {
            return ResponseEntity.ok().body(clientRepository.findByDocument(document));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
    }


}
