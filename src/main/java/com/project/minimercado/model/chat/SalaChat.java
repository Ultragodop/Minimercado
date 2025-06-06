package com.project.minimercado.model.chat;

import com.project.minimercado.model.bussines.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "salachat") // Asegurate que coincida con tu tabla real
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    // Relación con SalaUsuario (usuarios autorizados)
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalaUsuario> usuariosAutorizados = new ArrayList<>();

    // Relación con mensajes de chat
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> mensajes = new ArrayList<>();
}
