package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.CityHall;
import br.com.tecsus.sccubs.repositories.CityHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityHallService {

    private final CityHallRepository cityHallRepository;

    @Autowired
    public CityHallService(CityHallRepository cityHallRepository) {
        this.cityHallRepository = cityHallRepository;
    }

    public CityHall findById(Long id) {
        return cityHallRepository.findById(id).orElse(null);
    }
}
