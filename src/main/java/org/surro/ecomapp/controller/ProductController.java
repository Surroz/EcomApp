package org.surro.ecomapp.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.surro.ecomapp.model.Product;
import org.surro.ecomapp.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK) ;
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer id) {
        Product product = productService.getProduct(id);
        HttpStatus status = product != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(product, status);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart("imageFile") MultipartFile image) {
        boolean result = productService.addProduct(product, image);
        return result ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @GetMapping("product/{productId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer productId) {
        Product product = productService.getProduct(productId);
        return product == null || product.getImageData() == null ?
                ResponseEntity.notFound().build() : ResponseEntity.status(HttpStatus.OK).body(product.getImageData());
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id, @RequestPart Product product, @RequestPart @Nullable MultipartFile imageFile) {
        Product updatedProduct = null;
        try {
            updatedProduct = productService.addOrUpdateProduct(product, imageFile);
            return new ResponseEntity<>("Updated", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.getProducts(keyword);
        HttpStatus status = CollectionUtils.isEmpty(products) ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return ResponseEntity.status(status).body(products);
    }

}
