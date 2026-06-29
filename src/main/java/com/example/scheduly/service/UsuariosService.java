package com.example.scheduly.service;

import com.example.scheduly.model.HorarioDisponible;
import com.example.scheduly.model.Rol;
import com.example.scheduly.model.Usuario;
import com.example.scheduly.repository.HorariosRepository;
import com.example.scheduly.repository.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {

    private final UsuariosRepository usuarioRepository;
    private final HorariosRepository horarioRepository;

    public UsuariosService(UsuariosRepository usuarioRepository, HorariosRepository horarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
    }

    // Crear usuario (cliente o empresa)
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ✅ NUEVO: Obtener usuario por email
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Actualizar usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setRol(usuarioActualizado.getRol());
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // **Solo clientes pueden reservar horarios**
    public HorarioDisponible reservarHorario(Long idCliente, Long idHorario) {
        Usuario cliente = obtenerUsuarioPorId(idCliente);

        if (!cliente.getRol().equals(Rol.CLIENTE)) {
            throw new RuntimeException("Solo los clientes pueden reservar horarios");
        }

        HorarioDisponible horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (horario.getClienteReservado() != null) {
            throw new RuntimeException("Horario ya reservado");
        }

        horario.setClienteReservado(cliente);
        return horarioRepository.save(horario);
    }

    // **Solo empresas pueden publicar horarios**
    public HorarioDisponible publicarHorario(Long idEmpresa, HorarioDisponible horario) {
        Usuario empresa = obtenerUsuarioPorId(idEmpresa);

        if (!empresa.getRol().equals(Rol.EMPRESA)) {
            throw new RuntimeException("Solo las empresas pueden publicar horarios");
        }

        horario.setEmpresa(empresa);
        return horarioRepository.save(horario);
    }
}
