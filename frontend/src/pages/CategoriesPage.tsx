import type { Categoria } from '../types'
import { categoryFamily, groupCategories } from '../utils/catalog'

type CategoriesPageProps = {
  categorias: Categoria[]
  onSelect: (id: number) => void
}

export function CategoriesPage({ categorias, onSelect }: CategoriesPageProps) {
  return (
    <section className="subpage container-fluid px-4 px-lg-5">
      <div className="subpage-intro">
        <span className="eyebrow">Explora por universo</span>
        <h1>Las colecciones<br /><em>de la casa.</em></h1>
        <p>Elige una familia de productos para encontrar la pieza adecuada.</p>
      </div>

      {groupCategories(categorias).map((group) => (
        <div className="category-family" key={group.name}>
          <div className="family-heading">
            <span className="eyebrow">Familia</span>
            <h2>{group.name}</h2>
          </div>
          <div className="row g-3 category-grid">
            {group.items.map((category, index) => (
              <div className="col-12 col-md-6" key={category.id}>
                <button className="category-card" onClick={() => onSelect(category.id)}>
                  {category.imagen ? <img src={category.imagen} alt="" /> : <span className="category-number">0{index + 1}</span>}
                  <div>
                    <span className="product-code">{categoryFamily(category)}</span>
                    <h2>{category.nombre}</h2>
                    <p>{category.descripcion || 'Materiales seleccionados para transformar tu espacio.'}</p>
                  </div>
                  <span className="category-arrow">↗</span>
                </button>
              </div>
            ))}
          </div>
        </div>
      ))}

      {categorias.length === 0 && <div className="empty-state">Aun no hay colecciones disponibles.</div>}
    </section>
  )
}
