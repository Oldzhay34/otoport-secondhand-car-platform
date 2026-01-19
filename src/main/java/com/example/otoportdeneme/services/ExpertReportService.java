package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.ExpertReportDto;

public interface ExpertReportService {
    ExpertReportDto getByCarId(Long carId);
}
