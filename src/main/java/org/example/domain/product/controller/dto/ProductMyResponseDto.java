package org.example.domain.product.controller.dto;

import lombok.Getter;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductStatus;
import org.example.domain.product.domain.model.StockStatus;

@Getter
public class ProductMyResponseDto {

    private Long id;

    private String title;

    private int stock;

    private int price;

    private ProductStatus productStatus;

    private StockStatus stockStatus;

    public ProductMyResponseDto(Long id, String title, int stock, int price, ProductStatus productStatus, StockStatus stockStatus) {
        this.id = id;
        this.title = title;
        this.stock = stock;
        this.price = price;
        this.productStatus = productStatus;
        this.stockStatus = stockStatus;
    }

    public static ProductMyResponseDto from(Product product) {
        return new ProductMyResponseDto(
                product.getId(),
                product.getTitle(),
                product.getStock(),
                product.getPrice(),
                product.getProductStatus(),
                product.getStockStatus()
        );
    }
}
