package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.service;

import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.repository.DetalleInformeOaRepository;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository.InformePedagogicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleInformeOaService {

    private final DetalleInformeOaRepository detalleInformeOaRepository;
    private final InformePedagogicoRepository informePedagogicoRepository;

    public List<DetalleInformeOa> findByInformeId(Long informeId) {
        if (!informePedagogicoRepository.existsById(informeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Informe no encontrado");
        }
        return detalleInformeOaRepository.findByInformeIdOrderByIdAsc(informeId);
    }
}
