//package com.sportshop.sportshop.utils.upload;
//
//import com.sportshop.sportshop.entity.BrandEntity;
//import com.sportshop.sportshop.entity.CategoryEntity;
//import com.sportshop.sportshop.entity.ProductEntity;
//import com.sportshop.sportshop.enums.StatusEnum;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//public class ExcelProductHelper {
//
//    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//
//    public static boolean hasExcelFormat(MultipartFile file) {
//        return TYPE.equals(file.getContentType());
//    }
//
//
//
//    public static List<ProductEntity> excelToProducts(InputStream is) {
//        try (Workbook workbook = new XSSFWorkbook(is)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            DataFormatter formatter = new DataFormatter(); // <-- thêm formatter
//
//            List<ProductEntity> products = new ArrayList<>();
//            int rowNumber = 0;
//
//            while (rows.hasNext()) {
//                Row row = rows.next();
//                if (rowNumber == 0) { // bỏ qua header
//                    rowNumber++;
//                    continue;
//                }
//
//                ProductEntity product = new ProductEntity();
//
//                product.setName(formatter.formatCellValue(row.getCell(0)));
//                product.setPrice(Long.parseLong(formatter.formatCellValue(row.getCell(1))));
//                product.setDiscount(Long.parseLong(formatter.formatCellValue(row.getCell(2))));
//                product.setDescription(formatter.formatCellValue(row.getCell(3)));
//                product.setColor(formatter.formatCellValue(row.getCell(4)));
//                product.setQuantity(Long.parseLong(formatter.formatCellValue(row.getCell(5))));
//                product.setStatus(StatusEnum.valueOf(formatter.formatCellValue(row.getCell(6))));
//
//                String categoryName = formatter.formatCellValue(row.getCell(7));
//                CategoryEntity category = new CategoryEntity();
//                category.setName(categoryName);
//                product.setCategory(category);
//
//                String brandName = formatter.formatCellValue(row.getCell(8));
//                BrandEntity brand = new BrandEntity();
//                brand.setName(brandName);
//                product.setBrand(brand);
//
//                products.add(product);
//            }
//            return products;
//        } catch (IOException e) {
//            throw new RuntimeException("Không thể đọc file Excel: " + e.getMessage());
//        }
//    }
//}