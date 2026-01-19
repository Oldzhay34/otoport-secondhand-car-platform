package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FuelType;
import com.example.otoportdeneme.Enums.Transmission;
import com.example.otoportdeneme.dto_Requests.StoreListingCreateRequest;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class StoreCreateListingServiceImpl implements StoreCreateListingService {

    private static final int MAX_IMAGES = 10;

    private final StoreRepository storeRepository;
    private final ListingRepository listingRepository;

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;
    private final TrimRepository trimRepository;

    public StoreCreateListingServiceImpl(StoreRepository storeRepository,
                                         ListingRepository listingRepository,
                                         BrandRepository brandRepository,
                                         CarModelRepository carModelRepository,
                                         TrimRepository trimRepository) {
        this.storeRepository = storeRepository;
        this.listingRepository = listingRepository;
        this.brandRepository = brandRepository;
        this.carModelRepository = carModelRepository;
        this.trimRepository = trimRepository;
    }

    @Override
    @Transactional
    public Listing createMyListing(Long storeId, StoreListingCreateRequest req, List<MultipartFile> images) {

        if (req == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request boş olamaz.");

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        // ---------- request basic validation ----------
        if (isBlank(req.getTitle())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title zorunlu.");
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price zorunlu ve > 0 olmalı.");

        if (isBlank(req.getCurrency()) || req.getCurrency().trim().length() != 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency 3 harf olmalı (TRY).");

        if (isBlank(req.getCity())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "city zorunlu.");

        if (isBlank(req.getBrand())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand zorunlu.");
        if (isBlank(req.getModel())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "model zorunlu.");
        if (req.getYear() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "year zorunlu.");
        if (req.getKilometer() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "kilometer zorunlu.");

        // enums zorunlu
        Transmission transmission = parseEnum(Transmission.class, req.getTransmission(), "transmission");
        FuelType fuelType = parseEnum(FuelType.class, req.getFuelType(), "fuelType");
        BodyType bodyType = parseEnum(BodyType.class, req.getBodyType(), "bodyType");

        // ---------- images validation ----------
        List<MultipartFile> safeImages = (images == null) ? List.of() : images.stream()
                .filter(f -> f != null && !f.isEmpty())
                .toList();

        if (safeImages.size() > MAX_IMAGES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "En fazla 10 resim yükleyebilirsin.");
        }

        // ---------- Brand / Model / Trim resolve ----------
        Brand brand = brandRepository.findByNameIgnoreCase(req.getBrand().trim())
                .orElseGet(() -> {
                    Brand b = new Brand();
                    b.setName(req.getBrand().trim());
                    return brandRepository.save(b);
                });

        CarModel model = carModelRepository.findByBrandIdAndNameIgnoreCase(brand.getId(), req.getModel().trim())
                .orElseGet(() -> {
                    CarModel m = new CarModel();
                    m.setBrand(brand);
                    m.setName(req.getModel().trim());
                    return carModelRepository.save(m);
                });

        String trimName = buildTrimName(req);

        Trim trim = trimRepository.findByModelIdAndNameIgnoreCase(model.getId(), trimName)
                .orElseGet(() -> {
                    Trim t = new Trim();
                    t.setModel(model);
                    t.setName(trimName);
                    return trimRepository.save(t);
                });

        // ---------- Car ----------
        Car car = new Car();
        car.setTrim(trim);
        car.setYear(req.getYear());
        car.setKilometer(req.getKilometer());
        car.setColor(blankToNull(req.getColor()));
        car.setEngineVolumeCc(req.getEngineVolumeCc());
        car.setEnginePowerHp(req.getEnginePowerHp());
        car.setTransmission(transmission);
        car.setFuelType(fuelType);
        car.setBodyType(bodyType);

        // ---------- Listing ----------
        Listing listing = new Listing();
        listing.setStore(store);
        listing.setCar(car);

        listing.setTitle(req.getTitle().trim());
        listing.setDescription(blankToNull(req.getDescription()));
        listing.setPrice(req.getPrice());
        listing.setCurrency(req.getCurrency().trim().toUpperCase(Locale.ROOT));
        listing.setNegotiable(req.getNegotiable() == null ? Boolean.TRUE : req.getNegotiable());
        listing.setCity(req.getCity().trim());
        listing.setDistrict(blankToNull(req.getDistrict()));

        // ---------- Save images ----------
        if (!safeImages.isEmpty()) {
            List<String> savedPaths = saveToStaticUploads(safeImages);
            int sortOrder = 1;

            for (int i = 0; i < savedPaths.size(); i++) {
                ListingImage img = new ListingImage();
                img.setImagePath(savedPaths.get(i));
                img.setSortOrder(sortOrder++);
                img.setIsCover(i == 0);
                listing.addImage(img);
            }
        }

        return listingRepository.save(listing);
    }

    private String buildTrimName(StoreListingCreateRequest req) {
        String variant = blankToNull(req.getVariant());
        String engine = blankToNull(req.getEngine());
        String pkg = blankToNull(req.getCarPackage());

        // en anlamlı kombinasyon
        // Variant + Engine + Package gibi
        List<String> parts = new ArrayList<>();
        if (variant != null) parts.add(variant);
        if (engine != null) parts.add(engine);
        if (pkg != null) parts.add(pkg);

        if (!parts.isEmpty()) return String.join(" - ", parts);

        // en kötü model adı trim olsun
        return req.getModel().trim();
    }

    private <E extends Enum<E>> E parseEnum(Class<E> cls, String raw, String field) {
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " zorunlu.");
        }
        try {
            return Enum.valueOf(cls, raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " geçersiz: " + raw);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isBlank();
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private List<String> saveToStaticUploads(List<MultipartFile> files) {
        Path dir = Paths.get("src/main/resources/static/uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Uploads klasörü oluşturulamadı.");
        }

        List<String> out = new ArrayList<>();
        for (MultipartFile f : files) {
            String original = Optional.ofNullable(f.getOriginalFilename()).orElse("image");
            String ext = getSafeExtension(original);
            String fileName = "img_" + Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + ext;

            Path target = dir.resolve(fileName).normalize();
            try (InputStream in = f.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resim kaydedilemedi: " + original);
            }
            out.add("/uploads/" + fileName);
        }
        return out;
    }

    private String getSafeExtension(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) return ".png";
        if (lower.endsWith(".jpg")) return ".jpg";
        if (lower.endsWith(".jpeg")) return ".jpeg";
        if (lower.endsWith(".webp")) return ".webp";
        return "";
    }
}
