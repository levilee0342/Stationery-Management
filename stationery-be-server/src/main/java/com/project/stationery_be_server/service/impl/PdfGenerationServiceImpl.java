package com.project.stationery_be_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.PurchaseOrderDetail;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.PurchaseOrderRepository;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PdfGenerationServiceImpl implements PdfGenerationService {
    private final Cloudinary cloudinary;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(PdfGenerationServiceImpl.class.getName());

    // Chuyển đổi số thành chữ tiếng Anh
    private String numberToEnglishWords(long number) {
        if (number == 0) return "Zero VND";

        String[] units = {"", "thousand", "million", "billion"};
        String[] digits = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        String[] teens = {"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
        String[] tens = {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

        StringBuilder result = new StringBuilder();
        int unitIndex = 0;

        while (number > 0) {
            if (number % 1000 != 0) {
                StringBuilder part = new StringBuilder();
                int hundreds = (int) (number % 1000);
                if (hundreds >= 100) {
                    part.append(digits[hundreds / 100]).append(" hundred");
                    hundreds %= 100;
                    if (hundreds > 0) part.append(" and ");
                }
                if (hundreds >= 10 && hundreds < 20) {
                    part.append(teens[hundreds - 10]);
                } else if (hundreds >= 20) {
                    part.append(tens[hundreds / 10]);
                    if (hundreds % 10 > 0) part.append("-").append(digits[hundreds % 10]);
                } else if (hundreds > 0) {
                    part.append(digits[hundreds]);
                }
                if (part.length() > 0) {
                    if (result.length() > 0) result.insert(0, ", ");
                    result.insert(0, part.append(" ").append(units[unitIndex]));
                }
            }
            number /= 1000;
            unitIndex++;
        }

        result.append(" VND");
        result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
        return result.toString();
    }

    @Override
    public String generateAndUploadInvoicePdf(PurchaseOrder purchaseOrder) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Image logo = new Image(ImageDataFactory.create("images/logo_stationery.svg"));
            logo.scaleToFit(100, 100);
            logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.LEFT);
            document.add(logo);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            logger.info("Using Helvetica font for iText PDF generation");
            document.add(new Paragraph("Invoice")
                    .setFont(font)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            document.add(new Paragraph("Order ID: " + purchaseOrder.getPurchaseOrderId())
                    .setFont(font));
            document.add(new Paragraph("Date: " + LocalDate.now())
                    .setFont(font));
            document.add(new Paragraph("Customer: " + purchaseOrder.getUser().getFirstName() + " " + purchaseOrder.getUser().getLastName())
                    .setFont(font));
            document.add(new Paragraph("Address: " + purchaseOrder.getAddress().getAddressName())
                    .setFont(font));
            document.add(new Paragraph("\n"));

            float[] columnWidths = {200, 100, 100, 100};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
            table.setPadding(5);

            table.addHeaderCell(new Cell().add(new Paragraph("Product").setFont(font).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.BLUE));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setFont(font).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.BLUE));
            table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setFont(font).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.BLUE));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(font).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.BLUE));

            List<PurchaseOrderDetail> details = purchaseOrder.getPurchaseOrderDetails();
            if (details.isEmpty()) {
                throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
            }

            for (PurchaseOrderDetail detail : details) {
                table.addCell(new Cell().add(new Paragraph(detail.getProductDetail().getName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getDiscountPrice())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getDiscountPrice() * detail.getQuantity())).setFont(font)));
            }

            table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph("Total Amount:").setFont(font).setBold()));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(purchaseOrder.getAmount())).setFont(font).setBold()));

            document.add(table);

            document.add(new Paragraph("Thank you for your business!")
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));

            document.close();

            String localPath = "monthly_invoice_" + purchaseOrder.getPurchaseOrderId() + ".pdf";
            Files.write(Paths.get(localPath), baos.toByteArray());

            Map uploadResult = cloudinary.uploader().upload(baos.toByteArray(),
                    Map.of("resource_type", "raw", "public_id", localPath, "overwrite", true));
            String url = (String) uploadResult.get("secure_url");

            return url;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate or upload invoice PDF", e);
            throw new RuntimeException("Failed to generate or upload invoice PDF: " + e.getMessage());
        }
    }

    @Override
    public String generateAndUploadCurrentInvoicePdf(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
            if (!user.getRole().getRoleId().equals("113")) {
                throw new AppException(NotExistedErrorCode.USER_EXISTED);
            }

            List<PurchaseOrder> orders = purchaseOrderRepository.findByUser_UserIdAndCreatedAtBetween(
                    userId, startDate, endDate
            );

            if (orders.isEmpty()) {
                throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
            }

            // Đọc template HTML
            String template = new String(new ClassPathResource("templates/monthly_invoice_template.html")
                    .getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            long totalAmount = 0;
            int totalItems = 0;
            StringBuilder orderRows = new StringBuilder();
            DecimalFormat formatter = new DecimalFormat("#,###");

            boolean hasDetails = false;
            for (PurchaseOrder order : orders) {
                if (order.getStatus() != PurchaseOrder.Status.PROCESSING) {
                    continue;
                }

                List<PurchaseOrderDetail> details = order.getPurchaseOrderDetails();
                if (details.isEmpty()) {
                    continue;
                }

                totalAmount += order.getAmount();
                for (PurchaseOrderDetail detail : details) {
                    totalItems += detail.getQuantity();

                    orderRows.append(String.format(
                            "<tr>" +
                                    "<td class=\"order-id\">%s</td>" +
                                    "<td class=\"product-name\">%s</td>" +
                                    "<td class=\"text-center\">%d</td>" +
                                    "<td class=\"text-right\">%s VND</td>" +
                                    "</tr>",
                            order.getPurchaseOrderId(),
                            detail.getProductDetail().getName(),
                            detail.getQuantity(),
                            formatter.format(detail.getDiscountPrice())
                    ));
                    hasDetails = true;
                }
            }

            if (!hasDetails) {
                throw new AppException(NotExistedErrorCode.ORDER_NOT_FOUND);
            }

            // Chuẩn bị dữ liệu cho template
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String period = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                    endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Thay thế các placeholder trong template
            String htmlContent = template
                    .replace("${invoiceTitle}", "TAX INVOICE")
                    .replace("${invoiceNumber}", "INV-" + System.currentTimeMillis())
                    .replace("${userName}", user.getFirstName() + " " + user.getLastName())
                    .replace("${department}", user.getLastName() != null ? user.getLastName() : "N/A")
                    .replace("${currentDate}", currentDate)
                    .replace("${month}", period)
                    .replace("${orderCount}", String.valueOf(orders.size()))
                    .replace("${totalItems}", String.valueOf(totalItems))
                    .replace("${subtotal}", formatter.format(totalAmount))
                    .replace("${totalAmount}", formatter.format(totalAmount))
                    .replace("${totalAmountInWords}", numberToEnglishWords(totalAmount))
                    .replace("${orderRows}", orderRows.toString())
                    .replace("${taxInfo}", "Tax ID: 123456789 | VAT: 0%");

            // Tạo PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();

            // Cấu hình font
            String fontPath = "fonts/OpenSans-Regular.ttf";
            ClassPathResource fontResource = new ClassPathResource(fontPath);
            if (fontResource.exists()) {
                try {
                    renderer.getFontResolver().addFont(fontResource.getURL().toString(), true);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to register custom font, using default", e);
                }
            }

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);

            String localPath = "current_invoice_" + userId + "_" + startDate.toLocalDate() + "_to_" + endDate.toLocalDate() + ".pdf";
            Files.write(Paths.get(localPath), baos.toByteArray());

            // Upload lên Cloudinary
            String publicId = "invoices/" + userId + "_" + startDate.toLocalDate() + "_to_" + endDate.toLocalDate() + ".pdf";
            Map uploadResult = cloudinary.uploader().upload(baos.toByteArray(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "public_id", publicId,
                            "overwrite", true
                    ));

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read HTML template", e);
            throw new RuntimeException("Failed to read HTML template: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate or upload invoice PDF", e);
            throw new RuntimeException("Failed to generate or upload invoice PDF: " + e.getMessage());
        }
    }
}