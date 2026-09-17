import type { Producto } from '../types'
import { productWhatsappUrl } from '../utils/catalog'

type ProductModalProps = {
  producto: Producto
  onClose: () => void
}

export function ProductModal({ producto, onClose }: ProductModalProps) {
  return (
    <div className="product-overlay" role="dialog" aria-modal="true" onClick={onClose}>
      <div className="product-dialog" onClick={(event) => event.stopPropagation()}>
        <button className="close-dialog" onClick={onClose} aria-label="Cerrar">×</button>
        <div className="dialog-image">
          {producto.imagenUrl ? <img src={producto.imagenUrl} alt={producto.nombre} /> : <span>{producto.codigo}</span>}
        </div>
        <div className="dialog-content">
          <span className="product-code">{producto.codigo}</span>
          <h2>{producto.nombre}</h2>
          <p>{producto.descripcion || 'Una pieza pensada para acompañar proyectos duraderos.'}</p>
          <div className="dialog-stock">{producto.stock ?? 0} unidades disponibles</div>
          <a className="whatsapp-button" href={productWhatsappUrl(producto)} target="_blank" rel="noreferrer">Consultar por WhatsApp <span>↗</span></a>
        </div>
      </div>
    </div>
  )
}
