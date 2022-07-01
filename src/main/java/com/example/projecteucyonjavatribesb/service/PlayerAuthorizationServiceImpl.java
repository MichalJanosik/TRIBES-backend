package com.example.projecteucyonjavatribesb.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.projecteucyonjavatribesb.filter.JwtRequestFilter;
import com.example.projecteucyonjavatribesb.model.Kingdom;
import com.example.projecteucyonjavatribesb.repository.KingdomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.projecteucyonjavatribesb.repository.KingdomRepository;

@Service
@AllArgsConstructor
public class PlayerAuthorizationServiceImpl implements PlayerAuthorizationService {

    private final KingdomRepository kingdomRepository;

    @Override
    public Boolean playerOwnsKingdom(String playerUsername, Long kingdomId) {
        return kingdomRepository.findKingdomByRulerAndId(playerUsername, kingdomId).isPresent();
    }

    @Override
    public Kingdom getKingdomPreviewFromUsername(String username) {
        return kingdomRepository.findKingdomByRuler(username);
    }


}
