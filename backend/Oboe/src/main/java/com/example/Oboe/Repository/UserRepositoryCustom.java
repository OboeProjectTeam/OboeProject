package com.example.Oboe.Repository;

import java.util.UUID;

public interface UserRepositoryCustom {
    void deleteUserWithDependencies(UUID userId);
}
