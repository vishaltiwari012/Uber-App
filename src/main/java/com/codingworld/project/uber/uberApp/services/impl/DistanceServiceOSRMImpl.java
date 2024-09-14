package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";
    @Override
    public double calculateDistance(Point src, Point dest) {
//        call the third party api called osrm to fetch the distance
        try {
            String uri = src.getX()+","+src.getY()+";"+dest.getX()+","+dest.getY();
            OSRMResponseDTO responseDTO = RestClient.builder().baseUrl(OSRM_API_BASE_URL)
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(OSRMResponseDTO.class);
            return responseDTO.getRoutes().get(0).getDistance() / 1000.0;
        } catch (Exception e) {
            throw new RuntimeException("Error getting data from OSRM " + e.getMessage());
        }

    }
}


@Data
class OSRMResponseDTO {
    private List<OSRMRoute> routes;
}

@Data
class OSRMRoute {
    private Double distance;
}
