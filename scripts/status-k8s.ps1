param(
    [string]$Namespace = "clinica-ms"
)

$ErrorActionPreference = "Stop"

function Invoke-KubectlStatus {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Description,
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & kubectl @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo consultar $Description en el namespace $Namespace."
    }
}

Write-Host "Pods:"
Invoke-KubectlStatus "los pods" @("-n", $Namespace, "get", "pods", "-o", "wide")

Write-Host ""
Write-Host "Deployments:"
Invoke-KubectlStatus "los deployments" @("-n", $Namespace, "get", "deployments")

Write-Host ""
Write-Host "Services:"
Invoke-KubectlStatus "los servicios" @("-n", $Namespace, "get", "services")

Write-Host ""
Write-Host "Persistent volumes:"
Invoke-KubectlStatus "los PVC" @("-n", $Namespace, "get", "pvc")

Write-Host ""
Write-Host "Endpoints listos:"
Invoke-KubectlStatus "los endpoints" @("-n", $Namespace, "get", "endpoints")

Write-Host ""
Write-Host "Eventos recientes:"
$events = & kubectl -n $Namespace get events --sort-by=.lastTimestamp
if ($LASTEXITCODE -ne 0) {
    throw "No se pudieron consultar los eventos del namespace $Namespace."
}
$events | Select-Object -Last 25
