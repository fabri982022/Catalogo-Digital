import type { Categoria, Producto } from '../types'

const whatsappNumber = '543884654082'

export function categoryFamily(category: Categoria) {
  const name = category.nombre.toLowerCase()
  if (name.includes('ventan')) return 'Ventanas'
  if (name.includes('puert')) return 'Puertas'
  return 'Otros productos'
}

export function groupCategories(categories: Categoria[]) {
  return ['Puertas', 'Ventanas', 'Otros productos']
    .map((name) => ({ name, items: categories.filter((category) => categoryFamily(category) === name) }))
    .filter((group) => group.items.length > 0)
}

export function productWhatsappUrl(producto: Producto) {
  const message = `Hola, quiero consultar por ${producto.nombre} (${producto.codigo}).`
  return `https://wa.me/${whatsappNumber}?text=${encodeURIComponent(message)}`
}
