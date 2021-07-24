package com.qelery.mealmojo.api.service.utility;

import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.model.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectMapperUtils {

    private final ModelMapper modelMapper;

    @Autowired
    public ObjectMapperUtils() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(false);
        sourceToDestination(modelMapper);
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

    private void sourceToDestination(ModelMapper modelMapper) {
        modelMapper.createTypeMap(User.class, UserDtoOut.class)
                .addMappings(mapper -> mapper.using(firstNameConverter).map(user -> user, UserDtoOut::setFirstName))
                .addMappings(mapper -> mapper.using(lastNameConverter).map(user -> user, UserDtoOut::setLastName));
    }

    Converter<User, String> firstNameConverter = mappingContext -> {
        User user = mappingContext.getSource();
        if (user.getCustomerProfile() != null) {
            return user.getCustomerProfile().getFirstName();
        } else if (user.getMerchantProfile() != null) {
            return user.getMerchantProfile().getFirstName();
        } else {
            return null;
        }
    };

    Converter<User, String> lastNameConverter = mappingContext -> {
        User user = mappingContext.getSource();
        if (user.getCustomerProfile() != null) {
            return user.getCustomerProfile().getLastName();
        } else if (user.getMerchantProfile() != null) {
            return user.getMerchantProfile().getLastName();
        } else {
            return null;
        }
    };
}
