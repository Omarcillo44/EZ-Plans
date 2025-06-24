create table if not exists usuarios
(
    id_usuario        serial
        primary key,
    celular_usuario   varchar not null,
    pass              text    not null,
    nombre_usuario    varchar not null,
    apellidos_usuario varchar not null
);

alter table usuarios
    owner to postgres;

create table if not exists planes
(
    id_plan       serial
        primary key,
    titulo_plan   varchar not null,
    fecha_plan    date    not null,
    gasto_plan    numeric,
    detalles_plan text,
    estado_plan   boolean
);

alter table planes
    owner to postgres;

create table if not exists miembros_plan
(
    id_miembrosplan serial
        primary key,
    id_plan         integer not null
        references planes,
    id_usuario      integer not null
        references usuarios,
    administrador   boolean not null
);

alter table miembros_plan
    owner to postgres;

create table if not exists actividades
(
    id_actividad       serial
        primary key,
    id_plan            integer not null
        references planes,
    titulo_actividad   varchar not null,
    gasto_actividad    numeric,
    detalles_actividad text,
    estado_actividad   boolean
);

alter table actividades
    owner to postgres;

create table if not exists miembros_actividad
(
    id_miembrosactividad serial
        primary key,
    id_actividad         integer not null
        references actividades,
    id_usuario           integer not null,
    aportacion           numeric not null
);

alter table miembros_actividad
    owner to postgres;

create table if not exists deudas
(
    id_deuda             serial
        primary key,
    id_miembrosactividad integer not null
        references miembros_actividad,
    id_acreedor          integer not null,
    monto_deuda          numeric not null
);

alter table deudas
    owner to postgres;

create table if not exists pagos
(
    id_pago          serial
        primary key,
    id_deuda         integer not null
        references deudas,
    monto_pago       numeric not null,
    forma_pago       boolean,
    comprobante_pago text
);

alter table pagos
    owner to postgres;

create table if not exists contactos
(
    id_contactos serial
        primary key,
    id_usuario   integer not null
        references usuarios
);

alter table contactos
    owner to postgres;

