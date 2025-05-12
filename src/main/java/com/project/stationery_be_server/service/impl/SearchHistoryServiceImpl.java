package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.response.CategoryResponse;
import com.project.stationery_be_server.entity.Category;
import com.project.stationery_be_server.entity.Product;
import com.project.stationery_be_server.entity.ProductDetail;
import com.project.stationery_be_server.entity.SearchHistory;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.repository.CategoryRepository;
import com.project.stationery_be_server.repository.ProductDetailRepository;
import com.project.stationery_be_server.repository.ProductRepository;
import com.project.stationery_be_server.repository.SearchHistoryRepository;
import com.project.stationery_be_server.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<String> getTop10Keywords() {
        List<Object[]> result = searchHistoryRepository.findTop10Keywords();
        return result.stream()
                .limit(8)
                .map(item -> (String) item[0])
                .collect(Collectors.toList());
    }

    @Override
    public void logKeyword(String keyword, String userId) {
        if (keyword == null || keyword.isBlank()) return;

        SearchHistory history = new SearchHistory();
        history.setKeyword(keyword.trim().toLowerCase());
        history.setCreatedAt(LocalDateTime.now());

        // Gán user nếu có
        if (userId != null) {
            User user = new User();
            user.setUserId(userId);
            history.setUser(user);
        }

        searchHistoryRepository.save(history);

        if (userId != null) {
            // Đã đăng nhập: xóa nếu > 30, giữ lại 5 mới nhất
            List<SearchHistory> userHistories = searchHistoryRepository.findByUserIdOrderByCreatedAtAsc(userId);
            if (userHistories.size() > 30) {
                List<SearchHistory> toDelete = userHistories.subList(0, userHistories.size() - 5);
                searchHistoryRepository.deleteAll(toDelete);
            }
        } else {
            // Chưa đăng nhập: nếu tổng lịch sử ẩn danh > 50 thì xóa hết
            List<SearchHistory> anonymousHistories = searchHistoryRepository.findByUserIsNull();
            if (anonymousHistories.size() > 50) {
                searchHistoryRepository.deleteAll(anonymousHistories);
            }
        }
    }

    @Override
    public List<CategoryResponse> getTop8UserCategories() {
        List<Object[]> keywordResults = searchHistoryRepository.findTop10Keywords();
        return keywordResults.stream()
                .limit(8)
                .map(item -> matchKeywordToCategory((String) item[0]))
                .filter(categoryResponse -> categoryResponse != null)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getTop24HotCategories() {
        List<Object[]> keywordResults = searchHistoryRepository.findTop10Keywords();
        return keywordResults.stream()
                .limit(20)
                .map(item -> matchKeywordToCategory((String) item[0]))
                .filter(categoryResponse -> categoryResponse != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private CategoryResponse matchKeywordToCategory(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        // Tìm ProductDetail khớp với từ khóa dựa trên trường name
        List<ProductDetail> productDetails = productDetailRepository.findByKeyword(keyword);
        if (productDetails.isEmpty()) return null;

        // Lấy Product đầu tiên (giả sử một từ khóa có thể khớp với nhiều ProductDetail)
        ProductDetail productDetail = productDetails.get(0);
        Product product = productRepository.findById(productDetail.getProduct().getProductId())
                .orElse(null);
        if (product == null) return null;

        // Lấy Category
        Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                .orElse(null);
        if (category == null) return null;

        // Chuyển đổi Category thành CategoryResponse
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .icon(category.getIcon())
                .bgColor(category.getBgColor())
                .build();
    }
}