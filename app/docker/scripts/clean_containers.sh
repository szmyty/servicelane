#!/bin/bash
set -eu

# stop all running containers
echo '####################################################'
echo 'Stopping running containers (if available)...'
echo '####################################################'
docker stop $(docker ps -aq)

# remove all stopped containers
echo '####################################################'
echo 'Removing containers ..'
echo '####################################################'
docker rm $(docker ps -aq)

# remove all stray volumes if any
echo '####################################################'
echo 'Removing docker container volumes (if any)'
echo '####################################################'
docker volume rm $(docker volume ls -q)

# docker system prune --all --volumes --force
