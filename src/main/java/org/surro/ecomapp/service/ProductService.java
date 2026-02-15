package org.surro.ecomapp.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.surro.ecomapp.model.Product;
import org.surro.ecomapp.repo.ProductRepo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepo repo;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ImageGenerationService imageGeneration;
    @Autowired
    PgVectorStore vectorStore;


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
        Product savedProduct = repo.save(product);
        saveProductToVectorDb(product);
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
        Product savedProduct = repo.save(product);
        saveProductToVectorDb(product);
        return savedProduct;
    }

    public void saveProductToVectorDb(Product product) {
        String content = String.format(
                "Product name: %s, description: %s, brand: %s, category: %s, price: %s, release date: %s, available: %s, stock: %s",
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory(),
                product.getPrice(),
                product.getReleaseDate(),
                product.getProductAvailable(),
                product.getStockQuantity()
        );

        Document document = new Document(
                UUID.randomUUID().toString(),
                content,
                Map.of("productId", String.valueOf(product.getId())));

        vectorStore.add(List.of(document));
    }

    public void deleteVectorDbProduct(Integer id) {
        vectorStore.delete(String.format("productId == %s", String.valueOf(id)));
    }

    public List<Product> getProducts(String keyword) {
        return repo.searchProducts(keyword);
    }

    public String generateDescription(String name, String category) {
        String prompt = String.format("""
                You are marketologist.
                Generate description for product based on its name %s and category %s for web site catalog.
                Max length 200 symbols.
                """, name, category);
        return chatService.callChatclient(prompt);
    }

    public byte[] generateImage(String name, String category, String description) throws RuntimeException {
        String prompt = String.format("""
                You are marketologist.
                Generate image for product based on its name %s, description %s and category %s for web site catalog.
                Image must look like professional photo of product.
                """, name, category, description);
        String image = imageGeneration.generateImage(prompt);
        byte[] result = null;
        try (InputStream is = new URI(image).toURL().openStream()) {
            result = is.readAllBytes();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
