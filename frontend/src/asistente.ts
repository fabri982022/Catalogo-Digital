import { Golondrina, type Accion } from './golondrina'
import type { Categoria, Producto, Usuario, View } from './types'
import { productWhatsappUrl } from './utils/catalog'

/**
 * Instancia única del asistente + Golondrina para el catálogo.
 * App.tsx la monta y le pasa las acciones; los componentes registran lo que el
 * usuario hace a mano para que Golondrina aprenda sus costumbres.
 */
export const asistente = new Golondrina({
  app: 'catalogo-digital',
  acciones: [],
  colorPrimario: '#111111',
  // «configurar» en el asistente abre el panel de Golondrina 100% configurable.
  alConfigurar: () => window.dispatchEvent(new CustomEvent('golondrina:configurar')), // = EVENTO_CONFIGURAR
  saludo:
    '¡Hola! Soy Golondrina. Decime qué buscás («puertas de madera», «ventanas corredizas») y te lo muestro, ' +
    'o pedime consultar un producto por WhatsApp. Con el uso aprendo qué mirás y te lo dejo a mano.',
})

export type EstadoCatalogo = {
  view: View
  usuario: Usuario | null
  productos: Producto[]
  categorias: Categoria[]
  seleccionado: Producto | null
  navigate: (view: View, categoryId?: number) => void
  setSearch: (search: string) => void
  setSelectedProduct: (producto: Producto | null) => void
  editarProducto: (productoId: number) => void
  recargar: () => Promise<void> | void
  logout: () => void
}

export function coincideBusqueda(producto: Producto, busqueda: string) {
  return `${producto.nombre} ${producto.codigo} ${producto.descripcion ?? ''}`.toLowerCase().includes(busqueda.toLowerCase())
}

function irAGrilla() {
  setTimeout(() => document.getElementById('catalogo-grid')?.scrollIntoView({ behavior: 'smooth' }), 80)
}

function piezas(n: number) {
  return n === 1 ? '1 pieza' : `${n} piezas`
}

export function accionesDelCatalogo(estado: EstadoCatalogo): Accion[] {
  const { usuario, productos, categorias, seleccionado } = estado
  const nombresProductos = () => productos.map((p) => p.nombre)
  const nombresCategorias = () => categorias.map((c) => c.nombre)
  const buscarProducto = (nombre: string | undefined) => productos.find((p) => p.nombre === nombre)
  const esAdmin = () => !!usuario

  return [
    {
      id: 'buscar',
      titulo: 'Buscar «{consulta}»',
      descripcion: 'buscar piezas por nombre, código o descripción',
      frases: ['buscar {consulta}', 'tenés {consulta}', 'quiero ver {consulta}', 'busco {consulta}'],
      parametros: [{ nombre: 'consulta', libre: true, requerido: true, pregunta: '¿Qué busco?' }],
      ejecutar: ({ consulta }) => {
        estado.navigate('catalogo')
        estado.setSearch(consulta)
        irAGrilla()
        const n = productos.filter((p) => coincideBusqueda(p, consulta)).length
        return n ? `Encontré ${piezas(n)} para «${consulta}».` : `No encontré nada para «${consulta}». Probá con otra palabra o pedime «ver todo».`
      },
    },
    {
      id: 'filtrar-categoria',
      titulo: 'Ver {categoria}',
      descripcion: 'ver solo una categoría',
      frases: ['ver {categoria}', 'mostrar {categoria}', 'filtrar por {categoria}', 'categoría {categoria}'],
      parametros: [{ nombre: 'categoria', opciones: nombresCategorias, requerido: true, pregunta: '¿Qué categoría?' }],
      ejecutar: ({ categoria }) => {
        const cat = categorias.find((c) => c.nombre === categoria)
        if (!cat) return `No encontré la categoría ${categoria}.`
        estado.setSearch('')
        estado.navigate('catalogo', cat.id)
        irAGrilla()
        return `Te muestro ${categoria}: ${piezas(productos.filter((p) => p.categoriaId === cat.id).length)}.`
      },
    },
    {
      id: 'ver-todo',
      titulo: 'Ver todo el catálogo',
      frases: ['ver todo', 'mostrar todo el catálogo', 'quitar filtros', 'limpiar búsqueda'],
      ejecutar: () => {
        estado.setSearch('')
        estado.navigate('catalogo')
        irAGrilla()
        return `Todo el catálogo: ${piezas(productos.length)}.`
      },
    },
    {
      id: 'ver-producto',
      titulo: 'Abrir {producto}',
      descripcion: 'abrir la ficha de un producto',
      frases: ['abrir {producto}', 'ver {producto}', 'detalle de {producto}', 'mostrame {producto}'],
      parametros: [{ nombre: 'producto', opciones: nombresProductos, requerido: true, pregunta: '¿Qué producto?' }],
      ejecutar: ({ producto }) => {
        const p = buscarProducto(producto)
        if (!p) return `No encontré «${producto}».`
        estado.setSelectedProduct(p)
        return `Abrí ${p.nombre} (${p.codigo}). Hay ${p.stock ?? 0} disponibles.`
      },
    },
    {
      id: 'consultar-whatsapp',
      titulo: 'Consultar por WhatsApp',
      descripcion: 'consultar un producto por WhatsApp con el mensaje ya escrito',
      frases: ['consultar por whatsapp {producto}', 'preguntar por {producto}', 'hablar con un vendedor', 'quiero comprar {producto}'],
      // El producto no forma parte del hábito: lo que se aprende es "después de mirar, consulto".
      parametros: [{ nombre: 'producto', opciones: nombresProductos, memorizable: false }],
      descubrible: true,
      comoHacerlo: 'Está el botón «Consultar» en cada producto.',
      ejecutar: ({ producto }) => {
        const p = buscarProducto(producto) ?? seleccionado
        if (!p) return 'Decime sobre qué producto («consultar por WhatsApp la puerta…») o abrí uno y te lo envío.'
        window.open(productWhatsappUrl(p), '_blank', 'noopener')
        return `Abrí WhatsApp con la consulta por ${p.nombre}.`
      },
    },
    {
      id: 'ver-colecciones',
      titulo: 'Ver colecciones',
      frases: ['ver colecciones', 'qué categorías hay', 'mostrar categorías'],
      ejecutar: () => {
        estado.navigate('categorias')
        return `Hay ${categorias.length} colecciones: ${nombresCategorias().join(', ')}.`
      },
    },
    {
      id: 'ingresar',
      titulo: 'Ingresar a gestión',
      frases: ['iniciar sesión', 'ingresar a gestión', 'entrar como administrador', 'login'],
      disponible: () => !esAdmin(),
      ejecutar: () => {
        estado.navigate('login')
        return 'Te llevé al acceso. Ingresá tu usuario y contraseña.'
      },
    },
    {
      id: 'ir-gestion',
      titulo: 'Ir a gestión',
      frases: ['ir a gestión', 'abrir administración', 'panel de administración'],
      disponible: esAdmin,
      requisito: 'Primero ingresá a gestión (botón «Acceder» arriba a la derecha, o pedime «ingresar»).',
      ejecutar: () => {
        estado.navigate('admin')
        return 'Estás en el área de gestión.'
      },
    },
    {
      id: 'nuevo-producto',
      titulo: 'Cargar un producto nuevo',
      descripcion: 'cargar un producto nuevo',
      frases: ['nuevo producto', 'cargar producto', 'agregar producto', 'dar de alta un producto'],
      disponible: esAdmin,
      requisito: 'Primero ingresá a gestión (botón «Acceder» arriba a la derecha, o pedime «ingresar»).',
      descubrible: true,
      ejecutar: () => {
        estado.navigate('admin')
        setTimeout(() => {
          const campo = document.querySelector<HTMLInputElement>('.admin-form input')
          campo?.scrollIntoView({ behavior: 'smooth', block: 'center' })
          campo?.focus()
        }, 120)
        return 'Listo, el formulario está abierto. Empezá por el código.'
      },
    },
    {
      id: 'editar-producto',
      titulo: 'Modificar {producto}',
      descripcion: 'modificar un producto ya publicado',
      frases: ['editar {producto}', 'modificar {producto}', 'cambiar {producto}', 'actualizar {producto}'],
      parametros: [{ nombre: 'producto', opciones: nombresProductos, requerido: true, pregunta: '¿Qué producto querés modificar?' }],
      disponible: esAdmin,
      requisito: 'Primero ingresá a gestión (botón «Acceder» arriba a la derecha, o pedime «ingresar»).',
      ejecutar: ({ producto }) => {
        const p = buscarProducto(producto)
        if (!p) return `No encontré «${producto}».`
        estado.navigate('admin')
        estado.editarProducto(p.id)
        return `Cargué ${p.nombre} en el formulario para modificarlo.`
      },
    },
    {
      id: 'sin-stock',
      titulo: 'Productos sin stock',
      descripcion: 'preguntarme qué productos se quedaron sin stock',
      frases: ['productos sin stock', 'qué falta reponer', 'qué se agotó', 'stock bajo'],
      disponible: esAdmin,
      requisito: 'Primero ingresá a gestión (botón «Acceder» arriba a la derecha, o pedime «ingresar»).',
      descubrible: true,
      ejecutar: () => {
        const agotados = productos.filter((p) => (p.stock ?? 0) === 0 || p.estado === 'SIN_STOCK')
        const bajos = productos.filter((p) => (p.stock ?? 0) > 0 && (p.stock ?? 0) <= 3)
        if (!agotados.length && !bajos.length) return 'Todo tiene stock. 👌'
        const lineas = [
          ...agotados.map((p) => `• ${p.nombre} (${p.codigo}): sin stock`),
          ...bajos.map((p) => `• ${p.nombre} (${p.codigo}): quedan ${p.stock}`),
        ]
        return `Para reponer:\n${lineas.slice(0, 10).join('\n')}`
      },
    },
    {
      id: 'resumen',
      titulo: 'Resumen del catálogo',
      descripcion: 'pedirme un resumen del catálogo por categoría',
      frases: ['cuántos productos hay', 'resumen del catálogo', 'cuántas piezas hay'],
      ejecutar: () => {
        const lineas = categorias.map((c) => `• ${c.nombre}: ${productos.filter((p) => p.categoriaId === c.id).length}`)
        return `Hay ${piezas(productos.length)} en total.\n${lineas.join('\n')}`
      },
    },
    {
      id: 'actualizar',
      titulo: 'Actualizar el catálogo',
      frases: ['actualizar catálogo', 'recargar', 'refrescar'],
      ejecutar: async () => {
        await estado.recargar()
        return 'Catálogo actualizado.'
      },
    },
    {
      id: 'cerrar-sesion',
      titulo: 'Cerrar sesión',
      frases: ['cerrar sesión', 'salir de gestión', 'desconectarme'],
      disponible: esAdmin,
      requisito: 'Primero ingresá a gestión (botón «Acceder» arriba a la derecha, o pedime «ingresar»).',
      ejecutar: () => {
        estado.logout()
        return 'Cerraste sesión.'
      },
    },
  ]
}
