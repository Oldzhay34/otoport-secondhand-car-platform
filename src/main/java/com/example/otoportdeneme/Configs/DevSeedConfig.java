package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FuelType;
import com.example.otoportdeneme.Enums.ListingStatus;
import com.example.otoportdeneme.Enums.Transmission;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DevSeedConfig {

    private static final String STORE_EMAIL = "kiziler@otoport.com";

    @Bean
    CommandLineRunner seedKizilerOtomotiv(
            StoreRepository storeRepo,
            BrandRepository brandRepo,
            CarModelRepository modelRepo,
            TrimRepository trimRepo,
            CarRepository carRepo,
            ListingRepository listingRepo,
            UserAccountRepository userAccountRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // ✅ Eğer zaten listing varsa tekrar basma (dev seed idempotent)
            if (listingRepo.count() > 0) {
                System.out.println("ℹ️ Seed skip: listings already exist.");
                return;
            }

            // ✅ 1) Store kullanıcı var mı?
            Store store = null;

            var existingUserOpt = userAccountRepo.findByEmail(STORE_EMAIL);
            if (existingUserOpt.isPresent()) {
                Long uid = existingUserOpt.get().getId();

                store = storeRepo.findById(uid).orElse(null);
                if (store == null) {
                    System.out.println("⚠️ User var ama Store yok. Seed durduruldu. uid=" + uid);
                    return;
                }

                System.out.println("ℹ️ Seed: existing store found. id=" + store.getId());
            } else {
                // ✅ Store yoksa oluştur
                Store s = new Store();
                s.setEmail(STORE_EMAIL);

                // ✅ DOĞRU: passwordHash set et
                s.setPasswordHash(passwordEncoder.encode("123456"));

                s.setStoreName("Kızıler Otomotiv");
                s.setCity("İstanbul");
                s.setDistrict("Esenyurt");
                s.setPhone("0xxxxxxxxxxxxxxxxx");

                store = storeRepo.save(s);
                System.out.println("✅ Seed: store created. id=" + store.getId());
            }

            // 2) Brand: Audi
            Brand brand = getOrCreateBrand(brandRepo, "Audi");

            // 3) Model: A3
            CarModel a3 = modelRepo.findByNameIgnoreCaseAndBrandId("A3", brand.getId())
                    .orElseGet(() -> {
                        CarModel m = new CarModel();
                        m.setName("A3");
                        m.setBrand(brand);
                        return modelRepo.save(m);
                    });

            // 4) Trim listesi
            List<String> trimNames = List.of(
                    "30 TDI Design",
                    "30 TDI Dynamic",
                    "30 TDI Sport",
                    "30 TFSI Advanced",
                    "30 TFSI S Line",
                    "35 TFSI Design"
            );

            int year = 2022;
            int km = 55000;

            for (int i = 0; i < trimNames.size(); i++) {
                String trimName = trimNames.get(i);

                Trim trim = getOrCreateTrim(trimRepo, a3, trimName);

                Car car = new Car();
                car.setTrim(trim);
                car.setYear(year);
                car.setKilometer(km + (i * 3500));
                car.setTransmission(Transmission.AUTOMATIC);
                car.setFuelType(trimName.contains("TDI") ? FuelType.DIESEL : FuelType.GASOLINE);
                car.setBodyType(BodyType.SEDAN);
                car.setColor("Beyaz");
                car = carRepo.save(car);

                Listing listing = new Listing();
                listing.setStore(store);
                listing.setCar(car);
                listing.setTitle(year + " Audi A3 Sedan " + trimName);
                listing.setDescription("Kızıler Otomotiv güvencesiyle. A3 Sedan " + trimName);
                listing.setCity("İstanbul");
                listing.setDistrict("Esenyurt");
                listing.setPrice(new BigDecimal("1350000").add(new BigDecimal(i * 35000)));
                listing.setNegotiable(true);
                listing.setStatus(ListingStatus.ACTIVE);

                listingRepo.save(listing);
            }

            System.out.println("✅ Seed OK: Kızıler Otomotiv + A3 Sedan ilanları eklendi.");
        };
    }

    private Brand getOrCreateBrand(BrandRepository brandRepo, String name) {
        String n = name.trim();

        var existing = brandRepo.findByNameIgnoreCase(n);
        if (existing.isPresent()) return existing.get();

        try {
            Brand b = new Brand();
            b.setName(n);
            return brandRepo.saveAndFlush(b);
        } catch (DataIntegrityViolationException e) {
            return brandRepo.findByNameIgnoreCase(n)
                    .orElseThrow(() -> new IllegalStateException(
                            "Brand duplicate but cannot load by exact name: [" + n + "]. " +
                                    "brands tablosunu kontrol et.", e));
        }
    }

    private Trim getOrCreateTrim(TrimRepository trimRepo, CarModel model, String trimName) {
        String n = trimName.trim();
        Long mid = model.getId();

        var existing = trimRepo.findByNameIgnoreCaseAndModelId(n, mid);
        if (existing.isPresent()) return existing.get();

        try {
            Trim t = new Trim();
            t.setName(n);
            t.setModel(model);
            return trimRepo.saveAndFlush(t);
        } catch (DataIntegrityViolationException e) {
            return trimRepo.findByNameAndModelId(n, mid)
                    .orElseThrow(() -> e);
        }
    }
}
