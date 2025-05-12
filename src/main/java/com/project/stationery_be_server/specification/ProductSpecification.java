    package com.project.stationery_be_server.specification;

    import com.project.stationery_be_server.dto.request.ProductFilterRequest;
    import com.project.stationery_be_server.entity.Product;
    import com.project.stationery_be_server.entity.ProductDetail;
    import jakarta.persistence.criteria.Join;
    import jakarta.persistence.criteria.JoinType;
    import jakarta.persistence.criteria.Predicate;
    import org.springframework.data.jpa.domain.Specification;

    import java.util.ArrayList;
    import java.util.List;

    public class ProductSpecification {
        public static Specification<Product> filterProducts(ProductFilterRequest filter) {
            return (root, query, criteriaBuilder) -> {
                //predicates là danh sách các điều kiện (WHERE ...).
                assert query != null;
                query.distinct(true);

                List<Predicate> predicates = new ArrayList<>();
                // Join Product →  ProductDetail
                Join<Object, Object> productDetailJoin = null;
                System.out.println("filter.getMinPrice() = " + filter.getMinPrice());
                System.out.println("filter.getMaxPrice() = " + filter.getMaxPrice());
                if ((filter.getMinPrice() != null && !filter.getMinPrice().isBlank()) ||
                    (filter.getMaxPrice() != null && !filter.getMaxPrice().isBlank())) {
                    productDetailJoin = root.join("productDetails", JoinType.INNER);
                }

                if (filter.getCategoryId() != null && !filter.getCategoryId().isBlank()) {
                    predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), filter.getCategoryId()));
                }

                if (filter.getMinPrice() != null && productDetailJoin!=null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(productDetailJoin.get("discountPrice"), filter.getMinPrice()));
                }

                if (filter.getMaxPrice() != null && productDetailJoin!=null ) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(productDetailJoin.get("discountPrice"), filter.getMaxPrice()));
                }

                if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                    String searchKeyword = filter.getSearch().trim().toLowerCase();
                    List<Predicate> searchPredicates = new ArrayList<>();

                    searchPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + searchKeyword + "%"
                    ));

                    if (searchKeyword.endsWith("s")) {
                        String singular = searchKeyword.substring(0, searchKeyword.length() - 1);
                        searchPredicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + singular + "%"
                        ));
                    }

                    predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
                }

                if (filter.getTotalRating() != null && !filter.getTotalRating().isBlank()) {
                    predicates.add(criteriaBuilder.equal(root.get("totalRating"), filter.getTotalRating()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }