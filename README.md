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

2. Ejecutar docker-compose

```bash
sudo docker-compose up --build
```
(docker-compose levanta un servidor redis, un servidor kafka y el aplicativo)

## Dispatcher
### Descripción General

  El Dispatcher es el componente central del sistema Call Flow Manager, encargado de gestionar la distribución de llamadas entrantes de manera eficiente y ordenada. Este módulo evalúa la disponibilidad de los empleados y asigna las llamadas a los operadores, supervisores o directores según la jerarquía definida y la disponibilidad en tiempo real. Además, si no hay empleados disponibles, el Dispatcher coloca las llamadas en una cola de espera, garantizando que ninguna llamada se pierda y todas sean atendidas tan pronto como sea posible. Esta funcionalidad es crucial para mantener la calidad del servicio en el Callcenter y optimizar el tiempo de respuesta a los clientes.

### Configuración

Este proyecto utiliza varias propiedades configurables que pueden ser ajustadas mediante variables de entorno o directamente en los archivos de propiedades. A continuación, se describen las propiedades más importantes:

### Propiedades de Redis

Estas propiedades configuran la conexión a la base de datos Redis.

- `spring.data.redis.host`: Especifica la dirección del host de Redis. Puede ser configurado mediante la variable de entorno `SPRING_REDIS_HOST`. Si no se especifica, se utiliza `localhost` por defecto.

```properties
spring.data.redis.host=${SPRING_REDIS_HOST:localhost}
```

- `spring.data.redis.port`: Especifica el puerto en el cual Redis está escuchando. El valor predeterminado es `6379`.

```properties
spring.data.redis.port=6379
```

### Propiedades de Ejecución Asíncrona

Estas propiedades configuran el comportamiento del executor asíncrono utilizado en el proyecto.

- `spring.async.core-size`: Especifica el tamaño del pool de hilos centrales. Puede ser configurado mediante la variable de entorno `CORE_SIZE`. El valor predeterminado es `10`.

```properties
spring.async.core-size=${CORE_SIZE:10}
```

- `spring.async.max-size`: Especifica el tamaño máximo del pool de hilos. Puede ser configurado mediante la variable de entorno `MAX_SIZE`. El valor predeterminado es `15`.

```properties
spring.async.max-size=${MAX_SIZE:15}
```

- `spring.async.capacity`: Especifica la capacidad de la cola de tareas. Puede ser configurado mediante la variable de entorno `CAPACITY`. El valor predeterminado es `100`.

```properties
spring.async.capacity=${CAPACITY:100}
```

- `spring.async.name-prefix`: Especifica el prefijo para los nombres de los hilos. Puede ser configurado mediante la variable de entorno `NAME_PREFIX`. El valor predeterminado es `Async-`.

```properties
spring.async.name-prefix=${NAME_PREFIX:Async-}
```

### Pruebas

Los test levantan su propio servidor REDIS embebido para pruebas
Los test levantan su propio servidor KAFKA embebido para pruebas

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

#### Cuando no hay operadores disponibles, el resultado es vacio
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest#whenNoAvailableOperators_thenResultIsEmpty test
```

#### Cuando ocurre un error de conexión a Redis, se lanza EmployeeServiceException
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest#whenRedisConnectionErrorOccurs_thenEmployeeServiceExceptionIsThrown test
```

#### Cuando ocurre un error inesperado, se lanza EmployeeServiceException
```bash
mvn -Dtest=GetAvailableEmployeeByTypeRedisAdapterTest#whenUnexpectedErrorOccurs_thenEmployeeServiceExceptionIsThrown test
```

#### Capturar Llamadas Test General
```bash
mvn -Dtest=HandleCallThreadAdapterTest test
```

#### Al ejecutar una llamada con un operador, el estado del operador se establece como disponible después de procesarla
```bash
mvn -Dtest=HandleCallThreadAdapterTest#whenExecutingCallWithOperator_thenOperatorStatusIsSetToAvailableAfterProcessing test
```

#### Cuando ocurre una excepción de conexión a Redis, se lanza CallHandlingException
```bash
mvn -Dtest=HandleCallThreadAdapterTest#whenRedisOperationThrowsException_thenCallHandlingExceptionIsThrown test
```

#### Cuando el hilo es interrumpido, se lanza CallHandlingException
```bash
mvn -Dtest=HandleCallThreadAdapterTest#whenThreadIsInterrupted_thenCallHandlingExceptionIsThrown test
```

### Diagrama
#### Diagrama de Clases
![](https://github.com/PsyBrainy/call-flow-manager/blob/master/image/call_manager.drawio.png)
#### Diagrama de Secuencia
![](https://github.com/PsyBrainy/call-flow-manager/blob/master/image/call_manager-Page-2.drawio.png)

