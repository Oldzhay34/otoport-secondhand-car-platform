package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.Brand;
import com.example.otoportdeneme.models.CarModel;
import com.example.otoportdeneme.models.Trim;
import com.example.otoportdeneme.repositories.BrandRepository;
import com.example.otoportdeneme.repositories.CarModelRepository;
import com.example.otoportdeneme.repositories.TrimRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogResolveService {

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;
    private final TrimRepository trimRepository;

    public CatalogResolveService(
            BrandRepository brandRepository,
            CarModelRepository carModelRepository,
            TrimRepository trimRepository
    ) {
        this.brandRepository = brandRepository;
        this.carModelRepository = carModelRepository;
        this.trimRepository = trimRepository;
    }

    /* ================= BRAND ================= */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Brand getOrCreateBrandNewTx(String rawName) {
        String name = normalize(rawName);
        String key  = CatalogKey.keyOf(name);

        brandRepository.upsertBrand(name, key);

        return brandRepository.findByNameKey(key)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Brand resolve edilemedi: " + name
                ));
    }



    /* ================= MODEL ================= */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CarModel getOrCreateModelNewTx(Long brandId, String rawModel){
        String name = normalize(rawModel);
        String key  = CatalogKey.keyOf(name);

        carModelRepository.upsertModel(brandId, name, key);

        return carModelRepository.findByBrandIdAndNameKey(brandId, key)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Model resolve edilemedi: " + name
                ));
    }


    /* ================= TRIM ================= */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Trim getOrCreateTrimNewTx(Long modelId, String rawTrim) {

        String name = normalize(rawTrim);
        String key  = CatalogKey.keyOf(name);

        // DB-side idempotent (duplicate-safe)
        trimRepository.upsertTrim(modelId, name, key);

        return trimRepository.findByModelIdAndNameKey(modelId, key)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Trim resolve edilemedi: " + name
                ));
    }


    /* ================= UTIL ================= */

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }
}
