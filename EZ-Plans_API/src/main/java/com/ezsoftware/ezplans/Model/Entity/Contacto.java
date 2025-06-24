package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "contactos")
public class Contacto {
    @Id
    @ColumnDefault("nextval('contactos_id_contactos_seq')")
    @Column(name = "id_contactos", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private com.ezsoftware.ezplans.Model.Entity.Usuario idUsuario;

}