export type View = 'catalogo' | 'categorias' | 'admin' | 'login'

export type Producto = {
  id: number
  codigo: string
  nombre: string
  descripcion?: string
  precio: number
  imagenUrl?: string
  stock?: number
  stockId?: number
  categoriaId?: number
  catalogoId?: number
  tipoProducto?: 'PUERTA' | 'VENTANA'
  estado?: 'DISPONIBLE' | 'SIN_STOCK' | 'OCULTO'
  color?: string
  terminacion?: string
  ancho?: number
  altura?: number
  destacado: boolean
}

export type Categoria = {
  id: number
  nombre: string
  descripcion?: string
  imagen?: string
  catalogoId?: number
}

export type Catalogo = {
  id: number
  nombre: string
  descripcion?: string
}

export type Usuario = {
  id: number
  nombreUsuario: string
}

export type AdminProductForm = {
  codigo: string
  nombre: string
  stock: string
  tipoProducto: 'PUERTA' | 'VENTANA'
  estado: 'DISPONIBLE' | 'SIN_STOCK' | 'OCULTO'
  color: string
  terminacion: string
  ancho: string
  altura: string
  catalogoId: string
  categoriaId: string
  descripcion: string
}
