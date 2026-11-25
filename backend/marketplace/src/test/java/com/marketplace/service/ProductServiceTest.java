package com.marketplace.service;

import com.marketplace.dto.ProductRequest;
import com.marketplace.dto.ProductResponse;
import com.marketplace.entity.Product;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnAllProducts(){
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("999.99"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Keyboard");
        product2.setPrice(new BigDecimal("49.99"));

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Laptop", result.get(0).getName());
        Assertions.assertEquals("Keyboard", result.get(1).getName());
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists(){
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Gaming Laptop");
        product.setPrice(new BigDecimal("1499.99"));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse result = productService.getProductById(1L);

        // Assert
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Gaming Laptop", result.getName());
        Assertions.assertEquals(new BigDecimal("1499.99"), result.getPrice());
    }

    @Test
    void getProductById_shouldThrowException_whenProductDoesNotExist() {

        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99L)
        );
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Mechanical Keyboard");
        request.setDescription("RGB gaming keyboard");
        request.setPrice(new BigDecimal("79.99"));
        request.setQuantity(10);
        request.setCategory("Electronics");

        Product savedProduct = new Product();
        savedProduct.setId(3L);
        savedProduct.setName("Mechanical Keyboard");
        savedProduct.setDescription("RGB gaming keyboard");
        savedProduct.setPrice(new BigDecimal("79.99"));
        savedProduct.setQuantity(10);
        savedProduct.setCategory("Electronics");

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponse result = productService.createProduct(request);

        // Assert
        Assertions.assertEquals(3L, result.getId());
        Assertions.assertEquals("Mechanical Keyboard", result.getName());
        Assertions.assertEquals(new BigDecimal("79.99"), result.getPrice());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldUpdateAndReturnProduct() {

        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Laptop");
        existingProduct.setPrice(new BigDecimal("899.99"));

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated description");
        request.setPrice(new BigDecimal("1199.99"));
        request.setQuantity(5);
        request.setCategory("Electronics");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductResponse result = productService.updateProduct(1L, request);

        // Assert
        Assertions.assertEquals("Updated Laptop", result.getName());
        Assertions.assertEquals(
                new BigDecimal("1199.99"),
                result.getPrice()
        );
        Assertions.assertEquals(5, result.getQuantity());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_shouldThrowException_whenProductDoesNotExist() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setPrice(new BigDecimal("1199.99"));

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(99L, request)
        );

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenProductExists() {

        // Arrange
        when(productRepository.existsById(1L))
                .thenReturn(true);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductDoesNotExist() {

        // Arrange
        when(productRepository.existsById(99L))
                .thenReturn(false);

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(99L)
        );

        // Verify
        verify(productRepository, never()).deleteById(99L);
    }
}

