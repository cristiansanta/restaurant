package org.globant.restaurant.service.Client;

import org.globant.restaurant.commons.constans.response.client.IClientResponse;
import org.globant.restaurant.entity.ClientEntity;
import org.globant.restaurant.exceptions.EntityAlreadyExistsException;
import org.globant.restaurant.exceptions.EntityHasNoDifferentDataException;
import org.globant.restaurant.exceptions.EntityNotFoundException;
import org.globant.restaurant.exceptions.InvalidQueryArgsException;
import org.globant.restaurant.mapper.ClientConverter;
import org.globant.restaurant.model.ClientDto;
import org.globant.restaurant.repository.Client.IClientRepository;
import org.globant.restaurant.validators.ClientValidators;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    private final ClientConverter converter;

    private final ClientValidators validator;

    public ClientServiceImpl(IClientRepository clientRepository, ClientConverter converter, ClientValidators validator) {
        this.clientRepository = clientRepository;
        this.converter = converter;
        this.validator = validator;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        Optional<ClientEntity> optionalClient = clientRepository.findByDocument(clientDto.getDocument());
        ClientEntity clientEntity = converter.convertClientDtoToClientEntity(clientDto);
        if (!optionalClient.isPresent()){
            if(validator.isSavableClient(clientDto)){
                return converter.convertClientEntityToClientDTO
                                (clientRepository.save(clientEntity));
            } throw new EntityHasNoDifferentDataException(IClientResponse.CLIENT_DIFFERENT_DATA);
        }
        throw new EntityAlreadyExistsException(IClientResponse.CLIENT_EXIST);
    }

    @Override
    public void updateByDocument(String document, ClientDto clientDto) {
        clientDto.setDocument(document);
        Optional<ClientEntity> optionalClient = clientRepository.findByDocument(clientDto.getDocument());

        if (optionalClient.isPresent()) {
            if(validator.clientIsUpdatable(clientDto, optionalClient.get())){
                ClientEntity clientEntity = converter.convertClientDtoToClientEntity(clientDto);
                clientEntity.setUuid(optionalClient.get().getUuid());
                clientRepository.save(clientEntity);
            } throw new EntityHasNoDifferentDataException(IClientResponse.CLIENT_DIFFERENT_DATA);
        } throw new EntityNotFoundException(IClientResponse.CLIENT_NOT_EXIST);
    }

    @Override
    public void deleteByDocument(String document) {
        Optional<ClientEntity> optionalClient = clientRepository.findByDocument(document);
        if (optionalClient.isPresent()) {
            clientRepository.deleteByDocument(document);
        } throw new EntityNotFoundException(IClientResponse.CLIENT_NOT_EXIST);
    }

    @Override
    public ClientDto findClientByDocument(String document){
            Optional<ClientEntity> optionalClient = clientRepository.findByDocument(document);
            if (optionalClient.isPresent()) {
                return converter.convertClientEntityToClientDTO(optionalClient.get());
            } throw new EntityNotFoundException(IClientResponse.CLIENT_NOT_EXIST);
    }

    @Override
    public List<ClientDto> findAllByCustomFieldAndOrder(String customField, String customOrder) {
        boolean queryIsValid = validator.validateCustomFieldAndOrderQuery(customField, customOrder);

        if (queryIsValid){
            customField = validator.transformCustomField(customField);

            Sort sort = customOrder.equalsIgnoreCase("desc")
                    ? Sort.by(customField).descending()
                    : Sort.by(customField).ascending();

            List<ClientEntity> clientEntities = clientRepository.findAll(sort);

            return clientEntities.stream()
                    .map(converter::convertClientEntityToClientDTO)
                    .toList();
        }
        throw new InvalidQueryArgsException(IClientResponse.CLIENT_INVALID_ARGUMENTS);
    }
}