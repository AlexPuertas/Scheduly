package com.example.scheduly.controller;

import com.example.scheduly.model.Usuario;
import com.example.scheduly.service.UsuariosService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuariosController(UsuariosService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // Mostrar formulario para crear usuario
    @GetMapping("/crear")
    public String mostrarFormularioCrearUsuario() {
        return "CreaUser";
    }

    // Crear usuario desde formulario
    @PostMapping("/crear")
    public String crearUsuarioForm(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono
    ) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        // ✅ SEGURIDAD: Encriptar contraseña con BCrypt
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Enum.valueOf(com.example.scheduly.model.Rol.class, rol));
        usuario.setNombre(nombre);
        usuario.setDireccion(direccion);
        usuario.setTelefono(telefono);

        usuarioService.crearUsuario(usuario);

        return "redirect:/usuarios";
    }

    // Mostrar lista de todos los usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "ListarUsuarios";
    }
}
