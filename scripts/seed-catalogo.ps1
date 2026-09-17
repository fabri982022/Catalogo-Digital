param(
    [string]$ApiUrl = 'http://localhost:8080/api',
    [string]$AdminUser = 'admin',
    [string]$AdminPassword = 'admin1234'
)

$ErrorActionPreference = 'Stop'

function Get-AuthHeader {
    $raw = [Text.Encoding]::ASCII.GetBytes($AdminUser + ':' + $AdminPassword)
    return @{ Authorization = "Basic $([Convert]::ToBase64String($raw))" }
}

function Invoke-Api([string]$Path, [string]$Method = 'Get', [object]$Body = $null) {
    $params = @{ Uri = "$ApiUrl$Path"; Method = $Method; Headers = (Get-AuthHeader) }
    if ($null -ne $Body) {
        $params.ContentType = 'application/json'
        $params.Body = $Body | ConvertTo-Json -Depth 8
    }
    return Invoke-RestMethod @params
}

function Find-Or-CreateCatalog([string]$Name, [string]$Description) {
    $catalog = @(Invoke-Api '/catalogos') | Where-Object { $_.nombre -eq $Name } | Select-Object -First 1
    if ($null -eq $catalog) { $catalog = Invoke-Api '/catalogos' 'Post' @{ nombre = $Name; descripcion = $Description } }
    return $catalog
}

function Find-Or-CreateCategory([string]$Name, [string]$Description, [long]$CatalogId) {
    $category = @(Invoke-Api '/categorias') | Where-Object { $_.nombre -eq $Name -and $_.catalogoId -eq $CatalogId } | Select-Object -First 1
    if ($null -eq $category) { $category = Invoke-Api '/categorias' 'Post' @{ nombre = $Name; descripcion = $Description; catalogoId = $CatalogId; activa = $true } }
    return $category
}

function Find-Or-CreateProduct([hashtable]$Data) {
    $product = @(Invoke-Api '/productos') | Where-Object { $_.codigo -eq $Data.codigo } | Select-Object -First 1
    if ($null -eq $product) { $product = Invoke-Api '/productos' 'Post' $Data }
    return $product
}

$doorCatalog = Find-Or-CreateCatalog 'Catalogo Digital - Puertas' 'Puertas de acceso e interiores.'
$windowCatalog = Find-Or-CreateCatalog 'Catalogo Digital - Ventanas' 'Ventanas de aluminio y vidrio.'
$doorCategory = Find-Or-CreateCategory 'Puertas' 'Soluciones de acceso e interiores.' $doorCatalog.id
$windowCategory = Find-Or-CreateCategory 'Ventanas' 'Soluciones de iluminación y ventilación.' $windowCatalog.id
$existingStocks = @(Invoke-Api '/stocks')

for ($index = 1; $index -le 5; $index++) {
    $code = 'PUERTA-{0:D2}' -f $index
    $product = Find-Or-CreateProduct @{ codigo = $code; nombre = "Puerta de diseño $index"; descripcion = 'Producto de la familia puertas.'; precio = 0; catalogoId = $doorCatalog.id; categoriaId = $doorCategory.id; tipoProducto = 'PUERTA'; destacado = ($index -le 2) }
    if (-not ($existingStocks | Where-Object { $_.productoId -eq $product.id })) { Invoke-Api '/stocks' 'Post' @{ productoId = $product.id; cantidadDisponible = 10; cantidadMinima = 1; permiteBajoPedido = $true } | Out-Null }
}

for ($index = 1; $index -le 5; $index++) {
    $code = 'VENTANA-{0:D2}' -f $index
    $product = Find-Or-CreateProduct @{ codigo = $code; nombre = "Ventana de diseño $index"; descripcion = 'Producto de la familia ventanas.'; precio = 0; catalogoId = $windowCatalog.id; categoriaId = $windowCategory.id; tipoProducto = 'VENTANA'; destacado = ($index -le 2) }
    if (-not ($existingStocks | Where-Object { $_.productoId -eq $product.id })) { Invoke-Api '/stocks' 'Post' @{ productoId = $product.id; cantidadDisponible = 10; cantidadMinima = 1; permiteBajoPedido = $true } | Out-Null }
}

Write-Host 'Seed completado: admin, catalogos, categorias, productos y stock.'
