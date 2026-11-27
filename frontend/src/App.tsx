import { useEffect, useState } from 'react'
import { accionesDelCatalogo, asistente } from './asistente'
import './App.css'
import './responsive.css'
import { clearAuthentication, fetchCatalogData } from './api'
import { Navbar } from './components/Navbar'
import { GolondrinaBanner, GolondrinaConfig } from './components/GolondrinaConfig'
import { ProductModal } from './components/ProductModal'
import { AdminPage } from './pages/AdminPage'
import { CatalogPage } from './pages/CatalogPage'
import { CategoriesPage } from './pages/CategoriesPage'
import { LoginPage } from './pages/LoginPage'
import type { Catalogo, Categoria, Producto, Usuario, View } from './types'

const sessionKey = 'catalogo-session-user'

function readInitialView(): View {
  const path = window.location.hash.replace('#/', '')
  if (path === 'categorias' || path === 'admin' || path === 'login') return path
  return 'catalogo'
}

function readInitialCategory(): number | null {
  const category = new URLSearchParams(window.location.hash.split('?')[1] ?? '').get('categoria')
  return category ? Number(category) : null
}

function App() {
  const [view, setView] = useState<View>(readInitialView)
  const [activeCategory, setActiveCategory] = useState<number | null>(readInitialCategory)
  const [search, setSearch] = useState('')
  const [productToEdit, setProductToEdit] = useState<{ id: number; request: number } | null>(null)
  const [productos, setProductos] = useState<Producto[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [catalogos, setCatalogos] = useState<Catalogo[]>([])
  const [usuario, setUsuario] = useState<Usuario | null>(() => readStoredUser())
  const [selectedProduct, setSelectedProduct] = useState<Producto | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    void loadCatalog()
    const handleHistory = () => {
      setView(readInitialView())
      setActiveCategory(readInitialCategory())
    }
    window.addEventListener('popstate', handleHistory)
    return () => window.removeEventListener('popstate', handleHistory)
  }, [])

  // Golondrina: asistente + automatización que aprende de lo que hace cada usuario.
  useEffect(() => {
    asistente.montar()
    return () => asistente.destruir()
  }, [])

  // En cada render el asistente recibe las acciones con el estado actual.
  // (Sin dependencias a propósito: solo actualiza closures, no cambia estado.)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    asistente.definirAcciones(accionesDelCatalogo({
      view, usuario, productos, categorias, seleccionado: selectedProduct,
      navigate, setSearch, setSelectedProduct, logout,
      editarProducto: (id) => setProductToEdit({ id, request: Date.now() }),
      recargar: loadCatalog,
    }))
  })

  // Contexto: pantalla actual, producto abierto y categoría elegida.
  useEffect(() => {
    asistente.contexto(view, {
      producto: selectedProduct?.nombre ?? null,
      categoria: categorias.find((c) => c.id === activeCategory)?.nombre ?? null,
    })
  }, [view, selectedProduct, activeCategory, categorias])

  // Cada administrador tiene su propia memoria de hábitos.
  useEffect(() => {
    asistente.usuario(usuario?.nombreUsuario ?? null)
  }, [usuario?.nombreUsuario])

  function chooseCategory(categoryId: number | null) {
    const categoria = categorias.find((c) => c.id === categoryId)
    if (categoria) asistente.registrar('filtrar-categoria', { categoria: categoria.nombre })
    else asistente.registrar('ver-todo')
    setActiveCategory(categoryId)
  }

  function openProduct(producto: Producto) {
    asistente.registrar('ver-producto', { producto: producto.nombre })
    setSelectedProduct(producto)
  }

  async function loadCatalog() {
    setLoading(true)
    try {
      const data = await fetchCatalogData()
      setProductos(data.productos)
      setCategorias(data.categorias)
      setCatalogos(data.catalogos)
      setError('')
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo cargar el catalogo.')
    } finally {
      setLoading(false)
    }
  }

  function navigate(nextView: View, categoryId?: number) {
    const query = categoryId ? `?categoria=${categoryId}` : ''
    window.history.pushState({}, '', `#/${nextView}${query}`)
    setView(nextView)
    setActiveCategory(categoryId ?? null)
  }

  function login(nextUser: Usuario) {
    sessionStorage.setItem(sessionKey, JSON.stringify(nextUser))
    setUsuario(nextUser)
    navigate('admin')
  }

  function logout() {
    sessionStorage.removeItem(sessionKey)
    clearAuthentication()
    setUsuario(null)
    navigate('catalogo')
  }

  function renderPage() {
    if (view === 'login') {
      return <LoginPage onLogin={login} onBack={() => navigate('catalogo')} />
    }
    if (view === 'admin' && usuario) {
      return <AdminPage productos={productos} categorias={categorias} catalogos={catalogos} onCreated={loadCatalog} productToEdit={productToEdit} />
    }
    if (view === 'categorias') {
      return <CategoriesPage categorias={categorias} onSelect={(categoryId) => { chooseCategory(categoryId); navigate('catalogo', categoryId) }} />
    }
    return <CatalogPage productos={productos} categorias={categorias} search={search} onSearchChange={setSearch} activeCategory={activeCategory} onCategoryChange={chooseCategory} cargando={loading} error={error} onRefresh={loadCatalog} onSelect={openProduct} />
  }

  const page = renderPage()

  return (
    <main className="app-shell">
      <Navbar usuario={usuario} view={view} onNavigate={navigate} onLogout={logout} />
      {/* Golondrina 100% configurable: lo primero que ve el usuario hasta que la configura. */}
      <GolondrinaBanner />
      <GolondrinaConfig />
      {page}
      {selectedProduct && <ProductModal producto={selectedProduct} onClose={() => setSelectedProduct(null)} />}
      <footer className="app-footer"><span>CATALOGO DIGITAL</span><span>Puertas, ventanas y espacios con caracter.</span><span>Integrantes: {import.meta.env.VITE_TEAM_MEMBERS ?? 'Equipo de desarrollo'}</span></footer>
    </main>
  )
}

function readStoredUser(): Usuario | null {
  const stored = sessionStorage.getItem(sessionKey)
  if (!stored) return null
  try {
    return JSON.parse(stored) as Usuario
  } catch {
    sessionStorage.removeItem(sessionKey)
    return null
  }
}

export default App
