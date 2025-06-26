package com.project.stationery_be_server.specification;

import com.project.stationery_be_server.entity.Color;
import com.project.stationery_be_server.entity.ProductPromotion;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductPromotionSpecification {

        public static Specification<ProductPromotion> filterProductPromotion(String  search) {
            return (root, query, criteriaBuilder) -> {
                query.distinct(true);
                List<Predicate> predicates = new ArrayList<>();

                // Filter theo roleId
                if (search != null && !search.trim().isEmpty()) {
                    String keyword = "%" + search.trim().toLowerCase() + "%";
                    Predicate byName = criteriaBuilder.like(criteriaBuilder.lower(root.get("product_promotion_id")), keyword);
                    predicates.add(byName);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }

}
