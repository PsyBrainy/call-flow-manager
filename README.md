# Call Flow Manager

## Tabla de Contenidos
- [Introducción](#introducción)
- [Características](#características)
- [Instalación](#instalación)
- [Uso](#uso)
- [Dispatcher](#dispatcher)
  - [Descripción General](#descripción-general)
  - [Configuración](#configuración)
  - [Ejemplo de Uso](#ejemplo-de-uso)
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
