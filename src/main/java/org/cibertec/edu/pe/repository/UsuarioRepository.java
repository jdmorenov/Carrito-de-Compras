package org.cibertec.edu.pe.repository;

import org.cibertec.edu.pe.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Usuario findByNombreUsuario(String nombreUsuario);
	Usuario findByEmail(String email);
}
