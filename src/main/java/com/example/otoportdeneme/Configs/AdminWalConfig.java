package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.services.WalService;
import jakarta.servlet.Filter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AdminWalProperties.class)
public class AdminWalConfig {

    @Bean
    public Filter adminWalFilter(WalService walService, AdminWalProperties props) {
        return new AdminWalFilter(walService, props);
    }
}
