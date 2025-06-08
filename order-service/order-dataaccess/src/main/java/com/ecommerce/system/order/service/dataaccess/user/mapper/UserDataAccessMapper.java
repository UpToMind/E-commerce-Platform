package com.ecommerce.system.order.service.dataaccess.user.mapper;

import com.ecommerce.system.domain.valueobject.UserId;
import com.ecommerce.system.order.service.dataaccess.user.entity.UserEntity;
import com.ecommerce.system.order.service.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDataAccessMapper {
    public User userEntityToUser(UserEntity userEntity) {
        return new User(new UserId(userEntity.getId()));
    }
}
