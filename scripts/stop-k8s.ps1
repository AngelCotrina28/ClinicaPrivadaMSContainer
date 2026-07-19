param(
    [string]$Namespace = "clinica-ms",
    [switch]$Destroy
)

$ErrorActionPreference = "Stop"

$namespaceResult = & kubectl get namespace $Namespace --ignore-not-found=true -o name 2>&1
if ($LASTEXITCODE -ne 0) {
    $detail = ($namespaceResult | Out-String).Trim()
    throw "No se pudo consultar el cluster Kubernetes. $detail"
}

if ([string]::IsNullOrWhiteSpace(($namespaceResult | Out-String))) {
    Write-Host "El namespace $Namespace no existe; no hay nada que detener."
    exit 0
}

if ($Destroy) {
    Write-Warning "Se eliminara el namespace $Namespace junto con sus PVC y datos locales."
    kubectl delete namespace $Namespace --ignore-not-found=true | Out-Host
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo eliminar el namespace $Namespace."
    }
    Write-Host "Kubernetes y sus datos locales fueron eliminados."
    exit 0
}

Write-Host "Deteniendo deployments sin borrar bases de datos..."
kubectl -n $Namespace scale deployment --all --replicas=0 | Out-Host
if ($LASTEXITCODE -ne 0) {
    throw "No se pudieron detener los deployments de $Namespace."
}

Write-Host "Kubernetes detenido. Los PVC y datos se conservaron."
Write-Host "Para eliminarlos expresamente usa: .\scripts\stop-k8s.ps1 -Destroy"
