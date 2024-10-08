package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.repositories.ContemplationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContemplationService {

    private final ContemplationRepository contemplationRepository;

    @Autowired
    public ContemplationService(ContemplationRepository contemplationRepository) {
        this.contemplationRepository = contemplationRepository;
    }
}
