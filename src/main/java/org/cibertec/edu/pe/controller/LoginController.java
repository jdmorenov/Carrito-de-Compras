package org.cibertec.edu.pe.controller;

import org.cibertec.edu.pe.modelo.Usuario;
import org.cibertec.edu.pe.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarInicioSesion(@RequestParam String email, @RequestParam String contrasena, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            // Inicio de sesión exitoso
        	return "index";
        } else {
            // Inicio de sesión fallido
            model.addAttribute("error", "Nombre de usuario o contraseña incorrectos");
            return "login";
        }
    }
}
