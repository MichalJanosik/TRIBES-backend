package com.example.projecteucyonjavatribesb.service;

import com.example.projecteucyonjavatribesb.model.DTO.KingdomDetailsDTO;
import com.example.projecteucyonjavatribesb.model.Resources;

import java.util.List;

public interface ResourcesService {
    KingdomDetailsDTO getKingdomResources(Long kingdomId);
    List<Resources> getInitialResources();
    List<Resources> getResourcesByKingdomId(Long kingdom_id);
    void generateResources(Long kingdomId);
    Integer getResourceGenerationPerMinute(Resources resource);

    boolean useResource(Resources resource, Integer amount);
    boolean canBeResourceUsed(Resources resource, Integer amount);
}
