package com.example.scheduly.controller;

import com.example.scheduly.model.Usuario;
import com.example.scheduly.service.UsuariosService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuariosService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuariosService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // Página de inicio (index con login integrado)
    @GetMapping("/")
    public String home(@RequestParam(required = false) String error,
                      @RequestParam(required = false) String logout,
                      @RequestParam(required = false) String registro,
                      Model model) {
        // Agregar mensajes al modelo para que el index los muestre
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("logout", "Has cerrado sesión correctamente");
        }
        if (registro != null) {
            model.addAttribute("registro", "¡Registro exitoso! Ya puedes iniciar sesión");
        }
        return "index";
    }

    // Página de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            Model model
    ) {
        try {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRol(Enum.valueOf(com.example.scheduly.model.Rol.class, rol));
            usuario.setNombre(nombre);
            usuario.setDireccion(direccion);
            usuario.setTelefono(telefono);

            usuarioService.crearUsuario(usuario);

            return "redirect:/?registro=exitoso";  // ✅ Redirige al index
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "registro";
        }
    }

    // Página de acceso denegado
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
