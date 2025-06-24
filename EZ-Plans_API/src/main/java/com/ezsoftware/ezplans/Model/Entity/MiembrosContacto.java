package com.ezsoftware.ezplans.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "miembros_contactos")
public class MiembrosContacto {
    @Id
    @ColumnDefault("nextval('miembros_contactos_id_miembroscontactos_seq')")
    @Column(name = "id_miembroscontactos", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_contactos", nullable = false)
    private Contacto idContactos;

    @Column(name = "id_miembrocontacto", nullable = false)
    private Integer idMiembrocontacto;

}