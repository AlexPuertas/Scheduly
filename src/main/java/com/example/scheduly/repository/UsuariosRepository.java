package com.example.scheduly.repository;

import com.example.scheduly.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
