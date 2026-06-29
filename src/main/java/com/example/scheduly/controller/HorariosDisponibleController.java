package com.example.scheduly.controller;

import com.example.scheduly.model.HorarioDisponible;
import com.example.scheduly.model.Usuario;
import com.example.scheduly.service.HorariosDisponiblesService;
import com.example.scheduly.service.UsuariosService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/horarios")
public class HorariosDisponibleController {

    private final HorariosDisponiblesService horariosService;
    private final UsuariosService usuarioService;

    public HorariosDisponibleController(HorariosDisponiblesService horariosService,
                                        UsuariosService usuarioService) {
        this.horariosService = horariosService;
        this.usuarioService = usuarioService;
    }

    // ─── LISTAR ──────────────────────────────────────────────────────────────

    @GetMapping
    public String listarHorarios(@RequestParam(required = false) String estado,
                                 @RequestParam(required = false) String empresaId,
                                 Model model,
                                 Authentication authentication) {
        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
        List<HorarioDisponible> horarios = horariosService.obtenerTodosLosHorarios();

        // Filtros
        if ("disponible".equals(estado)) {
            horarios = horarios.stream().filter(h -> h.getClienteReservado() == null).toList();
        } else if ("reservado".equals(estado)) {
            horarios = horarios.stream().filter(h -> h.getClienteReservado() != null).toList();
        }

        if (empresaId != null && !empresaId.isBlank()) {
            try {
                Long eid = Long.parseLong(empresaId);
                horarios = horarios.stream().filter(h -> h.getEmpresa().getId().equals(eid)).toList();
            } catch (NumberFormatException ignored) {}
        }

        // Estadísticas para empresa
        if (usuarioActual.getRol().name().equals("EMPRESA")) {
            Map<String, Long> stats = horariosService.estadisticasEmpresa(usuarioActual);
            model.addAttribute("stats", stats);
        }

        // Lista de empresas para el filtro (solo clientes lo ven)
        List<Usuario> empresas = usuarioService.obtenerTodosLosUsuarios()
                .stream().filter(u -> u.getRol().name().equals("EMPRESA")).toList();

        model.addAttribute("horarios", horarios);
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("empresas", empresas);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroEmpresa", empresaId);
        return "HorariosList";
    }

    // ─── CREAR (empresa para sí misma) ───────────────────────────────────────

    @GetMapping("/crear/{emailEmpresa}")
    public String mostrarCrear(@PathVariable String emailEmpresa,
                               Model model, Authentication authentication) {
        if (!authentication.getName().equals(emailEmpresa)) return "redirect:/acceso-denegado";
        model.addAttribute("empresa", usuarioService.obtenerUsuarioPorEmail(emailEmpresa));
        model.addAttribute("adminMode", false);
        return "CrearHorario";
    }

    @PostMapping("/crear/{emailEmpresa}")
    public String crear(@PathVariable String emailEmpresa,
                        @RequestParam String fecha,
                        @RequestParam String horaInicio,
                        @RequestParam String horaFin,
                        Authentication authentication,
                        RedirectAttributes ra) {
        if (!authentication.getName().equals(emailEmpresa)) return "redirect:/acceso-denegado";
        try {
            Usuario empresa = usuarioService.obtenerUsuarioPorEmail(emailEmpresa);
            horariosService.crearHorario(buildHorario(fecha, horaInicio, horaFin), empresa);
            ra.addFlashAttribute("exito", "Horario creado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── CREAR ADMIN (empresa crea para otra empresa) ─────────────────────────

    @GetMapping("/crear-admin/{emailEmpresa}")
    public String mostrarCrearAdmin(@PathVariable String emailEmpresa, Model model) {
        Usuario empresa = usuarioService.obtenerUsuarioPorEmail(emailEmpresa);
        if (empresa == null) return "redirect:/usuarios";
        model.addAttribute("empresa", empresa);
        model.addAttribute("adminMode", true);
        return "CrearHorario";
    }

    @PostMapping("/crear-admin/{emailEmpresa}")
    public String crearAdmin(@PathVariable String emailEmpresa,
                             @RequestParam String fecha,
                             @RequestParam String horaInicio,
                             @RequestParam String horaFin,
                             RedirectAttributes ra) {
        try {
            Usuario empresa = usuarioService.obtenerUsuarioPorEmail(emailEmpresa);
            horariosService.crearHorario(buildHorario(fecha, horaInicio, horaFin), empresa);
            ra.addFlashAttribute("exito", "Horario creado para " + (empresa.getNombre() != null ? empresa.getNombre() : empresa.getEmail()));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── EDITAR ──────────────────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model, Authentication authentication) {
        HorarioDisponible horario = horariosService.obtenerHorarioPorId(id);
        Usuario empresa = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
        if (!horario.getEmpresa().getId().equals(empresa.getId())) return "redirect:/acceso-denegado";
        if (horario.getClienteReservado() != null) {
            return "redirect:/horarios?error=No puedes editar un horario ya reservado";
        }
        model.addAttribute("horario", horario);
        return "EditarHorario";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @RequestParam String fecha,
                         @RequestParam String horaInicio,
                         @RequestParam String horaFin,
                         Authentication authentication,
                         RedirectAttributes ra) {
        try {
            Usuario empresa = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
            horariosService.actualizarHorario(id, buildHorario(fecha, horaInicio, horaFin), empresa);
            ra.addFlashAttribute("exito", "Horario actualizado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── ELIMINAR ─────────────────────────────────────────────────────────────

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes ra) {
        try {
            Usuario empresa = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
            horariosService.eliminarHorario(id, empresa);
            ra.addFlashAttribute("exito", "Horario eliminado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── RESERVAR ─────────────────────────────────────────────────────────────

    @PostMapping("/reservar/{id}")
    public String reservar(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes ra) {
        try {
            Usuario cliente = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
            horariosService.reservarHorario(id, cliente);
            ra.addFlashAttribute("exito", "¡Reserva realizada correctamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── CANCELAR RESERVA ─────────────────────────────────────────────────────

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes ra) {
        try {
            Usuario cliente = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
            horariosService.cancelarReserva(id, cliente);
            ra.addFlashAttribute("exito", "Reserva cancelada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    // ─── PERFIL ───────────────────────────────────────────────────────────────

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
        model.addAttribute("usuario", usuario);
        return "Perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam(required = false) String nombre,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam(required = false) String direccion,
                                   Authentication authentication,
                                   RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(authentication.getName());
            usuario.setNombre(nombre);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuarioService.crearUsuario(usuario); // save() vía JPA
            ra.addFlashAttribute("exito", "Perfil actualizado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios/perfil";
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────

    private HorarioDisponible buildHorario(String fecha, String horaInicio, String horaFin) {
        HorarioDisponible h = new HorarioDisponible();
        h.setFecha(LocalDate.parse(fecha));
        h.setHoraInicio(LocalTime.parse(horaInicio));
        h.setHoraFin(LocalTime.parse(horaFin));
        return h;
    }
}
