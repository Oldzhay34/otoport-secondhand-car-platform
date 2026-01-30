package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.ExpertItemDto;
import com.example.otoportdeneme.dto_Objects.ExpertReportDto;
import com.example.otoportdeneme.models.Car;
import com.example.otoportdeneme.models.ExpertItem;
import com.example.otoportdeneme.models.ExpertReport;
import com.example.otoportdeneme.repositories.CarRepository;
import com.example.otoportdeneme.repositories.ExpertItemRepository;
import com.example.otoportdeneme.repositories.ExpertReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExpertReportServiceImpl implements ExpertReportService {

    private final ExpertReportRepository expertReportRepository;
    private final ExpertItemRepository expertItemRepository;
    private final CarRepository carRepository;

    @PersistenceContext
    private EntityManager em;

    public ExpertReportServiceImpl(
            ExpertReportRepository expertReportRepository,
            ExpertItemRepository expertItemRepository,
            CarRepository carRepository
    ) {
        this.expertReportRepository = expertReportRepository;
        this.expertItemRepository = expertItemRepository;
        this.carRepository = carRepository;
    }

    @Override
    public ExpertReportDto getByCarId(Long carId) {
        var opt = expertReportRepository.findByCar_Id(carId);

        if (opt.isEmpty()) {
            ExpertReportDto dto = new ExpertReportDto();
            dto.setCarId(carId);
            dto.setResult(com.example.otoportdeneme.Enums.ExpertResult.UNKNOWN);

            var items = new java.util.ArrayList<ExpertItemDto>();
            for (var p : com.example.otoportdeneme.Enums.CarPart.values()) {
                items.add(new ExpertItemDto(
                        p,
                        com.example.otoportdeneme.Enums.PartStatus.ORIGINAL,
                        "Varsayılan"
                ));
            }
            dto.setItems(items);
            return dto;
        }

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
                        .toList()
        );

        return dto;
    }

    @Override
    @Transactional
    public ExpertReportDto upsertByCarId(Long carId, ExpertReportDto dto) {

        if (carId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "carId required");

        ExpertReport report = expertReportRepository.findByCarIdForUpdate(carId)
                .orElseGet(() -> {
                    Car car = carRepository.findById(carId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

                    ExpertReport r = new ExpertReport();
                    r.setCar(car);
                    return expertReportRepository.saveAndFlush(r); // ⬅️ persist
                });

        report.setCompanyName(dto.getCompanyName());
        report.setReportDate(dto.getReportDate());
        report.setReportNo(dto.getReportNo());
        report.setResult(
                dto.getResult() == null
                        ? com.example.otoportdeneme.Enums.ExpertResult.UNKNOWN
                        : dto.getResult()
        );
        report.setNotes(dto.getNotes());

        // =========================
        // 🔥 PART BAZLI UPSERT
        // =========================
        List<ExpertItem> existingItems =
                expertItemRepository.findByReportId(report.getId());

        var byPart = existingItems.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ExpertItem::getPart,
                        i -> i
                ));

        List<ExpertItemDto> incoming =
                dto.getItems() == null ? List.of() : dto.getItems();

        for (ExpertItemDto it : incoming) {
            if (it == null || it.getPart() == null || it.getStatus() == null) continue;

            ExpertItem ei = byPart.get(it.getPart());

            if (ei == null) {

                ei = new ExpertItem();
                ei.setReport(report);
                ei.setPart(it.getPart());
            }

            ei.setStatus(it.getStatus());
            ei.setNote(it.getNote());

            expertItemRepository.save(ei);
        }

        return getByCarId(carId);
    }

}
