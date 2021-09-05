package com.qelery.mealmojo.api.service.utility;

import com.qelery.mealmojo.api.model.dto.AddressDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Role;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapperUtils {

    private final ModelMapper modelMapper;

    @Autowired
    public MapperUtils() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(Conditions.isNotNull())
                .setAmbiguityIgnored(false);
        setAdditionalConverters(modelMapper);
    }

    public <D, T> D map(final T obj, Class<D> outclass) {
        return this.modelMapper.map(obj, outclass);
    }

    public void map(Object source, Object destination) {
        this.modelMapper.map(source, destination);
    }

    public <D, T> List<D> mapAll(final List<T> objList, Class<D> outClass) {
        return objList.stream().map(obj -> map(obj, outClass)).collect(Collectors.toList());
    }

    public <D, T> Set<D> mapAll(final Set<T> objList, Class<D> outClass) {
        return objList.stream().map(obj -> map(obj, outClass)).collect(Collectors.toSet());
    }

    private void setAdditionalConverters(ModelMapper modelMapper) {
        modelMapper.createTypeMap(User.class, UserInfoDto.class).setPreConverter(userToUserInfoDtoConverter);
        modelMapper.createTypeMap(Address.class, Address.class).setPreConverter(streetsConverter);
    }

    Converter<User, UserInfoDto> userToUserInfoDtoConverter = mappingContext -> {
        User source = mappingContext.getSource();
        UserInfoDto destination = mappingContext.getDestination();
        if (source.getRole() == Role.CUSTOMER) {
            if (source.getCustomerProfile().getAddress() != null) {
                AddressDto addressDto = this.map(source.getCustomerProfile().getAddress(), AddressDto.class);
                destination.setAddress(addressDto);
            } else {
                destination.setAddress(null);
            }
            destination.setFirstName(source.getCustomerProfile().getFirstName());
            destination.setLastName(source.getCustomerProfile().getLastName());
        } else if (source.getRole() == Role.MERCHANT) {
            destination.setFirstName(source.getMerchantProfile().getFirstName());
            destination.setLastName(source.getMerchantProfile().getLastName());
            destination.setAddress(null);
        }
        return destination;
    };

    Converter<Address, Address> streetsConverter = mappingContext -> {
        Address source = mappingContext.getSource();
        Address destination = mappingContext.getDestination();
        destination.setStreet2(source.getStreet2());
        destination.setStreet3(source.getStreet3());
        return destination;
    };
}
