
-- =============================================
-- SIFI Seed Data
-- Generated: 2025-11-04
-- Nota: Usa IDs explícitos con OVERRIDING SYSTEM VALUE
-- para mantener referencias estables.
-- =============================================

BEGIN;

-- (Opcional) Limpiar datos previos
-- TRUNCATE TABLE public.usopuntodetalle, public.usopuntocabecera,
--   public.bolsapunto, public.cliente, public.nacionalidades,
--   public.vencimientopunto, public.tipos_documentos,
--   public.reglaasignacionpunto, public.conceptopunto, public.paises
-- RESTART IDENTITY CASCADE;

-- ==========================
-- 1) Catálogos básicos
-- ==========================
INSERT INTO public.paises (cod_pais, abreviatura, descripcion)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 'PY', 'PARAGUAY'),
  (2, 'AR', 'ARGENTINA')
ON CONFLICT (cod_pais) DO NOTHING;

INSERT INTO public.tipos_documentos (cod_tipo_documento, descripcion)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 'CEDULA DE IDENTIDAD'),
  (2, 'PASAPORTE')
ON CONFLICT (cod_tipo_documento) DO NOTHING;

INSERT INTO public.nacionalidades (cod_nacionalidad, nacionalidad, cod_pais)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 'PARAGUAYA', 1),
  (2, 'ARGENTINA', 2)
ON CONFLICT (cod_nacionalidad) DO NOTHING;

-- ==========================
-- 2) Reglas de asignación
--    (tier básico + tier bonificado)
-- ==========================
-- Tier 1: hasta 19.999 → 1 punto cada 10.000
INSERT INTO public.reglaasignacionpunto (id, limite_inferior, limite_superior, monto_equivalente, punto_equivalente)
  OVERRIDING SYSTEM VALUE
VALUES (1, NULL, 19999, 10000, 1)
ON CONFLICT (id) DO NOTHING;

-- Tier 2: desde 20.000 → 2 puntos cada 10.000 (doble acumulación)
INSERT INTO public.reglaasignacionpunto (id, limite_inferior, limite_superior, monto_equivalente, punto_equivalente)
  OVERRIDING SYSTEM VALUE
VALUES (2, 20000, NULL, 10000, 2)
ON CONFLICT (id) DO NOTHING;

-- ==========================
-- 3) Vencimientos
-- ==========================
INSERT INTO public.vencimientopunto (id, dias_validez, fecha_inicio, fecha_fin)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 30, DATE '2025-11-04', DATE '2025-12-03'),
  (2, 90, DATE '2025-11-04', DATE '2026-02-01')
ON CONFLICT (id) DO NOTHING;

-- ==========================
-- 4) Clientes (email en lower por trigger)
-- ==========================
INSERT INTO public.cliente (id, nombre, apellido, numero_documento, cod_nacionalidad, cod_tipo_documento, fecha_nacimiento, celular, email)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 'Nico', 'Pizurno', '5555555', 1, 1, DATE '1996-10-20', '0991999999', 'nico@example.com'),
  (2, 'Maria', 'Garcia', '1234567', 1, 1, DATE '1998-05-10', '0981123456', 'maria.garcia@example.com')
ON CONFLICT (id) DO NOTHING;

-- ==========================
-- 5) Conceptos de canje
-- ==========================
INSERT INTO public.conceptopunto (id, concepto, punto_requerido)
  OVERRIDING SYSTEM VALUE
VALUES
  (1, 'CANJE BASICO', 2),
  (2, 'CANJE PREMIUM', 5)
ON CONFLICT (id) DO NOTHING;

-- ==========================
-- 6) Bolsas (triggers calculan puntaje_asignado/saldo)
-- ==========================
-- Cliente 1: dos bolsas, una de 25.000 (tier 2 ⇒ floor(25000/10000)*2 = 4 pts),
--            otra de 12.000 (tier 1 ⇒ floor(12000/10000)*1 = 1 pt)
INSERT INTO public.bolsapunto (idcliente, idvencimientopunto, monto_operacion)
VALUES
  (1, 1, 25000.00),
  (1, 2, 12000.00);

-- Cliente 2: una bolsa de 8.000 (tier 1 ⇒ 0 pts)
INSERT INTO public.bolsapunto (idcliente, idvencimientopunto, monto_operacion)
VALUES
  (2, 1, 8000.00);

-- ==========================
-- 7) Canje de ejemplo (consumo FIFO)
--    Usa punto_requerido del concepto. Aquí: concepto 1 = 2 puntos.
-- ==========================
SELECT fn_usar_puntos(1, 1, NULL);

COMMIT;

-- ==========================
-- Verificaciones rápidas
-- ==========================
-- Totales por cliente
-- SELECT c.id, c.nombre, c.apellido,
--        COALESCE(SUM(b.puntaje_asignado),0) AS asignado,
--        COALESCE(SUM(b.puntaje_utilizado),0) AS utilizado,
--        COALESCE(SUM(b.saldo_punto),0) AS saldo
-- FROM cliente c
-- LEFT JOIN bolsapunto b ON b.idcliente = c.id
-- GROUP BY c.id, c.nombre, c.apellido
-- ORDER BY c.id;

-- Canjes creados
-- SELECT * FROM usopuntocabecera ORDER BY id DESC;
-- SELECT * FROM usopuntodetalle ORDER BY id DESC;

-- Bolsas con detalle de vencimiento
-- SELECT b.id, b.idcliente, b.monto_operacion, b.puntaje_asignado, b.puntaje_utilizado, b.saldo_punto,
--        v.fecha_fin
-- FROM bolsapunto b
-- LEFT JOIN vencimientopunto v ON v.id = b.idvencimientopunto
-- ORDER BY b.id;
