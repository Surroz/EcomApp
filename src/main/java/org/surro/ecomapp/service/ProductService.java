package org.surro.ecomapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.surro.ecomapp.model.Product;
import org.surro.ecomapp.repo.ProductRepo;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepo repo;

    public List<Product> getProducts() {
        return repo.findAll();
    }

    public Product getProduct(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public boolean addProduct(Product product, MultipartFile image) {
        try {
            product.setImageData(image.getBytes());
            product.setImageType(image.getContentType());
            product.setImageName(image.getOriginalFilename());
        } catch (IOException e) {
            return false;
        }
        repo.save(product);
        return true;
    }

    public boolean updateProduct(Product product, MultipartFile image) {
        try {
            product.setImageData(image.getBytes());
            product.setImageType(image.getContentType());
            product.setImageName(image.getOriginalFilename());
        } catch (IOException e) {
            return false;
        }
        repo.save(product);
        return true;
    }

    public void deleteProduct(Integer id) {
        repo.deleteById(id);
    }

    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {
        if(image != null && !image.isEmpty()) {
            product.setImageName(image.getOriginalFilename());
            product.setImageType(image.getContentType());
            product.setImageData(image.getBytes());
        }

        return repo.save(product);
    }

    public List<Product> getProducts(String keyword) {
        return repo.searchProducts(keyword);
    }
}
