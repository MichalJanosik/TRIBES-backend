package com.example.projecteucyonjavatribesb.service;

//this service is going to be used in general for reading/updating any resources values

import com.example.projecteucyonjavatribesb.model.Buildings;
import com.example.projecteucyonjavatribesb.model.DTO.KingdomDTO;
import com.example.projecteucyonjavatribesb.model.DTO.KingdomDetailsDTO;
import com.example.projecteucyonjavatribesb.model.DTO.ResourcesDTO;
import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.example.projecteucyonjavatribesb.model.Resources;
import com.example.projecteucyonjavatribesb.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;

    //here the waiting time after generating resources is set in milliseconds:
    protected final Long timeToWaitForResourcesInMillis = 30 * 60 * 1000L;

    @Override
    public KingdomDetailsDTO getKingdomResources(Long kingdomId) {
        Kingdom kingdom = resourcesRepository
                .findAllByKingdom_Id(kingdomId)
                .listIterator()
                .next()
                .getKingdom();

        return KingdomDetailsDTO.builder()
                .kingdom(KingdomDTO.builder()
                        .kingdomId(kingdom.getId())
                        .kingdomName(kingdom.getPlayer().getKingdomName())
                        .ruler(kingdom.getRuler())
                        .population(kingdom.getPopulation())
                        //TODO: refactor with locationDTO:
                        .location(kingdom.getLocation())
                        .build())
                .resources(kingdom.getResourcesList().stream()
                        .map(ResourcesServiceImpl::convertToResourcesDTO)
                        .toList())
                .build();

//        SortedMap<> solution to the same problem:
//        --------------------------------------------------------------
//        KingdomDTO kingdomDTO = kingdomService.getKingdomDTO(id);
//        List<ResourcesDTO> resourcesDTOList =
//                kingdomService.findKingdomById(id).getResourcesList().stream()
//                        .map(ResourcesServiceImpl::convertToResourcesDTO)
//                        .toList();
//
//        SortedMap<String, Object> kingdomResources = new TreeMap<>();
//
//        kingdomResources.put("kingdom", kingdomDTO);
//        kingdomResources.put("resources", resourcesDTOList);
//
//        return kingdomResources;
    }

    //initial amount of 100 gold is given to the ruler upon kingdom creation
    public List<Resources> getInitialResources() {
        return new ArrayList<>(List.of(
                new Resources("gold", 100, 1)
        ));
    }


    public void generateResources(Long kingdomId) {
        //TODO: these two values have to be extracted from the kingdom
        // (they should depend on TownHall level):
        Integer granaryCapacity = 1000;
        Integer vaultCapacity = 1000;

        List<Resources> kingdomResources = getResourcesByKingdomId(kingdomId);
        for (Resources resource : kingdomResources) {
            Integer resourceGenerationPerMinute = getResourceGenerationPerMinute(resource);
            if (canGenerateResource(resource)) {
                Integer timePassedInMinutes = Math.toIntExact(
                        (System.currentTimeMillis() - resource.getUpdatedAt()) / 1000 / 60
                );
                Integer amountToBeAdded = resourceGenerationPerMinute * timePassedInMinutes;

                if (resource.getType().equals("gold")) {
                    resource.setAmount(
                            (resource.getAmount() + amountToBeAdded) <= vaultCapacity ?
                                    resource.getAmount() + amountToBeAdded : vaultCapacity
                    );
                } else if (resource.getType().equals("food")) {
                    resource.setAmount(
                            (resource.getAmount() + amountToBeAdded) <= granaryCapacity ?
                                    resource.getAmount() + amountToBeAdded : granaryCapacity
                    );
                }

            }
        }
        resourcesRepository.saveAll(kingdomResources);
    }

    //TODO: this in final will be implemented somewhere else to update the resources generation
    // upon buildings creation, level-up or destroy
    // (avoid calculating this everytime resource is updated)
    // (could be also private)
    //this function will return the actual resource generation per minute depending on
    //mines/farms count and their levels
    public Integer getResourceGenerationPerMinute(Resources resource) {
        Integer resourceGeneration = resource.getGeneration();
        List<Buildings> kingdomBuildings = resource.getKingdom().getBuildingList();
        int mineCount = 0;
        int farmCount = 0;

        for (Buildings building : kingdomBuildings) {
            if (building.getType().equals("mine") && resource.getType().equals("gold")) {
                mineCount++;
                resourceGeneration = resourceGeneration * building.getLevel();
            }
            if (building.getType().equals("farm") && resource.getType().equals("food")) {
                farmCount++;
                resourceGeneration = resourceGeneration * building.getLevel();
            }
        }

        if (resource.getType().equals("gold")) {
            resourceGeneration = resourceGeneration * mineCount;
        }
        if (resource.getType().equals("food")) {
            resourceGeneration = resourceGeneration * farmCount;
        }

        return resourceGeneration;
    }

    private boolean canGenerateResource(Resources resource) {
        //TODO:
        // Condition here needed to be added to evaluate if vault or granary is full depending on
        // the resource passed to the method and thus it can be generated or not.
        // We should figure out where to implement vault and granary
        return (System.currentTimeMillis() > (resource.getUpdatedAt() + timeToWaitForResourcesInMillis));
    }

    //method to be used for all cases decreasing amount of resource
    public void useResource(Resources resource, Integer amount) {
        if (canBeResourceUsed(resource, amount)) {
            resource.setAmount(resource.getAmount() - amount);
            resourcesRepository.save(resource);
        }
    }

    public boolean canBeResourceUsed(Resources resource, Integer amount) {
        return resource.getAmount() >= amount;
    }

    public List<Resources> getResourcesByKingdomId(Long kingdomId) {
        return resourcesRepository.findAllByKingdom_Id(kingdomId);
    }

    private static ResourcesDTO convertToResourcesDTO(Resources resources) {
        return new ResourcesDTO(
                resources.getType(),
                resources.getAmount(),
                resources.getGeneration(),
                resources.getUpdatedAt()
        );
    }
}
