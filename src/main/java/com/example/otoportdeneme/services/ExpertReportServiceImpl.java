package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.ExpertItemDto;
import com.example.otoportdeneme.dto_Objects.ExpertReportDto;
import com.example.otoportdeneme.models.ExpertReport;
import com.example.otoportdeneme.repositories.ExpertItemRepository;
import com.example.otoportdeneme.repositories.ExpertReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
public class ExpertReportServiceImpl implements ExpertReportService {

    private final ExpertReportRepository expertReportRepository;
    private final ExpertItemRepository expertItemRepository;

    public ExpertReportServiceImpl(ExpertReportRepository expertReportRepository,
                                   ExpertItemRepository expertItemRepository) {
        this.expertReportRepository = expertReportRepository;
        this.expertItemRepository = expertItemRepository;
    }

    @Override
    public ExpertReportDto getByCarId(Long carId) {
        var opt = expertReportRepository.findByCarId(carId);

        // ✅ RAPOR YOKSA: default rapor (tüm parçalar ORIGINAL)
        if (opt.isEmpty()) {
            ExpertReportDto dto = new ExpertReportDto();
            dto.setId(null);
            dto.setCarId(carId);
            dto.setCompanyName(null);
            dto.setReportDate(null);
            dto.setReportNo(null);
            dto.setResult(com.example.otoportdeneme.Enums.ExpertResult.UNKNOWN);
            dto.setNotes(null);

            java.util.List<ExpertItemDto> items = new java.util.ArrayList<>();
            for (com.example.otoportdeneme.Enums.CarPart p : com.example.otoportdeneme.Enums.CarPart.values()) {
                items.add(new ExpertItemDto(
                        p,
                        com.example.otoportdeneme.Enums.PartStatus.ORIGINAL,
                        "Varsayılan: mağaza expertiz girmedi"
                ));
            }
            dto.setItems(items);
            return dto;
        }

        // ✅ RAPOR VARSA: normal map
        ExpertReport report = opt.get();

        ExpertReportDto dto = new ExpertReportDto();
        dto.setId(report.getId());
        dto.setCarId(carId);
        dto.setCompanyName(report.getCompanyName());
        dto.setReportDate(report.getReportDate());
        dto.setReportNo(report.getReportNo());
        dto.setResult(report.getResult());
        dto.setNotes(report.getNotes());

        dto.setItems(
                expertItemRepository.findByReportId(report.getId()).stream()
                        .map(i -> new ExpertItemDto(i.getPart(), i.getStatus(), i.getNote()))
                        .collect(java.util.stream.Collectors.toList())
        );


        java.util.Set<com.example.otoportdeneme.Enums.CarPart> existing =
                dto.getItems().stream().map(ExpertItemDto::getPart).collect(java.util.stream.Collectors.toSet());

        for (com.example.otoportdeneme.Enums.CarPart p : com.example.otoportdeneme.Enums.CarPart.values()) {
            if (!existing.contains(p)) {
                dto.getItems().add(new ExpertItemDto(
                        p,
                        com.example.otoportdeneme.Enums.PartStatus.ORIGINAL,
                        "Varsayılan: parça kaydı girilmedi"
                ));
            }
        }

        // İstersen UI'da sabit sıralı görünsün diye:
        dto.getItems().sort(java.util.Comparator.comparing(a -> a.getPart().name()));

        return dto;
    }

}
