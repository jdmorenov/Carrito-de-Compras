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
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombreUsuario, @RequestParam String contrasena,
                                   @RequestParam String email, Model model) {

        // Verificar si el nombre de usuario o el correo ya existen
        if (usuarioRepository.findByNombreUsuario(nombreUsuario) != null ||
            usuarioRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Nombre de usuario o correo ya existen");
            return "registro";
        }

        // Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(nombreUsuario);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setEmail(email);

        // Guardar el usuario en la base de datos
        usuarioRepository.save(nuevoUsuario);

        // Redirigir al inicio de sesión
        return "redirect:/login";
    }
}