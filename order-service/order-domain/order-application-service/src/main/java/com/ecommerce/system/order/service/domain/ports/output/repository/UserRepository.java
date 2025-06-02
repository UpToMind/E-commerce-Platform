package com.ecommerce.system.order.service.domain.ports.output.repository;

import com.ecommerce.system.order.service.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findUser(UUID userId);

}
