package com.example.projecteucyonjavatribesb.model.DTO;

import com.example.projecteucyonjavatribesb.model.Resources;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ResourcesDTO {
    private String type;
    private Integer amount;
    private Integer generation;
    private Long updatedAt;

    public ResourcesDTO(Resources resources) {
        this.type = resources.getType();
        this.amount = resources.getAmount();
        this.generation = resources.getGeneration();
        this.updatedAt = resources.getUpdatedAt();
    }
}
