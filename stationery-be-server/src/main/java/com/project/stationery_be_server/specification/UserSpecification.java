package com.project.stationery_be_server.specification;

import com.project.stationery_be_server.dto.request.UserFilterRequest;
import com.project.stationery_be_server.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsersForUser(UserFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo roleId
            if (filter.getRoleId() != null && !filter.getRoleId().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("role").get("roleId"), filter.getRoleId()));
            }

            // Tìm kiếm theo tên đầy đủ
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String searchKeyword = filter.getSearch().toLowerCase();
                List<Predicate> searchPredicates = new ArrayList<>();

                String[] keywords = searchKeyword.split("\\s+");
                for (String keyword : keywords) {
                    if (!keyword.isBlank()) {
                        searchPredicates.add(
                                criteriaBuilder.or(
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("firstName"), "")),
                                                "%" + keyword + "%"
                                        ),
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("lastName"), "")),
                                                "%" + keyword + "%"
                                        )
                                )
                        );
                    }
                }

                if (!searchPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
