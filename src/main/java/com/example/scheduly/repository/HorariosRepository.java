package com.example.scheduly.repository;

import com.example.scheduly.model.HorarioDisponible;
import com.example.scheduly.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HorariosRepository extends JpaRepository<HorarioDisponible, Long> {

    List<HorarioDisponible> findByEmpresa(Usuario empresa);

    List<HorarioDisponible> findByClienteReservadoIsNull();

    List<HorarioDisponible> findByClienteReservadoIsNotNull();

    List<HorarioDisponible> findByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT h FROM HorarioDisponible h WHERE h.empresa = :empresa ORDER BY h.fecha, h.horaInicio")
    List<HorarioDisponible> findByEmpresaOrdered(@Param("empresa") Usuario empresa);

    @Query("SELECT COUNT(h) FROM HorarioDisponible h WHERE h.empresa = :empresa AND h.clienteReservado IS NOT NULL")
    long countReservadasByEmpresa(@Param("empresa") Usuario empresa);

    @Query("SELECT COUNT(h) FROM HorarioDisponible h WHERE h.empresa = :empresa")
    long countByEmpresa(@Param("empresa") Usuario empresa);

    List<HorarioDisponible> findAllByOrderByFechaAscHoraInicioAsc();
}
