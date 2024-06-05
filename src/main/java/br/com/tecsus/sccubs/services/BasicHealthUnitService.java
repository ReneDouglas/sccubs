package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.UBSsystemUserDTO;
import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.repositories.BasicHealthUnitRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BasicHealthUnitService {

    private final BasicHealthUnitRepository basicHealthUnitRepository;
    private final CityHallService cityHallService;

    public BasicHealthUnitService(BasicHealthUnitRepository basicHealthUnitRepository, CityHallService cityHallService) {
        this.basicHealthUnitRepository = basicHealthUnitRepository;
        this.cityHallService = cityHallService;
    }

    public BasicHealthUnit findById(Long id) {
        return basicHealthUnitRepository.findById(id).orElse(null);
    }

    public List<BasicHealthUnit> findBasicHealthUnitsByCityHallOfLoggedSystemUser() {

        SystemUserDetails systemUserDetails = (SystemUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (systemUserDetails.getCityHallId() != null) {
            return basicHealthUnitRepository.findByCityHallId(systemUserDetails.getCityHallId());
        }
        return basicHealthUnitRepository.findByCityHallId(1L);
    }

    public void registerBasicHealthUnit(BasicHealthUnit basicHealthUnit, SystemUserDetails loggedUser) throws Exception{

        basicHealthUnit.setCityHall(cityHallService.findById(loggedUser.getCityHallId()));
        basicHealthUnit.setCreationDate(LocalDateTime.now());
        basicHealthUnit.setCreationUser(loggedUser.getUsername());
        basicHealthUnitRepository.save(basicHealthUnit);
    }

    @Transactional
    public List<UBSsystemUserDTO> findUBSsystemUsersByUBSid(Long id) {
        BasicHealthUnit basicHealthUnit = basicHealthUnitRepository.findById(id).orElse(null);

        if (basicHealthUnit == null) {
            return List.of();
        }
        if (basicHealthUnit.getSystemUsers() == null || basicHealthUnit.getSystemUsers().isEmpty()) {
            return List.of();
        }

        return basicHealthUnit
                .getSystemUsers()
                .stream()
                .map(user -> new UBSsystemUserDTO(
                        user.getId().toString(),
                        user.getName(),
                        user.getFirstRoleTitle(),
                        user.getActive() ? "Sim" : "Não"))
                .toList();
    }
}
