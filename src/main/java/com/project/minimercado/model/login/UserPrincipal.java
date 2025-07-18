package com.project.minimercado.model.login;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.utils.UserDetailsWithId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;


/*
 * Esta clase va a proveerle el usuario a Spring Security
 * Modificada por Santiago Garcia 2025 para que acepte ids de usuario
 * */

public class UserPrincipal implements UserDetailsWithId {

    public Usuario usuario;

    public UserPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public long getId() {
        return usuario.getId();
    }

    @Override
    public String getUsername() {
        return usuario.getNombre();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
