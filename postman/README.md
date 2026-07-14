# Postman verification

Import the collection and one environment:

- `Clinica-Privada-Microservicios.postman_collection.json`
- `Clinica-Local.postman_environment.json` for Docker Compose or Kubernetes.
- `Clinica-Render.postman_environment.json` for the cloud deployment.

For Render, complete the `password` environment variable with the Auth Service
administrator password. Run the folders in numeric order with Collection Runner.
The login request stores the JWT and the creation requests store all generated
identifiers automatically.

The `/api/ms` prefix explicitly selects a business microservice. Legacy frontend
paths continue through the same Gateway and can fall back to the original backend
while modules are migrated incrementally.
