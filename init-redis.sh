#!/bin/bash

until redis-cli ping; do
  echo "Esperando a que Redis esté disponible..."
  sleep 2
done

for i in {1..10}; do
  redis-cli set "empleado:$i" true
done

echo "Redis inicializado con empleados disponibles."
