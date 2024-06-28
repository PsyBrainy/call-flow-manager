#!/bin/bash

# Espera a que Redis esté disponible
until redis-cli ping; do
  echo "Esperando a que Redis esté disponible..."
  sleep 2
done

# Añadir empleados de diferentes tipos
for i in {1..5}; do
  redis-cli set "employee:$i:OPERATOR" true
done

for i in {6..8}; do
  redis-cli set "employee:$i:SUPERVISOR" true
done

for i in {9..10}; do
  redis-cli set "employee:$i:DIRECTOR" true
done

echo "Redis inicializado con empleados disponibles."

