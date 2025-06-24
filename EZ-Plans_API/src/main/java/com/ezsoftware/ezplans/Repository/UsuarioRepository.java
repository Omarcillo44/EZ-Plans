package com.ezsoftware.ezplans.Repository;

import com.ezsoftware.ezplans.Model.DTO.Acceso.DatosUsuario;
import com.ezsoftware.ezplans.Model.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCelularUsuario(String celularUsuario);

    @Query(value = "SELECT usuarios.id_usuario, usuarios.celular_usuario, usuarios.nombre_usuario, " +
            "usuarios.apellidos_usuario FROM usuarios WHERE usuarios.celular_usuario = :celularUsuario", nativeQuery = true)
    Optional<DatosUsuario> obtenerDatosUsuario(
            @Param("celularUsuario") String celularUsuario
    );

}
