package com.example.projecteucyonjavatribesb.model.DTO;

import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KingdomDTO {

    private Long kingdomId;
    private String kingdomName;
    private String ruler;
    private int population;
    private LocationDTO location;

    public KingdomDTO(Kingdom kingdom) {
        this.kingdomId = kingdom.getId();
        this.kingdomName = kingdom.getPlayer().getKingdomName();
        this.ruler = kingdom.getRuler();
        this.population = kingdom.getPopulation();
        this.location = new LocationDTO(kingdom);
    }
}
