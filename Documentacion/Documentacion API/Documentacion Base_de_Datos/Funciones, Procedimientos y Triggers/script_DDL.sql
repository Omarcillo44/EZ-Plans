create or replace function actualizar_estado_actividad_plan() returns trigger
    language plpgsql
as
$$
DECLARE
    actividad_id INTEGER;
    plan_id INTEGER;
    total_deuda NUMERIC;
    id_miembro INTEGER;
BEGIN
    -- Obtener el id_miembrosActividad dependiendo del tipo de operación
    IF TG_OP = 'DELETE' THEN
        id_miembro := OLD.id_miembrosActividad;
    ELSE
        id_miembro := NEW.id_miembrosActividad;
    END IF;

    -- Obtener el id_actividad desde miembros_actividad
    SELECT ma.id_actividad INTO actividad_id
    FROM miembros_actividad ma
    WHERE ma.id_miembrosActividad = id_miembro;

    -- Obtener el id_plan desde actividades
    SELECT a.id_plan INTO plan_id
    FROM actividades a
    WHERE a.id_actividad = actividad_id;

    -- Calcular la deuda total de la actividad
    SELECT COALESCE(SUM(d.monto_deuda), 0) INTO total_deuda
    FROM deudas d
             JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
    WHERE ma.id_actividad = actividad_id;

    -- Actualizar estado de la actividad según la deuda
    UPDATE actividades
    SET estado_actividad = (total_deuda = 0)
    WHERE id_actividad = actividad_id;

    -- Verificar si hay al menos una actividad en falso dentro del plan
    IF EXISTS (
        SELECT 1 FROM actividades WHERE id_plan = plan_id AND estado_actividad = FALSE
    ) THEN
        UPDATE planes SET estado_plan = FALSE WHERE id_plan = plan_id;
    ELSE
        UPDATE planes SET estado_plan = TRUE WHERE id_plan = plan_id;
    END IF;

    RETURN NULL;
END;
$$;

alter function actualizar_estado_actividad_plan() owner to postgres;

create trigger trg_actualizar_estado_actividad_plan
    after insert or update
    on deudas
    for each row
execute procedure actualizar_estado_actividad_plan();

create trigger trigger_actualizar_estado
    after insert or update or delete
    on deudas
    for each row
execute procedure actualizar_estado_actividad_plan();

create trigger trg_actualizar_estado_despues_deuda
    after insert or update or delete
    on deudas
    for each row
execute procedure actualizar_estado_actividad_plan();

create or replace function obtener_resumen_usuario(p_id_usuario integer)
    returns TABLE("Planes administrados" integer, "Planes como participante" integer, "Deudas pendientes" text, "Deudas por cobrar" text)
    language plpgsql
as
$$
BEGIN
    RETURN QUERY
        SELECT
            -- Planes administrados
            (SELECT COUNT(*)::INTEGER
             FROM miembros_plan mp
             WHERE mp.id_usuario = p_id_usuario AND mp.administrador = true),

            -- Planes como participante
            (SELECT COUNT(*)::INTEGER
             FROM miembros_plan mp
             WHERE mp.id_usuario = p_id_usuario AND mp.administrador = false),

            -- Deudas pendientes (formato)
            CASE
                WHEN COALESCE((
                                  SELECT SUM(d.monto_deuda)
                                  FROM deudas d
                                           JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                                  WHERE ma.id_usuario = p_id_usuario AND d.monto_deuda > 0
                              ), 0) = 0 THEN '$0'
                ELSE TO_CHAR((
                                 SELECT SUM(d.monto_deuda)
                                 FROM deudas d
                                          JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                                 WHERE ma.id_usuario = p_id_usuario AND d.monto_deuda > 0
                             ), 'FM$999,999,999.00')
                END::TEXT,

            -- Deudas por cobrar (formato)
            CASE
                WHEN COALESCE((
                                  SELECT SUM(d.monto_deuda)
                                  FROM deudas d
                                  WHERE d.id_acreedor = p_id_usuario AND d.monto_deuda > 0
                              ), 0) = 0 THEN '$0'
                ELSE TO_CHAR((
                                 SELECT SUM(d.monto_deuda)
                                 FROM deudas d
                                 WHERE d.id_acreedor = p_id_usuario AND d.monto_deuda > 0
                             ), 'FM$999,999,999.00')
                END::TEXT;
END;
$$;

alter function obtener_resumen_usuario(integer) owner to postgres;

create or replace function obtener_planes_usuario(p_id_usuario integer, p_estado_plan boolean DEFAULT NULL::boolean, p_es_admin boolean DEFAULT NULL::boolean)
    returns TABLE("ID Plan" integer, "Título" text, "Fecha" text, "Gasto total" text, "Número de miembros" text, "Rol del usuario" text, "Estado del plan" text)
    language plpgsql
as
$$
BEGIN
    RETURN QUERY
        SELECT
            p.id_plan,
            p.titulo_plan::TEXT,
            TO_CHAR(p.fecha_plan, 'DD-MM/YY')::TEXT,
            CASE
                WHEN COALESCE(p.gasto_plan, 0) = 0 THEN '$0'
                ELSE TO_CHAR(p.gasto_plan, 'FM$999,999,999.00')
                END::TEXT,
            (COUNT(mp_all.id_usuario) || ' miembros')::TEXT,
            (CASE
                 WHEN mp_usuario.administrador THEN 'Admin.'
                 ELSE 'Miembro'
                END)::TEXT,
            (CASE
                 WHEN p.estado_plan THEN 'Completo'
                 ELSE 'Pendiente'
                END)::TEXT
        FROM PLANES p
                 JOIN MIEMBROS_PLAN mp_usuario
                      ON p.id_plan = mp_usuario.id_plan
                          AND mp_usuario.id_usuario = p_id_usuario
                 JOIN MIEMBROS_PLAN mp_all
                      ON p.id_plan = mp_all.id_plan
        WHERE
            (p_estado_plan IS NULL OR p.estado_plan = p_estado_plan)
          AND (p_es_admin IS NULL OR mp_usuario.administrador = p_es_admin)
        GROUP BY
            p.id_plan,
            p.titulo_plan,
            p.fecha_plan,
            p.gasto_plan,
            p.estado_plan,
            mp_usuario.administrador
        ORDER BY p.fecha_plan;
END;
$$;

alter function obtener_planes_usuario(integer, boolean, boolean) owner to postgres;

create or replace function obtener_actividades_por_plan(id_plan_input integer)
    returns TABLE(idactividad integer, titulo text, montototal text, miembros text, estado text)
    language sql
as
$$
SELECT
    a.id_actividad,
    a.titulo_actividad,
    CASE
        WHEN COALESCE(a.gasto_actividad, 0) = 0 THEN '$0'
        ELSE TO_CHAR(a.gasto_actividad, 'FM$999,999,999.00')
        END AS montototal,
    (
        SELECT COUNT(ma.id_usuario) || ' miembros'
        FROM MIEMBROS_ACTIVIDAD ma
        WHERE ma.id_actividad = a.id_actividad
    ) AS miembros,
    CASE
        WHEN a.estado_actividad THEN 'Completa'
        ELSE 'Pendiente'
        END AS estado
FROM ACTIVIDADES a
WHERE a.id_plan = id_plan_input
ORDER BY a.estado_actividad;
$$;

alter function obtener_actividades_por_plan(integer) owner to postgres;

create or replace function obtener_resumen_miembros_plan(idplan integer)
    returns TABLE(id_miembro integer, nombre text, celular text, monto_total_deuda text, aportacion_total text, tiene_deuda boolean)
    language plpgsql
as
$$
BEGIN
    RETURN QUERY
        SELECT
            u.id_usuario::INTEGER,
            u.nombre_usuario::TEXT,
            u.celular_usuario::TEXT,

            CASE
                WHEN COALESCE(deuda_total, 0) = 0 THEN '$0'
                ELSE TO_CHAR(deuda_total, 'FM$999,999,999.00')
                END::TEXT,

            CASE
                WHEN COALESCE(aportacion.aportacion_total, 0) = 0 THEN '$0'
                ELSE TO_CHAR(aportacion.aportacion_total, 'FM$999,999,999.00')
                END::TEXT,

            (COALESCE(deuda_total, 0) > 0)::BOOLEAN

        FROM MIEMBROS_PLAN mp
                 JOIN USUARIOS u ON mp.id_usuario = u.id_usuario
                 LEFT JOIN LATERAL (
            SELECT SUM(d.monto_deuda) AS deuda_total
            FROM DEUDAS d
                     JOIN MIEMBROS_ACTIVIDAD ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                     JOIN ACTIVIDADES a ON ma.id_actividad = a.id_actividad
            WHERE a.id_plan = mp.id_plan AND ma.id_usuario = u.id_usuario
            ) deuda ON true
                 LEFT JOIN LATERAL (
            SELECT SUM(ma.aportacion) AS aportacion_total
            FROM MIEMBROS_ACTIVIDAD ma
                     JOIN ACTIVIDADES a ON ma.id_actividad = a.id_actividad
            WHERE a.id_plan = mp.id_plan AND ma.id_usuario = u.id_usuario
            ) aportacion ON true
        WHERE mp.id_plan = idPlan;
END;
$$;

alter function obtener_resumen_miembros_plan(integer) owner to postgres;

create or replace function actualizar_gasto_actividad() returns trigger
    language plpgsql
as
$$
BEGIN
    UPDATE ACTIVIDADES
    SET gasto_actividad = (
        SELECT COALESCE(SUM(aportacion), 0)
        FROM MIEMBROS_ACTIVIDAD
        WHERE id_actividad = NEW.id_actividad
    )
    WHERE id_actividad = NEW.id_actividad;

    RETURN NULL;
END;
$$;

alter function actualizar_gasto_actividad() owner to postgres;

create trigger trg_actualizar_gasto_actividad
    after insert or update or delete
    on miembros_actividad
    for each row
execute procedure actualizar_gasto_actividad();

create or replace function actualizar_gasto_plan() returns trigger
    language plpgsql
as
$$
DECLARE
    id_plan_actual INTEGER;
BEGIN
    IF TG_OP = 'DELETE' THEN
        SELECT id_plan INTO id_plan_actual FROM ACTIVIDADES WHERE id_actividad = OLD.id_actividad;
    ELSE
        SELECT id_plan INTO id_plan_actual FROM ACTIVIDADES WHERE id_actividad = NEW.id_actividad;
    END IF;

    UPDATE PLANES
    SET gasto_plan = (
        SELECT COALESCE(SUM(gasto_actividad), 0)
        FROM ACTIVIDADES
        WHERE id_plan = id_plan_actual
    )
    WHERE id_plan = id_plan_actual;

    RETURN NULL;
END;
$$;

alter function actualizar_gasto_plan() owner to postgres;

create trigger trg_actualizar_gasto_plan
    after insert or update or delete
    on actividades
    for each row
execute procedure actualizar_gasto_plan();

create or replace function obten_miembros_plan(idplan integer)
    returns TABLE(id_usuario integer, celular_usuario character varying, nombre_usuario character varying, apellidos_usuario character varying)
    language sql
as
$$
SELECT
    mp.id_usuario,
    u.celular_usuario,
    u.nombre_usuario,
    u.apellidos_usuario
FROM miembros_plan mp
         JOIN public.usuarios u ON u.id_usuario = mp.id_usuario
WHERE mp.id_plan = idPlan;
$$;

alter function obten_miembros_plan(integer) owner to postgres;

create or replace function insertar_pago(p_id_deuda integer, p_monto_pago numeric, p_forma_pago boolean, p_comprobante_pago text) returns void
    language plpgsql
as
$$
declare
    v_deuda_actual numeric;
begin
    -- Validar que el pago sea mayor a 0
    if p_monto_pago <= 0 then
        raise exception 'El monto del pago debe ser mayor a 0';
    end if;

    -- Obtener monto actual de la deuda
    select monto_deuda into v_deuda_actual
    from deudas
    where id_deuda = p_id_deuda;

    if not found then
        raise exception 'No existe una deuda con el id proporcionado: %', p_id_deuda;
    end if;

    -- Validar que no exceda la deuda
    if p_monto_pago > v_deuda_actual then
        raise exception 'El monto del pago (%s) excede la deuda actual (%s)', p_monto_pago, v_deuda_actual;
    end if;

    -- Insertar el pago
    insert into pagos (id_deuda, monto_pago, forma_pago, comprobante_pago)
    values (p_id_deuda, p_monto_pago, p_forma_pago, p_comprobante_pago);

    -- Actualizar el monto de la deuda
    update deudas
    set monto_deuda = monto_deuda - p_monto_pago
    where id_deuda = p_id_deuda;
end;
$$;

alter function insertar_pago(integer, numeric, boolean, text) owner to postgres;

create or replace function obtener_deudas_por_plan(idplan integer)
    returns TABLE(id_deuda integer, id_deudor integer, nombre_deudor character varying, id_acreedor integer, nombre_acreedor character varying, titulo_actividad character varying, monto_deuda text)
    language sql
as
$$
SELECT
    d.id_deuda,
    u_deudor.id_usuario,
    u_deudor.nombre_usuario,
    u_acreedor.id_usuario,
    u_acreedor.nombre_usuario,
    a.titulo_actividad,
    CASE
        WHEN COALESCE(d.monto_deuda, 0) = 0 THEN '$0'
        ELSE TO_CHAR(d.monto_deuda, 'FM$999,999,999.00')
        END AS monto_deuda
FROM DEUDAS d
         JOIN MIEMBROS_ACTIVIDAD ma_deudor ON d.id_miembrosActividad = ma_deudor.id_miembrosActividad
         JOIN USUARIOS u_deudor ON ma_deudor.id_usuario = u_deudor.id_usuario
         JOIN USUARIOS u_acreedor ON d.id_acreedor = u_acreedor.id_usuario
         JOIN ACTIVIDADES a ON ma_deudor.id_actividad = a.id_actividad
WHERE a.id_plan = idPlan
  AND d.monto_deuda > 0
ORDER BY d.id_deuda;
$$;

alter function obtener_deudas_por_plan(integer) owner to postgres;

create or replace procedure eliminar_plan_completo(IN integer)
    language plpgsql
as
$$
BEGIN
    -- Eliminar pagos
    DELETE FROM PAGOS
    WHERE id_deuda IN (
        SELECT d.id_deuda
        FROM DEUDAS d
                 JOIN MIEMBROS_ACTIVIDAD ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                 JOIN ACTIVIDADES a ON ma.id_actividad = a.id_actividad
        WHERE a.id_plan = $1
    );

    -- Eliminar deudas
    DELETE FROM DEUDAS
    WHERE id_miembrosActividad IN (
        SELECT ma.id_miembrosActividad
        FROM MIEMBROS_ACTIVIDAD ma
                 JOIN ACTIVIDADES a ON ma.id_actividad = a.id_actividad
        WHERE a.id_plan = $1
    );

    -- Eliminar miembros_actividad
    DELETE FROM MIEMBROS_ACTIVIDAD
    WHERE id_actividad IN (
        SELECT id_actividad FROM ACTIVIDADES WHERE id_plan = $1
    );

    -- Eliminar actividades
    DELETE FROM ACTIVIDADES
    WHERE id_plan = $1;

    -- Eliminar miembros_plan
    DELETE FROM MIEMBROS_PLAN
    WHERE id_plan = $1;

    -- Eliminar plan
    DELETE FROM PLANES
    WHERE id_plan = $1;
END;
$$;

alter procedure eliminar_plan_completo(integer) owner to postgres;

create or replace function obtener_resumen_plan(id_plan_input integer)
    returns TABLE(idplan integer, titulo text, fecha text, estado text, avance numeric, gastototal text, numerodemiembros text, actividadescompletadas text, deudaspendientes integer, idadministrador integer)
    language plpgsql
as
$$
BEGIN
    RETURN QUERY
        SELECT
            p.id_plan::INTEGER AS idplan,
            p.titulo_plan::TEXT AS titulo,
            TO_CHAR(p.fecha_plan, 'DD-MM/YY')::TEXT AS fecha,
            CASE WHEN p.estado_plan THEN 'Completo' ELSE 'Pendiente' END::TEXT AS estado,

            ROUND(
                    100.0 * (
                        SELECT COUNT(*)
                        FROM deudas d
                                 JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                                 JOIN actividades act ON ma.id_actividad = act.id_actividad
                        WHERE act.id_plan = p.id_plan AND d.monto_deuda = 0
                    )::NUMERIC /
                    NULLIF((
                               SELECT COUNT(*)
                               FROM deudas d
                                        JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                                        JOIN actividades act ON ma.id_actividad = act.id_actividad
                               WHERE act.id_plan = p.id_plan
                           ), 0),
                    2
            )::NUMERIC AS avance,

            CASE
                WHEN COALESCE(p.gasto_plan, 0) = 0 THEN '$0'
                ELSE TO_CHAR(p.gasto_plan, 'FM$999,999,999.00')
                END::TEXT AS gastototal,

            (
                SELECT (COUNT(DISTINCT mp.id_usuario) || ' miembros')::TEXT
                FROM miembros_plan mp
                WHERE mp.id_plan = p.id_plan
            ) AS numerodemiembros,

            (
                SELECT (COUNT(*) || '/' || (
                    SELECT COUNT(*) FROM actividades a2 WHERE a2.id_plan = p.id_plan
                ))::TEXT
                FROM actividades a1
                WHERE a1.id_plan = p.id_plan AND a1.estado_actividad
            ) AS actividadescompletadas,

            COALESCE((
                         SELECT COUNT(*)::INTEGER
                         FROM deudas d
                                  JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
                                  JOIN actividades act ON ma.id_actividad = act.id_actividad
                         WHERE act.id_plan = p.id_plan AND d.monto_deuda > 0
                     ), 0)::INTEGER AS deudaspendientes,

            (
                SELECT mp.id_usuario
                FROM miembros_plan mp
                WHERE mp.id_plan = p.id_plan AND mp.administrador
                LIMIT 1
            ) AS idadministrador

        FROM planes p
        WHERE p.id_plan = id_plan_input;
END;
$$;

alter function obtener_resumen_plan(integer) owner to postgres;

create or replace procedure actualizar_plan(IN pid_plan integer, IN ptitulo_plan character varying, IN pdetalles_plan text)
    language plpgsql
as
$$
BEGIN
    UPDATE planes
    SET titulo_plan = ptitulo_plan,
        detalles_plan = pdetalles_plan
    WHERE id_plan = pid_plan;
END;
$$;

alter procedure actualizar_plan(integer, varchar, text) owner to postgres;

create or replace procedure actualizar_plan(IN pid_plan integer, IN ptitulo_plan character varying, IN pdetalles_plan text, IN pfecha_plan date)
    language plpgsql
as
$$
BEGIN
    UPDATE planes
    SET titulo_plan = ptitulo_plan,
        detalles_plan = pdetalles_plan,
        fecha_plan = pfecha_plan
    WHERE id_plan = pid_plan;
END;
$$;

alter procedure actualizar_plan(integer, varchar, text, date) owner to postgres;

create or replace function obtener_datos_editar_plan(idplaninput integer)
    returns TABLE(idplan integer, titulo character varying, fecha date, detalles text, idadministrador integer)
    language sql
as
$$
SELECT
    p.id_plan,
    p.titulo_plan,
    p.fecha_plan,
    p.detalles_plan,
    (
        SELECT mp.id_usuario
        FROM miembros_plan mp
        WHERE mp.id_plan = p.id_plan AND mp.administrador = true
        LIMIT 1
    ) AS idAdministrador
FROM planes p
WHERE p.id_plan = idPlanInput;
$$;

alter function obtener_datos_editar_plan(integer) owner to postgres;

create or replace function obtener_datos_vista_pago(idpagoinput integer, idusuarioinput integer)
    returns TABLE(idpago integer, nombredeudor character varying, nombreacreedor character varying, tituloplan character varying, tituloactividad character varying, montorestantedeuda text, montopago text, comprobantepago character varying)
    language sql
as
$$
SELECT
    p.id_pago,
    u_deudor.nombre_usuario AS nombreDeudor,
    u_acreedor.nombre_usuario AS nombreAcreedor,
    pl.titulo_plan AS tituloPlan,
    a.titulo_actividad AS tituloActividad,

    CASE
        WHEN d.monto_deuda = 0 THEN '$0'
        ELSE TO_CHAR(d.monto_deuda, 'FM$999,999,999.00')
        END AS montoRestanteDeuda,

    CASE
        WHEN p.monto_pago = 0 THEN '$0'
        ELSE TO_CHAR(p.monto_pago, 'FM$999,999,999.00')
        END AS montoPago,

    p.comprobante_pago
FROM pagos p
         JOIN deudas d ON p.id_deuda = d.id_deuda
         JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
         JOIN usuarios u_deudor ON ma.id_usuario = u_deudor.id_usuario
         JOIN usuarios u_acreedor ON d.id_acreedor = u_acreedor.id_usuario
         JOIN actividades a ON ma.id_actividad = a.id_actividad
         JOIN planes pl ON a.id_plan = pl.id_plan
WHERE p.id_pago = idPagoInput
  AND ma.id_usuario = idUsuarioInput;
$$;

alter function obtener_datos_vista_pago(integer, integer) owner to postgres;

create or replace function obtener_datos_vista_pago(idusuarioinput integer)
    returns TABLE(idpago integer, nombredeudor character varying, nombreacreedor character varying, tituloplan character varying, tituloactividad character varying, montorestantedeuda text, montopago text, comprobantepago character varying)
    language sql
as
$$
SELECT
    p.id_pago,
    u_deudor.nombre_usuario AS nombreDeudor,
    u_acreedor.nombre_usuario AS nombreAcreedor,
    pl.titulo_plan AS tituloPlan,
    a.titulo_actividad AS tituloActividad,

    CASE
        WHEN d.monto_deuda = 0 THEN '$0'
        ELSE TO_CHAR(d.monto_deuda, 'FM$999,999,999.00')
        END AS montoRestanteDeuda,

    CASE
        WHEN p.monto_pago = 0 THEN '$0'
        ELSE TO_CHAR(p.monto_pago, 'FM$999,999,999.00')
        END AS montoPago,

    p.comprobante_pago
FROM pagos p
         JOIN deudas d ON p.id_deuda = d.id_deuda
         JOIN miembros_actividad ma ON d.id_miembrosActividad = ma.id_miembrosActividad
         JOIN usuarios u_deudor ON ma.id_usuario = u_deudor.id_usuario
         JOIN usuarios u_acreedor ON d.id_acreedor = u_acreedor.id_usuario
         JOIN actividades a ON ma.id_actividad = a.id_actividad
         JOIN planes pl ON a.id_plan = pl.id_plan
WHERE ma.id_usuario = idUsuarioInput
ORDER BY p.id_pago;
$$;

alter function obtener_datos_vista_pago(integer) owner to postgres;


