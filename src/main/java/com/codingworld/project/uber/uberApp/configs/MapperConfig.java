package com.codingworld.project.uber.uberApp.configs;

import com.codingworld.project.uber.uberApp.dto.PointDTO;
import com.codingworld.project.uber.uberApp.utils.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(PointDTO.class, Point.class).setConverter(context -> {
            PointDTO pointDTO = context.getSource();
            return GeometryUtil.createPoint(pointDTO);
        });

        modelMapper.typeMap(Point.class, PointDTO.class).setConverter(context -> {
            Point point = context.getSource();
            double coordinates[] = {
                    point.getX(),
                    point.getY()
            };
            return new PointDTO(coordinates);
        });
        return modelMapper;
    }
}
