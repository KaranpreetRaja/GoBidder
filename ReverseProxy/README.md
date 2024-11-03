# Reverse Proxy Service

The reverse proxy service is a containerized instance of nginx that is used for
load balancing, caching, and authorization. When run with Docker Compose (from
the parent directory, see [../README.md](../README.md)) the reverse proxy
container will spin up and automatically start proxying requests.
