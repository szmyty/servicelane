#!/bin/bash
set -eu

echo '####################################################'
echo 'Pulling base images for project...'
echo '####################################################'

echo 'Pulling Keycloak...'
docker pull quay.io/keycloak/keycloak:19.0.1

echo 'Pulling Elasticsearch...'
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.17.4

echo 'Pulling PostgreSQL...'
docker pull postgres:14.5

echo 'Pulling Redis...'
docker pull redis:6.2.7

echo 'Pulling Sonarqube...'
docker pull sonarqube:9.6.0-community

echo 'Pulling Swagger Editor...'
docker pull swaggerapi/swagger-editor:v4.6.1

echo 'Pulling Zipkin...'
docker pull openzipkin/zipkin:2.23

echo 'Pulling pgAdmin...'
docker pull dpage/pgadmin4:6.20

echo 'Pulling Portainer...'
docker pull portainer/portainer-ce:2.16.2

echo 'Pulling Caddy...'
docker pull caddy:2.6.2-builder
docker pull caddy:2.6.2-alpine

echo '####################################################'
echo 'Finished pulling base images for project!'
echo '####################################################'
