package com.example.projecteucyonjavatribesb.service;

import com.example.projecteucyonjavatribesb.model.DTO.BuildingDTO;
import com.example.projecteucyonjavatribesb.model.DTO.KingdomBuildingsDTO;
import com.example.projecteucyonjavatribesb.model.DTO.KingdomDTO;
import com.example.projecteucyonjavatribesb.model.DTO.LocationDTO;
import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.example.projecteucyonjavatribesb.repository.BuildingsRepository;
import com.example.projecteucyonjavatribesb.repository.KingdomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingsServiceImpl implements BuildingsService{
        private final KingdomRepository kingdomRepository;

    @Override
    public KingdomBuildingsDTO makeKingdomBuildingsDTO(Long id) {
        Kingdom kingdom = kingdomRepository.getKingdomById(id);
        List<BuildingDTO> listOfBuildings = new ArrayList<>();
        KingdomDTO kingdomDTO = new KingdomDTO(id,
                kingdom.getPlayer().getKingdomName(),
                kingdom.getRuler(),
                kingdom.getPopulation(),
                new LocationDTO(kingdom.getLocation().getCoordinateX(), kingdom.getLocation().getCoordinateY()));

        for (int i = 0; i < kingdom.getBuildingList().size(); i++) {
            listOfBuildings.add(new BuildingDTO(kingdom.getBuildingList().get(i).getId(),
                    kingdom.getBuildingList().get(i).getType(),
                    kingdom.getBuildingList().get(i).getLevel(),
                    kingdom.getBuildingList().get(i).getStartedAt(),
                    kingdom.getBuildingList().get(i).getFinishedAt()
            ));
        }
        return new KingdomBuildingsDTO(kingdomDTO, listOfBuildings);
    }
}