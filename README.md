# Autos Colombia S.A. - Sistema Integrado (Iteraciones 1, 2 y 3)

Este repositorio contiene el código fuente en Java y el prototipo de interfaz web para el sistema de información del parqueadero **"Autos Colombia S.A."**. El proyecto evidencia la integración funcional de la gestión del flujo vehicular y cobros (Iteración 1), la validación de usuarios y control de disponibilidad de celdas (Iteración 2), y el módulo completo de gestión de pagos y mensualidades (Iteración 3).

## Estructura del Repositorio

- `Parqueadero.java`: Lógica de negocio integrada en Java correspondiente a las Iteraciones 1, 2 y 3.
- `pagos.html`: Prototipo funcional (Mockup) de la interfaz gráfica para el módulo de Gestión de Pagos, Mensualidades y Cierre de Caja (Iteración 3).

## Guía breve para ejecutar y verificar las operaciones

Para comprobar la correcta implementación de las operaciones de entrada, salida, mensualidades, pagos y reglas de negocio, siga estos pasos:

### 1. Requisitos Previos
- Tener instalado Java Development Kit (JDK 8 o superior).
- Contar con un IDE como Visual Studio Code, NetBeans, Eclipse o ejecutar vía consola (CLI).
- Un navegador web actualizado (Google Chrome, Edge o Firefox) para visualizar `pagos.html`.

### 2. Ejecución del Sistema
1. Descargue o clone este repositorio (`https://github.com/MarianaA-A/AutosColombia`).
2. Abra el archivo `Parqueadero.java` en su entorno de desarrollo y ejecute el método `main`.
3. Para explorar la interfaz visual del módulo de pagos, abra el archivo `pagos.html` directamente en su navegador web.

### 3. Verificación de Pruebas (Consola)
Al ejecutar `Parqueadero.java`, la consola simulará automáticamente el siguiente flujo integrado validando los criterios de aceptación de las tres iteraciones:

- **Prueba de Autenticación (Iteración 2):** El sistema verifica las credenciales y el rol (`operario@autos.com` como `Operario`). Si es correcto, autoriza el inicio de sesión para operar el sistema.
- **Prueba de Mensualidad y Salida Exenta (Iteración 3):** Se registra y activa una mensualidad de 30 días calendario para la placa `KLY-104` (SUV) pagada mediante `Transferencia / QR`, se registra su ingreso en la celda `B1` y al procesar su salida el sistema exime el cobro por hora (`$0 COP`), libera la celda y envía la señal de apertura a la talanquera.
- **Prueba de Ingreso Ocasional y Restricciones (Iteraciones 1 y 2):** Se registra el ingreso del vehículo `ABC-123` (Automóvil) en la celda `A1`; posteriormente se rechaza el ingreso de `XYZ-987` (`Motocicleta`) por restricción de tipo de vehículo y se bloquea el ingreso de `DEF-456` en la celda `A1` por encontrarse ocupada.
- **Prueba de Salida Ocasional, Recargo Nocturno y Apertura de Talanquera (Iteraciones 1 y 3):** Se procesa la salida de la placa `ABC-123` tras 4 horas con salida posterior a las 10:00 p.m., sumando el recargo nocturno (`$15.000 COP`), registrando el pago en `Efectivo`, emitiendo el comprobante oficial, liberando la celda `A1` y enviando la señal de apertura a la talanquera.
- **Prueba de Penalidad por Ticket Perdido (Iteraciones 1 y 3):** Se liquida la salida de la placa `MTR-551` (Automóvil) aplicando la tarifa plena de 17 horas (`$136.000 COP`) pagada con `Tarjeta` y emitiendo el comprobante correspondiente.
- **Reporte Consolidado de Cierre de Caja (Iteración 3):** Al finalizar la simulación, el sistema genera el resumen diario de ingresos discriminado por método de pago (`Efectivo`, `Transferencia / QR` y `Tarjeta`) junto con el total general recaudado.