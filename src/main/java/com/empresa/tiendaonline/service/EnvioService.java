package com.empresa.tiendaonline.service;

import com.empresa.tiendaonline.entity.Envio;
import com.empresa.tiendaonline.repository.EnvioRepository;
import org.springframework.stereotype.Service;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(
            EnvioRepository envioRepository) {

        this.envioRepository = envioRepository;
    }

    public Envio guardar(Envio envio) {

        return envioRepository.save(envio);
    }
}