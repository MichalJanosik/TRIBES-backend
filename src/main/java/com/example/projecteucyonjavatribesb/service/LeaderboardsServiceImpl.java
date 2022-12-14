package com.example.projecteucyonjavatribesb.service;

import com.example.projecteucyonjavatribesb.model.Buildings;
import com.example.projecteucyonjavatribesb.model.DTO.LeaderboardsDTO;
import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.example.projecteucyonjavatribesb.model.Troops.Troops;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardsServiceImpl implements LeaderboardsService {

    private final KingdomService kingdomService;

    public List<LeaderboardsDTO> getLeaderboard(LeaderboardType type) {
        List<LeaderboardsDTO> leaderboard = new ArrayList<>();
        switch (type) {
            case BUILDINGS -> leaderboard = getLeaderBoardByBuildings();
            case TROOPS -> leaderboard = getLeaderboardByTroops();
            case KINGDOMS -> leaderboard = getLeaderboardByKingdoms();
        }

        return leaderboard;
    }

    private List<LeaderboardsDTO> getLeaderboardByKingdoms() {
        List<Kingdom> kingdoms = kingdomService.findAllKingdoms();

        return kingdoms.stream()
                .map(x -> LeaderboardsDTO.builder()
                        .ruler(x.getRuler())
                        .kingdom(x.getPlayer().getKingdomName())
                        .buildings(x.getBuildingList().size())
                        .troops(x.getTroopsList().size())
                        .points(getBuildingsPoints(x) + getTroopsPoints(x))
                        .build())
                .sorted(sortByPoints())
                .collect(Collectors.toList());
    }

    private List<LeaderboardsDTO> getLeaderboardByTroops() {
        List<Kingdom> kingdoms = kingdomService.findAllKingdoms();

        return kingdoms.stream()
                .map(x -> LeaderboardsDTO.builder()
                        .ruler(x.getRuler())
                        .kingdom(x.getPlayer().getKingdomName())
                        .troops(x.getTroopsList().size())
                        .points(getTroopsPoints(x))
                        .build())
                .sorted(sortByPoints())
                .collect(Collectors.toList());
    }

    private Integer getTroopsPoints(Kingdom x) {
        return x.getTroopsList().stream()
                .map(Troops::getLevel)
                .reduce(Integer::sum)
                .get();
    }

    private List<LeaderboardsDTO> getLeaderBoardByBuildings() {
        List<Kingdom> kingdoms = kingdomService.findAllKingdoms();

        return kingdoms.stream()
                .map(x -> LeaderboardsDTO.builder()
                        .ruler(x.getRuler())
                        .kingdom(x.getPlayer().getKingdomName())
                        .buildings(x.getBuildingList().size())
                        .points(getBuildingsPoints(x))
                        .build())
                .sorted(sortByPoints())
                .collect(Collectors.toList());
    }

    public Integer getBuildingsPoints(Kingdom x) {
        return x.getBuildingList().stream()
                .map(Buildings::getLevel)
                .reduce(Integer::sum)
                .get();
    }

    private Comparator<LeaderboardsDTO> sortByPoints() {
        return Comparator.comparing(LeaderboardsDTO::getPoints).reversed();
    }
}
