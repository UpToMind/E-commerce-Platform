package com.ecommerce.system.order.service.dataaccess.user.adapter;

import com.ecommerce.system.order.service.dataaccess.user.mapper.UserDataAccessMapper;
import com.ecommerce.system.order.service.dataaccess.user.repository.UserJpaRepository;
import com.ecommerce.system.order.service.domain.entity.User;
import com.ecommerce.system.order.service.domain.ports.output.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserDataAccessMapper userDataAccessMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository,
                              UserDataAccessMapper userDataAccessMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userDataAccessMapper = userDataAccessMapper;
    }

    @Override
    public Optional<User> findUser(UUID userId) {
        return userJpaRepository.findById(userId).map(userDataAccessMapper::userEntityToUser);
    }
}
