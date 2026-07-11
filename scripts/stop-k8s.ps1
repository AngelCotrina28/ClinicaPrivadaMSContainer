param(
    [string]$Namespace = "clinica-ms"
)

$ErrorActionPreference = "Stop"

Write-Host "Eliminando namespace $Namespace..."
kubectl delete namespace $Namespace --ignore-not-found=true | Out-Host
Write-Host "Kubernetes detenido para la arquitectura de microservicios."
