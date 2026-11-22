import { useEffect, useMemo } from 'react'
import { asistente, coincideBusqueda } from '../asistente'
import { ProductCard } from '../components/ProductCard'
import type { Categoria, Producto } from '../types'
import { groupCategories } from '../utils/catalog'

type CatalogPageProps = {
  productos: Producto[]
  categorias: Categoria[]
  search: string
  onSearchChange: (search: string) => void
  activeCategory: number | null
  onCategoryChange: (categoryId: number | null) => void
  cargando: boolean
  error: string
  onRefresh: () => void
  onSelect: (producto: Producto) => void
}

export function CatalogPage({ productos, categorias, search, onSearchChange, activeCategory, onCategoryChange, cargando, error, onRefresh, onSelect }: CatalogPageProps) {
  const groups = groupCategories(categorias)
  const filteredProducts = useMemo(() => {
    return productos.filter((producto) => {
      const matchesSearch = coincideBusqueda(producto, search)
      const matchesCategory = activeCategory === null || producto.categoriaId === activeCategory
      return matchesSearch && matchesCategory
    })
  }, [activeCategory, productos, search])

  // Golondrina aprende las búsquedas cuando el usuario deja de escribir.
  useEffect(() => {
    const consulta = search.trim()
    if (consulta.length < 3) return
    const timer = setTimeout(() => asistente.registrar('buscar', { consulta }), 1500)
    return () => clearTimeout(timer)
  }, [search])

  return (
    <>
      <section className="hero-band">
        <div className="hero-grid" />
        <div className="hero-image" />
        <div className="container-fluid px-4 px-lg-5 position-relative">
          <div className="hero-copy reveal-up">
            <span className="eyebrow">Seleccion / 2026</span>
            <h1>Objetos que<br /><em>habitan</em> bien.</h1>
            <p>Una coleccion curada de puertas y ventanas para proyectos que buscan algo mas que funcionalidad.</p>
            <button className="btn btn-dark rounded-0 px-4 py-3" onClick={() => document.getElementById('catalogo-grid')?.scrollIntoView({ behavior: 'smooth' })}>
              Ver coleccion
            </button>
          </div>
        </div>
      </section>

      <section className="catalog-section container-fluid px-4 px-lg-5" id="catalogo-grid">
        <div className="section-heading">
          <div>
            <span className="eyebrow">La coleccion</span>
            <h2>Elegidos para quedarse.</h2>
          </div>
          <span className="result-count">{filteredProducts.length.toString().padStart(2, '0')} piezas</span>
        </div>

        <div className="toolbar row g-3 align-items-center">
          <div className="col-12 col-lg-5">
            <div className="search-box">
              <span>⌕</span>
              <input value={search} onChange={(event) => onSearchChange(event.target.value)} placeholder="Buscar por nombre o codigo..." />
            </div>
          </div>
          <div className="col-12 col-lg-7">
            <div className="category-filters">
              <button className={`filter-pill ${activeCategory === null ? 'selected' : ''}`} onClick={() => onCategoryChange(null)}>Todo</button>
              {groups.map((group) => (
                <div className="filter-group" key={group.name}>
                  <small>{group.name}</small>
                  <div>{group.items.map((category) => <button className={`filter-pill ${activeCategory === category.id ? 'selected' : ''}`} key={category.id} onClick={() => onCategoryChange(category.id)}>{category.nombre}</button>)}</div>
                </div>
              ))}
              <button className="refresh-button" data-golondrina="actualizar" onClick={onRefresh} title="Actualizar catalogo">↻</button>
            </div>
          </div>
        </div>

        {error && <div className="alert alert-warning mt-4">{error}</div>}
        {cargando ? <div className="loading-state">Cargando la coleccion...</div> : filteredProducts.length === 0 ? <EmptyCatalog /> : <div className="row g-4 product-grid">{filteredProducts.map((producto, index) => <ProductCard key={producto.id} producto={producto} index={index} onSelect={onSelect} />)}</div>}
      </section>
    </>
  )
}

function EmptyCatalog() {
  return <div className="empty-state"><h3>No encontramos esa pieza.</h3><p>Prueba con otra busqueda o explora todas las colecciones.</p></div>
}
