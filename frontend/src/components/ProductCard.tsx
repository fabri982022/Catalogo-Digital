import type { Producto } from '../types'
import { productWhatsappUrl } from '../utils/catalog'

type ProductCardProps = {
  producto: Producto
  index: number
  onSelect: (producto: Producto) => void
}

export function ProductCard({ producto, index, onSelect }: ProductCardProps) {
  return (
    <article className="col-12 col-md-6 col-xl-4 reveal-up" style={{ animationDelay: `${index * 70}ms` }}>
      <div
        className="product-card text-start"
        role="button"
        tabIndex={0}
        onClick={() => onSelect(producto)}
        onKeyDown={(event) => {
          if (event.key === 'Enter' || event.key === ' ') onSelect(producto)
        }}
      >
        <div className="product-visual">
          {producto.imagenUrl ? (
            <img src={producto.imagenUrl} alt={producto.nombre} />
          ) : (
            <div className="visual-placeholder">
              <span>{producto.codigo.slice(0, 2)}</span>
              <small>sin imagen</small>
            </div>
          )}
          {producto.destacado && <span className="featured-tag">Destacado</span>}
          <span className="open-card">↗</span>
        </div>

        <div className="product-meta">
          <div>
            <span className="product-code">{producto.codigo}</span>
            <h3>{producto.nombre}</h3>
          </div>
          <strong>{producto.stock ?? 0} disponibles</strong>
        </div>

        <div className="product-footer">
          <span>{producto.descripcion || 'Diseño contemporaneo para espacios con intencion.'}</span>
        </div>

        <a
          className="whatsapp-button"
          href={productWhatsappUrl(producto)}
          target="_blank"
          rel="noreferrer"
          onClick={(event) => event.stopPropagation()}
        >
          Consultar producto <span>↗</span>
        </a>
      </div>
    </article>
  )
}
