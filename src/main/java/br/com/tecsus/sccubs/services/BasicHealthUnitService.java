package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.repositories.BasicHealthUnitRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasicHealthUnitService {

    private final BasicHealthUnitRepository basicHealthUnitRepository;

    public BasicHealthUnitService(BasicHealthUnitRepository basicHealthUnitRepository) {
        this.basicHealthUnitRepository = basicHealthUnitRepository;
    }

    public List<BasicHealthUnit> findBasicHealthUnitsByCityHallOfLoggedSystemUser() {

        SystemUserDetails systemUserDetails = (SystemUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (systemUserDetails.getCityHallId() != null) {
            return basicHealthUnitRepository.findByCityHallId(systemUserDetails.getCityHallId());
        }
        return basicHealthUnitRepository.findByCityHallId(1L);
    }
}
