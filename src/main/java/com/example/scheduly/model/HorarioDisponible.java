package com.example.scheduly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Entity
@Table(name = "horarios_disponibles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HorarioDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Usuario empresa;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario clienteReservado;

    public DayOfWeek getDiaSemana() {
        return fecha != null ? fecha.getDayOfWeek() : null;
    }
}
