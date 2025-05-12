package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeleteProductRequest;
import com.project.stationery_be_server.dto.request.ProductFilterRequest;
import com.project.stationery_be_server.dto.response.ColorSizeSlugResponse;
import com.project.stationery_be_server.entity.Image;
import com.project.stationery_be_server.entity.Product;
import com.project.stationery_be_server.entity.ProductDetail;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.product.ProductResponse;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.mapper.ProductDetailMapper;
import com.project.stationery_be_server.mapper.ProductMapper;
import com.project.stationery_be_server.repository.*;
import com.project.stationery_be_server.service.ProductService;
import com.project.stationery_be_server.specification.ProductSpecification;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    ImageRepository imageRepository;
    ProductDetailRepository productDetailRepository;
    ProductMapper productMapper;
    ProductDetailMapper productDetailMapper;
    UserRepository userRepository;

    @Override
    public Page<ProductResponse> getAllProductWithDefaultPD(Pageable pageable, ProductFilterRequest filter) {
        Specification<Product> spec = ProductSpecification.filterProducts(filter);
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        List<ProductResponse> productListResponses = productsPage.getContent().stream()
                .map(product -> {
                    String colorId = null;
                    ProductDetail productDetail = product.getProductDetail();
                    if (productDetail != null && productDetail.getColor() != null) {
                        colorId = productDetail.getColor().getColorId();
                    }

                    product.setFetchColor(productDetailRepository.findDistinctColorsWithAnySlug(product.getProductId()));
                    Image img;
                    if (colorId != null) {
                        img = imageRepository.findFirstByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(product.getProductId(), colorId);
                    } else {
                        img = imageRepository.findFirstByProduct_ProductIdAndColorIsNullOrderByPriorityAsc(product.getProductId());
                    }
                    product.setImg(img != null ? img.getUrl() : null);
                    return productMapper.toProductResponse(product);
                })
                .toList();

        return new PageImpl<>(productListResponses, pageable, productsPage.getTotalElements());
    }


    @Override
    public ProductResponse getProductDetail(String slug) {
        ProductDetail pd = productDetailRepository.findBySlug(slug);
        String productId = pd.getProduct().getProductId();
        if (pd.getColor() != null && pd.getColor().getColorId() != null) {
            pd.setImages(imageRepository.findByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(productId, pd.getColor().getColorId()));
        } else {
            // Xử lý trường hợp không có ColorId, lấy tất cả ảnh của sản phẩm
            pd.setImages(imageRepository.findByProduct_ProductIdOrderByPriorityAsc(productId));
        }
        Product p = productRepository.findById(productId).orElseThrow(() -> new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED));
        p.setProductDetail(pd);
        return productMapper.toProductResponse(p);
    }

    @Override
    public List<ColorSizeSlugResponse> fetchColorSizeSlug(String slug) {
        return productDetailRepository.fetchColorSizeBySLug(slug);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, ProductFilterRequest filter) {
        Specification<Product> spec = ProductSpecification.filterProducts(filter);
        Page<Product> p = productRepository.findAll(spec, pageable);
        List<ProductResponse> productListResponses = p.getContent().stream()
                .map(product -> {
                    String colorId = null;
                    ProductDetail productDetail = product.getProductDetail();
                    if (productDetail != null && productDetail.getColor() != null) {
                        colorId = productDetail.getColor().getColorId();
                    }
                    product.setProductDetail(null);
                    product.setFetchColor(productDetailRepository.findDistinctColorsWithAnySlug(product.getProductId()));
                    Image img;
                    if (colorId != null) {
                        img = imageRepository.findFirstByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(product.getProductId(), colorId);
                    } else {
                        img = imageRepository.findFirstByProduct_ProductIdAndColorIsNullOrderByPriorityAsc(product.getProductId());
                    }
                    product.setImg(img != null ? img.getUrl() : null);
                    return productMapper.toProductResponse(product);
                })
                .toList();
        return new PageImpl<>(productListResponses, pageable, p.getTotalElements());
    }

    @Override
    public List<ProductDetailResponse> getProductDetailByProduct(String productId) {
        List<ProductDetail> pd = productDetailRepository.findByProduct_ProductId(productId);

        List<ProductDetailResponse> pdsResponse = pd.stream()
                .map(productDetail -> {
                    productDetail.setImages(imageRepository.findByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(productId, productDetail.getColor().getColorId()));
                    return productDetailMapper.toProductDetailResponse(productDetail);
                })
                .toList();

        return pdsResponse;
    }

    @Override
    @Transactional
    public void handleUpdateTotalProductRating(String productId, String type, Integer rating) {
        int countRating = reviewRepository.countByProductId(productId);
        int sumRating = reviewRepository.sumRatingByProductId(productId);

        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED));
        int length = countRating;
        if (type.equalsIgnoreCase("create")) {
            length += 1;
        } else if (type.equalsIgnoreCase("update")) {

        } else if (type.equalsIgnoreCase("delete")) {
            length -= 1;
        } else {
            throw new IllegalArgumentException("Type must be create, update or delete");
        }
        if (length == 0) { //  k có rating
            product.setTotalRating(0.0);

        } else {
            double totalRating = (double) (sumRating + rating) / length;
            product.setTotalRating(totalRating);
        }
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(DeleteProductRequest request) {
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")){
            throw new RuntimeException("You do not have permission to delete products");
        }
        //ktra san pham ton tai
        String productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Cannot find the product"));

        //kiem tra co data lien quan khong
        long reviews   = reviewRepository.countByProduct_ProductId(productId);
        long images    = imageRepository.countByProduct_ProductId(productId);
        long details   = productDetailRepository.countByProduct_ProductId(productId);

        if (reviews > 0 ) {
            throw new IllegalStateException(
                    String.format("Cannot delete product %s: has %d reviews",
                            productId, reviews)
            );
        }
        if (images > 0) {
            throw new IllegalStateException(
                    String.format("Cannot delete product %s: has %d images",
                            productId, images)
            );
        }
        if (details > 0) {
            throw new IllegalStateException(
                    String.format("Cannot delete product %s: has %d details",
                            productId, details)
            );
        }
        //xoa
        productRepository.deleteById(productId);
    }

}
