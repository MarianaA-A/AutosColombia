# Autos Colombia S.A. - Sistema Integrado (Iteraciones 1 y 2)

Este repositorio contiene el código fuente en Java para el sistema de información del parqueadero "Autos Colombia". El código evidencia la integración funcional de la gestión del flujo vehicular y cobros (Iteración 1) con los nuevos módulos de validación de usuarios y control de disponibilidad de celdas (Iteración 2).

## Guía breve para ejecutar y verificar las operaciones

Para que los evaluadores puedan comprobar la correcta implementación de las operaciones de entrada, salida y validación de reglas de negocio, sigan estos pasos:

### 1. Requisitos Previos
* Tener instalado Java Development Kit (JDK 8 o superior).
* Contar con un IDE como NetBeans, Eclipse, o ejecutar vía consola (CLI).

### 2. Ejecución del Sistema
1. Descargue o clone este repositorio.
2. Abra el archivo `Parqueadero.java` en su entorno de desarrollo.
3. Ejecute el método `main` (Run File).

### 3. Verificación de Pruebas (Consola)
Al ejecutar el sistema, la consola simulará automáticamente el siguiente flujo integrado validando los criterios de aceptación:

* **Prueba de Autenticación (Iteración 2):** El sistema verifica las credenciales y el rol (ej. `operario@autos.com`). Si es correcto, permite el inicio de sesión para operar el sistema.
* **Prueba de Ingreso Exitoso:** Se registra la placa `ABC-123` (Automóvil), el sistema valida que la celda `A1` esté libre, la asigna y cambia su estado a ocupada.
* **Prueba de Restricción por Tipo (Iteración 2):** Se intenta ingresar el vehículo `XYZ-987` categorizado como `Motocicleta`. El sistema rechaza la operación cumpliendo la restricción de admitir únicamente Autos y SUVs.
* **Prueba de Control de Disponibilidad (Iteración 2):** Se intenta ingresar un nuevo vehículo (`DEF-456`) a la celda `A1`. El sistema lo bloquea porque la celda ya fue ocupada en el primer paso.
* **Prueba de Salida y Cobro (Iteración 1 y 2):** Se procesa la salida de la placa `ABC-123` tras 3 horas. El sistema calcula la tarifa exacta, genera el cobro y libera automáticamente la celda `A1` para un nuevo uso.
