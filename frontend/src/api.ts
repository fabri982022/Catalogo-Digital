import type { AdminProductForm, Catalogo, Categoria, Producto, Usuario } from './types'

const apiUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'
const authStorageKey = 'catalogo-admin-auth'

function adminHeaders(): Record<string, string> {
  const authorization = sessionStorage.getItem(authStorageKey)
  return authorization ? { Authorization: authorization } : {}
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    throw new Error(`La solicitud fallo (${response.status}).`)
  }

  return response.json() as Promise<T>
}

export async function fetchCatalogData() {
  const [productosResponse, categoriasResponse, catalogosResponse] = await Promise.all([
    fetch(`${apiUrl}/productos`),
    fetch(`${apiUrl}/categorias`),
    fetch(`${apiUrl}/catalogos`),
  ])

  return {
    productos: await parseResponse<Producto[]>(productosResponse),
    categorias: await parseResponse<Categoria[]>(categoriasResponse),
    catalogos: await parseResponse<Catalogo[]>(catalogosResponse),
  }
}

export async function fetchProduct(productId: number) {
  const response = await fetch(`${apiUrl}/productos/${productId}`)
  return parseResponse<Producto>(response)
}

export async function authenticate(username: string, password: string): Promise<Usuario> {
  const credentials = `${username}:${password}`
  const authorization = `Basic ${btoa(credentials)}`
  const response = await fetch(`${apiUrl}/auth/session`, {
    headers: { Authorization: authorization },
  })
  const user = await parseResponse<Usuario>(response)
  sessionStorage.setItem(authStorageKey, authorization)
  return user
}

export function clearAuthentication() {
  sessionStorage.removeItem(authStorageKey)
}

export async function createProduct(form: AdminProductForm) {
  const catalogoId = Number(form.catalogoId)
  const categoriaId = Number(form.categoriaId)
  const stock = Number(form.stock)
  if (!Number.isInteger(catalogoId) || catalogoId <= 0) throw new Error('Selecciona un catalogo valido.')
  if (!Number.isInteger(categoriaId) || categoriaId <= 0) throw new Error('Selecciona una categoria valida.')
  if (!Number.isInteger(stock) || stock < 0) throw new Error('La disponibilidad debe ser un entero mayor o igual a 0.')

  const response = await fetch(`${apiUrl}/productos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...adminHeaders(),
    },
    body: JSON.stringify({
      codigo: form.codigo,
      nombre: form.nombre,
      descripcion: form.descripcion,
      color: form.color,
      terminacion: form.terminacion,
      ancho: Number(form.ancho),
      altura: Number(form.altura),
      estado: form.estado,
      precio: 0,
      catalogoId,
      categoriaId,
      tipoProducto: form.tipoProducto,
      destacado: false,
    }),
  })

  const producto = await parseResponse<Producto>(response)
  const stockResponse = await fetch(`${apiUrl}/stocks`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...adminHeaders(),
    },
    body: JSON.stringify({
      productoId: producto.id,
      cantidadDisponible: stock,
      permiteBajoPedido: false,
    }),
  })

  await parseResponse(stockResponse)
  return producto
}

export async function updateProduct(productId: number, form: AdminProductForm) {
  const catalogoId = Number(form.catalogoId)
  const categoriaId = Number(form.categoriaId)
  const stock = Number(form.stock)
  if (!Number.isInteger(catalogoId) || catalogoId <= 0) throw new Error('Selecciona un catalogo valido.')
  if (!Number.isInteger(categoriaId) || categoriaId <= 0) throw new Error('Selecciona una categoria valida.')
  if (!Number.isInteger(stock) || stock < 0) throw new Error('La disponibilidad debe ser un entero mayor o igual a 0.')

  const response = await fetch(`${apiUrl}/productos/${productId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...adminHeaders() },
    body: JSON.stringify({
      codigo: form.codigo,
      nombre: form.nombre,
      descripcion: form.descripcion,
      color: form.color,
      terminacion: form.terminacion,
      ancho: Number(form.ancho),
      altura: Number(form.altura),
      estado: form.estado,
      precio: 0,
      catalogoId,
      categoriaId,
      tipoProducto: form.tipoProducto,
      destacado: false,
      stock,
    }),
  })

  return parseResponse<Producto>(response)
}

export async function deleteProduct(productId: number) {
  const response = await fetch(`${apiUrl}/productos/${productId}`, {
    method: 'DELETE',
    headers: adminHeaders(),
  })

  if (!response.ok) {
    throw new Error(`La solicitud fallo (${response.status}).`)
  }
}

export async function uploadProductImage(productId: number, image: File, order: number) {
  const body = new FormData()
  body.append('file', image)
  body.append('productoId', productId.toString())
  body.append('nombre', image.name)
  body.append('esPrincipal', String(order === 0))
  body.append('orden', order.toString())

  const response = await fetch(`${apiUrl}/imagenes/upload`, {
    method: 'POST',
    headers: adminHeaders(),
    body,
  })

  return parseResponse(response)
}

export async function createCatalog(name: string, description: string) {
  const response = await fetch(`${apiUrl}/catalogos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...adminHeaders(),
    },
    body: JSON.stringify({ nombre: name, descripcion: description }),
  })

  return parseResponse<Catalogo>(response)
}
