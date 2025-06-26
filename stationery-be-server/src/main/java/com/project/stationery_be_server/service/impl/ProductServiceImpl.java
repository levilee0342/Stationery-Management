package com.project.stationery_be_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeleteProductRequest;
import com.project.stationery_be_server.dto.request.ProductFilterRequest;
import com.project.stationery_be_server.dto.request.UpdateProductRequest;
import com.project.stationery_be_server.dto.request.UserRequest;
import com.project.stationery_be_server.dto.response.ColorSizeSlugResponse;
import com.project.stationery_be_server.dto.response.product.CreateProductRequest;
import com.project.stationery_be_server.dto.response.product.ProductDetailRequest;
import com.project.stationery_be_server.entity.*;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.product.ProductResponse;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


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
    CategoryRepository categoryRepository;
    SizeRepository sizeRepository;
    ColorRepository colorRepository;
    Cloudinary cloudinary;

    @Override
    public Page<ProductResponse> getAllProductWithDefaultPD(Pageable pageable, ProductFilterRequest filter) {
        Specification<Product> spec = ProductSpecification.filterProductsForUser(filter);
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
    public Page<ProductResponse> getAllProductForAdmin(Pageable pageable, ProductFilterRequest filter) {
        Specification<Product> spec = ProductSpecification.filterProductsForAdmin(filter);
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
    // user
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, ProductFilterRequest filter) {
        Specification<Product> spec = ProductSpecification.filterProductsForUser(filter);
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
    // admin
    @Override
    public List<ProductDetailResponse> getProductDetailByProduct(String productId) {
        List<ProductDetail> pd = productDetailRepository.findByProduct_ProductId(productId);

        List<ProductDetailResponse> pdsResponse = pd.stream()
                .map(productDetail -> {
                    if (productDetail.getColor() != null) {
                        String colorId = productDetail.getColor().getColorId();
                        productDetail.setImages(imageRepository.findByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(productId, colorId));
                    } else {
                        // Xử lý trường hợp không có ColorId, lấy tất cả ảnh của sản phẩm
                        productDetail.setImages(imageRepository.findByProduct_ProductIdOrderByPriorityAsc(productId));
                    }

                    return productDetailMapper.toProductDetailResponse(productDetail);
                })
                .toList();

        return pdsResponse;
    }
    @Override
    public List<ProductResponse> getAllProductsForChatbot() {
        List<Product> products = productRepository.findAll();

        return products.stream()
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
        if (!user.getRole().getRoleName().equals("admin")) {
            throw new RuntimeException("You do not have permission to delete products");
        }
        //ktra san pham ton tai
        String productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Cannot find the product"));

        //kiem tra co data lien quan khong
        long reviews = reviewRepository.countByProduct_ProductId(productId);
        long images = imageRepository.countByProduct_ProductId(productId);
        long details = productDetailRepository.countByProduct_ProductId(productId);

        if (reviews > 0) {
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

    // đamg lỗi khi chưa có lưu product
    @Override
    @Transactional
    public void createProduct(String documentJson, MultipartHttpServletRequest files) {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateProductRequest request = null;
        try {
            request = objectMapper.readValue(documentJson, CreateProductRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product" + request.getName() + " already exists");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(NotExistedErrorCode.CATEGORY_NOT_EXISTED));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSlug(request.getSlug());
        product.setCategory(category);
        product.setSoldQuantity(0);
        product.setTotalRating(0.0);
        int currentQuantity = 0;
        ArrayList<String> fileKeys = new ArrayList<>();
        ArrayList<Color> colors = new ArrayList<>();
        List<ProductDetail> productDetails = new ArrayList<>();
        for (ProductDetailRequest detailRequest : request.getProductDetails()) {
            if (productDetailRepository.existsByName(detailRequest.getName())) {
                throw new RuntimeException("Product detail" + detailRequest.getName() + " already exists");
            }
            if (productDetailRepository.existsBySlug(detailRequest.getSlug())) {
                throw new RuntimeException("Product detail" + detailRequest.getSlug() + " already exists");
            }
            ProductDetail productDetail = new ProductDetail();
            productDetail.setName(detailRequest.getName()); // You can adjust this logic
            productDetail.setSlug(detailRequest.getSlug());
            productDetail.setOriginalPrice((int) detailRequest.getOriginalPrice());
            productDetail.setDiscountPrice((int) detailRequest.getDiscountPrice());
            currentQuantity += detailRequest.getStockQuantity();
            productDetail.setStockQuantity(detailRequest.getStockQuantity());
            productDetail.setAvailableQuantity(detailRequest.getStockQuantity());
            productDetail.setSoldQuantity(0);
            // Set size and color
            if (!detailRequest.getSizeId().isBlank()) {
                Size size = sizeRepository.findById(detailRequest.getSizeId())
                        .orElseThrow(() -> new AppException(NotExistedErrorCode.SIZE_NOT_EXISTED));
                productDetail.setSize(size);

            }

            Color color = colorRepository.findById(detailRequest.getColorId())
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.COLOR_NOT_EXISTED));

            productDetail.setColor(color);
            productDetail.setProduct(product); // Set back-reference
            productDetails.add(productDetail);

            String fileKey = "files_" + color.getColorId();
            fileKeys.add(fileKey);
            colors.add(color);
        }

        product.setQuantity(currentQuantity);
        product.setProductDetails(new HashSet<>(productDetails));
        product.setProductDetail(productDetails.get(0));
        productRepository.save(product);
        for (int i = 0; i < fileKeys.size(); i++) {
            Color color = colors.get(i);
            String fileKey = fileKeys.get(i);
            List<MultipartFile> imageFiles = files.getFiles(fileKey);
            int priority = 0;
            for (MultipartFile file : imageFiles) {
                try {
                    var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    String url = uploadResult.get("secure_url").toString();
                    Image image = Image.builder()
                            .url(url)
                            .priority(priority++)
                            .product(product)
                            .color(color)
                            .build();
                    imageRepository.save(image);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
        }
    }

    @Override
    public ProductResponse updateProduct(UpdateProductRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(NotExistedErrorCode.PRODUCT_NOT_EXISTED));

        if (request.getName() != null && !request.getName().equals(product.getName())) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().equals(product.getDescription())) {
            product.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null && !request.getCategoryId().isBlank()) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.CATEGORY_NOT_EXISTED));
            if (!category.equals(product.getCategory())) {
                product.setCategory(category);
            }
        }

        productRepository.save(product);
        Image img;
        if (product.getProductDetail().getColor() != null) {
            img = imageRepository.findFirstByProduct_ProductIdAndColor_ColorIdOrderByPriorityAsc(product.getProductId(), product.getProductDetail().getColor().getColorId());
        } else {
            img = imageRepository.findFirstByProduct_ProductIdAndColorIsNullOrderByPriorityAsc(product.getProductId());
        }
        product.setImg(img != null ? img.getUrl() : null);
        return productMapper.toProductResponse(product);
    }

    @Override
    public Boolean updateHiddenProduct(String productId, boolean isHidden) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        product.setHidden(isHidden);
        productRepository.save(product);
        return isHidden;
    }


}
