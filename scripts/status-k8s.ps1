param(
    [string]$Namespace = "clinica-ms"
)

$ErrorActionPreference = "Stop"

Write-Host "Pods:"
kubectl -n $Namespace get pods -o wide

Write-Host ""
Write-Host "Deployments:"
kubectl -n $Namespace get deployments

Write-Host ""
Write-Host "Services:"
kubectl -n $Namespace get services

Write-Host ""
Write-Host "Persistent volumes:"
kubectl -n $Namespace get pvc

Write-Host ""
Write-Host "Endpoints listos:"
kubectl -n $Namespace get endpoints

Write-Host ""
Write-Host "Eventos recientes:"
kubectl -n $Namespace get events --sort-by=.lastTimestamp | Select-Object -Last 25
