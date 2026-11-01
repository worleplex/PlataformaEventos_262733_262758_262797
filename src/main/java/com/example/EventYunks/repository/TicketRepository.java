package com.example.EventYunks.repository;

import com.example.EventYunks.model.Ticket;
import com.example.EventYunks.model.enums.EstadoPago;
import com.example.EventYunks.model.enums.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.boleto.evento.organizador.id = :organizadorId AND t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago")
    long contarVendidosPorOrganizador(@Param("organizadorId") Long organizadorId, @Param("estadoExcluido") EstadoTicket estadoExcluido,@Param("estadoPago") EstadoPago estadoPago);

    @Query("SELECT SUM(t.boleto.precio) FROM Ticket t WHERE t.boleto.evento.organizador.id = :organizadorId AND t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago")
    BigDecimal sumarIngresosPorOrganizador(@Param("organizadorId") Long organizadorId, @Param("estadoExcluido") EstadoTicket estadoExcluido, @Param("estadoPago") EstadoPago estadoPago);
}
