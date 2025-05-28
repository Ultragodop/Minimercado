package com.project.minimercado.services.auth;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.login.UserPrincipal;
import com.project.minimercado.repository.bussines.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUsrDtlsService implements UserDetailsService {

    private final  UsuarioRepository usuariorepository;
    public MyUsrDtlsService(UsuarioRepository usuariorepository) {
        this.usuariorepository = usuariorepository;
    }
    /*
     * Este metodo es el que se encarga de cargar el usuario desde la base de datos
     * y devolverlo a Spring Security
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuariorepository.findByNombre(username);
        // Si no se encuentra el usuario, lanzamos una excepción
        System.out.println("Buscando usuario: " + username);
        if (usuario == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserPrincipal(usuario);
    }
}

