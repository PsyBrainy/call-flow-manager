# Call Flow Manager

## Tabla de Contenidos
- [Introducción](#introducción)
- [Características](#características)
- [Instalación](#instalación)
- [Dispatcher](#dispatcher)
  - [Descripción General](#descripción-general)
  - [Configuración](#configuración)
  - [Pruebas](#pruebas)

## Introducción
Call Flow Manager es un sistema diseñado para gestionar los flujos de llamadas dentro de un Callcenter, asegurando una asignación eficiente de las llamadas a los empleados disponibles según sus roles.

## Características
- Asignación de llamadas a operadores, supervisores y directores
- Colocación de llamadas en cola cuando no hay empleados disponibles
- Procesamiento concurrente de llamadas

## Instalación
1. Clonar proyecto

```bash
git clone https://github.com/PsyBrainy/call-flow-manager.git
```

2. Ejecutar tests

```bash
mvn clean test
```
## Dispatcher
### Descripción General

  El Dispatcher es el componente central del sistema Call Flow Manager, encargado de gestionar la distribución de llamadas entrantes de manera eficiente y ordenada. Este módulo evalúa la disponibilidad de los empleados y asigna las llamadas a los operadores, supervisores o directores según la jerarquía definida y la disponibilidad en tiempo real. Además, si no hay empleados disponibles, el Dispatcher coloca las llamadas en una cola de espera, garantizando que ninguna llamada se pierda y todas sean atendidas tan pronto como sea posible. Esta funcionalidad es crucial para mantener la calidad del servicio en el Callcenter y optimizar el tiempo de respuesta a los clientes.

### Configuración

### Pruebas

### Sequence Diagram

```seq
Andrew->China: Says Hello 
Note right of China: China thinks\nabout it 
China-->Andrew: How are you? 
Andrew->>China: I am good thanks!
```