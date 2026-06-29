package com.example.scheduly.service;

import com.example.scheduly.model.HorarioDisponible;
import com.example.scheduly.model.Usuario;
import com.example.scheduly.model.Rol;
import com.example.scheduly.repository.HorariosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HorariosDisponiblesService {

    private final HorariosRepository horarioRepository;

    public HorariosDisponiblesService(HorariosRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public HorarioDisponible crearHorario(HorarioDisponible horario, Usuario empresa) {
        if (!empresa.getRol().equals(Rol.EMPRESA)) {
            throw new RuntimeException("Solo las empresas pueden crear horarios");
        }
        horario.setEmpresa(empresa);
        return horarioRepository.save(horario);
    }

    public HorarioDisponible obtenerHorarioPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }

    public List<HorarioDisponible> obtenerTodosLosHorarios() {
        return horarioRepository.findAllByOrderByFechaAscHoraInicioAsc();
    }

    public List<HorarioDisponible> obtenerHorariosPorEmpresa(Usuario empresa) {
        return horarioRepository.findByEmpresaOrdered(empresa);
    }

    public HorarioDisponible actualizarHorario(Long id, HorarioDisponible datos, Usuario empresa) {
        HorarioDisponible horario = obtenerHorarioPorId(id);
        if (!horario.getEmpresa().getId().equals(empresa.getId())) {
            throw new RuntimeException("No puedes modificar horarios de otra empresa");
        }
        if (horario.getClienteReservado() != null) {
            throw new RuntimeException("No puedes modificar un horario ya reservado");
        }
        horario.setFecha(datos.getFecha());
        horario.setHoraInicio(datos.getHoraInicio());
        horario.setHoraFin(datos.getHoraFin());
        return horarioRepository.save(horario);
    }

    public void eliminarHorario(Long id, Usuario empresa) {
        HorarioDisponible horario = obtenerHorarioPorId(id);
        if (!horario.getEmpresa().getId().equals(empresa.getId())) {
            throw new RuntimeException("No puedes eliminar horarios de otra empresa");
        }
        horarioRepository.delete(horario);
    }

    public HorarioDisponible reservarHorario(Long horarioId, Usuario cliente) {
        if (!cliente.getRol().equals(Rol.CLIENTE)) {
            throw new RuntimeException("Solo los clientes pueden reservar horarios");
        }
        HorarioDisponible horario = obtenerHorarioPorId(horarioId);
        if (horario.getClienteReservado() != null) {
            throw new RuntimeException("Este horario ya está reservado");
        }
        horario.setClienteReservado(cliente);
        return horarioRepository.save(horario);
    }

    public HorarioDisponible cancelarReserva(Long horarioId, Usuario cliente) {
        HorarioDisponible horario = obtenerHorarioPorId(horarioId);
        if (horario.getClienteReservado() == null) {
            throw new RuntimeException("Este horario no tiene reserva activa");
        }
        if (!horario.getClienteReservado().getId().equals(cliente.getId())) {
            throw new RuntimeException("No puedes cancelar la reserva de otro cliente");
        }
        horario.setClienteReservado(null);
        return horarioRepository.save(horario);
    }

    public Map<String, Long> estadisticasEmpresa(Usuario empresa) {
        long total = horarioRepository.countByEmpresa(empresa);
        long reservadas = horarioRepository.countReservadasByEmpresa(empresa);
        return Map.of(
            "total", total,
            "reservadas", reservadas,
            "disponibles", total - reservadas
        );
    }
}
