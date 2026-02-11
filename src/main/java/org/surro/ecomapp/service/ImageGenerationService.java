package org.surro.ecomapp.service;

import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerationService {

    private final ImageModel imageModel;

    public ImageGenerationService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public String generateImage(String prompt) {
        ImageGeneration imageGeneration = imageModel.call(new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .quality("hd")
                                .height(1024)
                                .width(1024)
                                .build()
                )
        ).getResult();
        return imageGeneration.getOutput().getUrl();
    }
}
