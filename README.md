# Call Flow Manager

## Tabla de Contenidos
- [Introducción](#introducción)
- [Características](#características)
- [Instalación](#instalación)
- [Dispatcher](#dispatcher)
  - [Descripción General](#descripción-general)
  - [Configuración](#configuración)
  - [Pruebas](#pruebas)
  - [Diagrama](#diagrama)

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

2. Ejecutar docker_compose

```bash
sudo docker-compose up --build
```


## Dispatcher
### Descripción General

  El Dispatcher es el componente central del sistema Call Flow Manager, encargado de gestionar la distribución de llamadas entrantes de manera eficiente y ordenada. Este módulo evalúa la disponibilidad de los empleados y asigna las llamadas a los operadores, supervisores o directores según la jerarquía definida y la disponibilidad en tiempo real. Además, si no hay empleados disponibles, el Dispatcher coloca las llamadas en una cola de espera, garantizando que ninguna llamada se pierda y todas sean atendidas tan pronto como sea posible. Esta funcionalidad es crucial para mantener la calidad del servicio en el Callcenter y optimizar el tiempo de respuesta a los clientes.

### Configuración

### Pruebas

#### Dispatcher Test General
```bash
mvn -Dtest=DispatchCallUseCaseTest test
```

#### Al despachar llamadas de manera concurrente, cada llamada se despacha con éxito
```bash
mvn -Dtest=DispatchCallUseCaseTest#whenDispatchingCallsConcurrently_thenEachCallIsDispatchedSuccessfully test
```
#### Cuando no hay empleados disponibles, la llamada se pone en cola y se procesa cuando un empleado cambia de estado
```bash
mvn -Dtest=DispatchCallUseCaseTest#whenNoEmployeesAreAvailable_thenCallIsQueuedAndProcessedWhenAnEmployeeBecomesAvailable test
```

#### Obtener Empleado Disponible Test General
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest test
```

#### Al asignar una llamada, se elige al primer empleado disponible con prioridad para los operadores
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest#whenAssigningCall_thenFirstAvailableEmployeeWithPriorityToOperatorsIsChosen test
```
#### Cuando no hay operadores disponibles, el resultado es nulo
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest#whenNoAvailableOperators_thenResultIsNull test
```

#### Capturar Llamadas Test General
```bash
mvn -Dtest=HandleCallThreadAdapterTest test
```

#### "Al ejecutar una llamada con un operador, el estado del operador se establece como disponible después de procesarla
```bash
mvn -Dtest=HandleCallThreadAdapterTest#whenExecutingCallWithOperator_thenOperatorStatusIsSetToAvailableAfterProcessing test
```

### Diagrama

![](https://github.com/PsyBrainy/call-flow-manager/blob/develop/image/call_manager.drawio.png)

