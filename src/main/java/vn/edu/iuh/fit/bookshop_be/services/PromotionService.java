package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.Promotion;
import vn.edu.iuh.fit.bookshop_be.repositories.PromotionRepository;

import java.util.List;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
    }

    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Integer id, Promotion promotion) {
        if (promotionRepository.existsById(id)) {
            promotion.setId(id);
            return promotionRepository.save(promotion);
        }
        return null; // or throw an exception
    }

    public void deletePromotion(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Promotion not found with id: " + id);
        }
    }




}
