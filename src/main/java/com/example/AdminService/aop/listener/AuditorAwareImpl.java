package com.example.AdminService.aop.listener;

import com.example.AdminService.utils.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(Objects.requireNonNull(CurrentUserService.getCurrentUser()).uuid());
    }

}
