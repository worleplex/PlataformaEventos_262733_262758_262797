package com.example.EventYunks.service;

import com.example.EventYunks.model.enums.EstadoPago;
import com.example.EventYunks.model.enums.EstadoTicket;
import com.example.EventYunks.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public long boletosVendidos(Long organizadorId) {
        return ticketRepository.contarVendidosPorOrganizador(organizadorId, EstadoTicket.CANCELADO, EstadoPago.APROBADO);
    }

    public BigDecimal ingresosTotales(Long organizadorId) {
        BigDecimal total = ticketRepository.sumarIngresosPorOrganizador(organizadorId, EstadoTicket.CANCELADO, EstadoPago.APROBADO);
        //si no se ha vendido nada, pues
        if (total == null) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    public long boletosVendidosGlobales() {
        return ticketRepository.contarBoletosVendidos(EstadoTicket.CANCELADO, EstadoPago.APROBADO);
    }

    public BigDecimal ingresosUltimos30Dias() {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusDays(30);
        BigDecimal total = ticketRepository.sumarIngresosPorPeriodo(inicio, fin, EstadoTicket.CANCELADO, EstadoPago.APROBADO);
        if (total == null) {
            return BigDecimal.ZERO;
        }
        return total;
    }



}
