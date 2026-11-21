package com.example.EventYunks.repository;

import com.example.EventYunks.model.Ticket;
import com.example.EventYunks.model.enums.EstadoPago;
import com.example.EventYunks.model.enums.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.boleto.evento.organizador.id = :organizadorId AND t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago")
    long contarVendidosPorOrganizador(@Param("organizadorId") Long organizadorId, @Param("estadoExcluido") EstadoTicket estadoExcluido,@Param("estadoPago") EstadoPago estadoPago);

    @Query("SELECT SUM(t.boleto.precio) FROM Ticket t WHERE t.boleto.evento.organizador.id = :organizadorId AND t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago")
    BigDecimal sumarIngresosPorOrganizador(@Param("organizadorId") Long organizadorId, @Param("estadoExcluido") EstadoTicket estadoExcluido, @Param("estadoPago") EstadoPago estadoPago);

    @Query(" SELECT COUNT(t)FROM Ticket t WHERE t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago")
    long contarBoletosVendidos(@Param("estadoExcluido") EstadoTicket estadoExcluido, @Param("estadoPago") EstadoPago estadoPago);

    @Query(" SELECT SUM(t.boleto.precio) FROM Ticket t WHERE t.estado <> :estadoExcluido AND t.compra.pago.estado = :estadoPago AND t.compra.fechaCompra BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal sumarIngresosPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin, @Param("estadoExcluido") EstadoTicket estadoExcluido, @Param("estadoPago") EstadoPago estadoPago);
}
