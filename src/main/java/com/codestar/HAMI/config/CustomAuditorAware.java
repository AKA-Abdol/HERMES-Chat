package com.codestar.HAMI.config;

import org.springframework.data.domain.AuditorAware;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class CustomAuditorAware implements AuditorAware<Instant> {

    @Override
    public Optional<Instant> getCurrentAuditor() {
        Instant date = new Date().toInstant();
        return Optional.of(date);
    }

}
