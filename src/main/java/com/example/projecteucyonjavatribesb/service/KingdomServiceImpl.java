package com.example.projecteucyonjavatribesb.service;

import com.example.projecteucyonjavatribesb.model.DTO.*;
import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.example.projecteucyonjavatribesb.repository.BuildingsRepository;
import com.example.projecteucyonjavatribesb.repository.KingdomRepository;
import com.example.projecteucyonjavatribesb.repository.ResourcesRepository;
import com.example.projecteucyonjavatribesb.repository.TroopsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class KingdomServiceImpl implements KingdomService {

    private final KingdomRepository kingdomRepository;
    private final BuildingsRepository buildingsRepository;
    private final ResourcesRepository resourcesRepository;
    private final TroopsRepository troopsRepository;

    @Override
    public KingdomDetailsDTO getKingdomDetailsDTOById(Long id) {
        Kingdom kingdom = kingdomRepository.getKingdomById(id);
        return extractKingdomDetailsFromKingdom(kingdom);
    }

    @Override
    public Optional<Kingdom> findById(Long id) {
        return kingdomRepository.findById(id);
    }

    @Override
    public ListOfKingdomsDTO makeListOfKingdomsDTO() {
        List<KingdomDTO> listOfKingdomsDTO = new ArrayList<>();
        List<Kingdom> listOfKingdoms = kingdomRepository.findAll();
        for (Kingdom listOfKingdom : listOfKingdoms) {
            // method for sum levels of all buildings in kingdom, it should be size of kingdom so ... its population?
            int population = countPopulationInKingdom(listOfKingdom);

            listOfKingdomsDTO.add(new KingdomDTO(
                    listOfKingdom.getId(),
                    listOfKingdom.getPlayer().getKingdomName(),
                    listOfKingdom.getRuler(),
                    population,
                    new LocationDTO(
                            listOfKingdom.getLocation().getCoordinateX(),
                            listOfKingdom.getLocation().getCoordinateY()
                    )));
        }
        return new ListOfKingdomsDTO(listOfKingdomsDTO);

    }

    @Override
    public int countPopulationInKingdom(Kingdom kingdom) {
        Kingdom checkedKingdom = kingdomRepository.findKingdomById(kingdom.getId());
        int population = 0;
        for (int i = 0; i < checkedKingdom.getBuildingList().size(); i++) {
            population += checkedKingdom.getBuildingList().get(i).getLevel();
        }
        return population;
    }

    public Kingdom findKingdomById(Long id) {
        return kingdomRepository.getKingdomById(id);
    }

    private KingdomDetailsDTO extractKingdomDetailsFromKingdom(Kingdom kingdom) {
        return KingdomDetailsDTO
                .builder()
                .kingdom(new KingdomDTO(kingdom))
                .resources(resourcesRepository.findAllByKingdom_Id(kingdom.getId())
                        .stream()
                        .map(ResourcesDTO::new)
                        .collect(Collectors.toList()))
                .buildings(buildingsRepository.findAllByKingdom_Id(kingdom.getId())
                        .stream().map(BuildingDTO::new)
                        .collect(Collectors.toList()))
                .troops(troopsRepository.findAllByKingdom_Id(kingdom.getId())
                        .stream().map(TroopDTO::new)
                        .collect(Collectors.toList()))
                .build();
    }

    public KingdomDTO getKingdomDTO(Long id) {
        return convertToKingdomDTO(kingdomRepository.findById(id).get());
    }

    @Override
    public void renameKingdom(Long kingdomId, KingdomNameDTO kingdomNameDTO) {
        Kingdom kingdom = kingdomRepository.findById(kingdomId).get();
        kingdom.getPlayer().setKingdomName(kingdomNameDTO.getKingdomName());
        kingdomRepository.save(kingdom);
    }
    @Override
    public KingdomDTO getRenamedKingdomDTO(Long kingdomId) {
        Kingdom kingdom = kingdomRepository.findById(kingdomId).get();
        return KingdomDTO.builder()
                .kingdomId(kingdom.getId())
                .kingdomName(kingdom.getPlayer().getKingdomName())
                .build();
    }

    private KingdomDTO convertToKingdomDTO(Kingdom kingdom) {
        return new KingdomDTO(
                kingdom.getId(),
                kingdom.getPlayer().getKingdomName(),
                kingdom.getRuler(),
                kingdom.getPopulation(),
                new LocationDTO(kingdom)
        );
    }

    public List<Kingdom> findAllKingdoms() {
        return kingdomRepository.findAll();
    }
}
