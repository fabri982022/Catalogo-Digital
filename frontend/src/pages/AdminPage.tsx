import { useEffect, useState } from 'react'
import { createCatalog, createProduct, deleteProduct, updateProduct as updateProductRequest, uploadProductImage } from '../api'
import type { AdminProductForm, Catalogo, Categoria, Producto } from '../types'
import '../admin.css'

type AdminPageProps = {
  productos: Producto[]
  categorias: Categoria[]
  catalogos: Catalogo[]
  onCreated: () => Promise<void>
}

const emptyProduct: AdminProductForm = {
  codigo: '',
  nombre: '',
  stock: '',
  tipoProducto: 'PUERTA',
  estado: 'DISPONIBLE',
  color: '',
  terminacion: '',
  ancho: '',
  altura: '',
  catalogoId: '',
  categoriaId: '',
  descripcion: '',
}

export function AdminPage({ productos, categorias, catalogos, onCreated }: Readonly<AdminPageProps>) {
  const [productForm, setProductForm] = useState<AdminProductForm>({
    ...emptyProduct,
    catalogoId: catalogos[0]?.id.toString() ?? '',
    categoriaId: categorias[0]?.id.toString() ?? '',
  })
  const [catalogForm, setCatalogForm] = useState({ nombre: '', descripcion: '' })
  const [images, setImages] = useState<File[]>([])
  const [imageInputKey, setImageInputKey] = useState(0)
  const [status, setStatus] = useState('')
  const [saving, setSaving] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Producto | null>(null)
  const [pendingAction, setPendingAction] = useState<'edit' | 'delete' | null>(null)
  const [pendingProduct, setPendingProduct] = useState<Producto | null>(null)
  const [completedProduct, setCompletedProduct] = useState<{ action: 'created' | 'updated'; product: Producto } | null>(null)

  useEffect(() => {
    setProductForm((current) => ({
      ...current,
      catalogoId: current.catalogoId && catalogos.some((catalogo) => catalogo.id.toString() === current.catalogoId)
        ? current.catalogoId
        : catalogos[0]?.id.toString() ?? '',
      categoriaId: current.categoriaId && categorias.some((categoria) => categoria.id.toString() === current.categoriaId)
        ? current.categoriaId
        : categorias[0]?.id.toString() ?? '',
    }))
  }, [catalogos, categorias])

  async function submitProduct(event: { preventDefault: () => void }) {
    event.preventDefault()
    const validName = /^\p{L}+(?: \p{L}+)*$/u
    if (!validName.test(productForm.nombre.trim())) {
      setStatus('El nombre solo puede contener letras, espacios y guiones.')
      return
    }
    if (!editingProduct && images.length === 0) {
      setStatus('Selecciona al menos una imagen antes de guardar el producto.')
      return
    }
    if (editingProduct && !editingProduct.imagenUrl && images.length === 0) {
      setStatus('El producto debe tener al menos una imagen.')
      return
    }
    setSaving(true)
    setStatus('')

    try {
      const wasEditing = Boolean(editingProduct)
      const product = editingProduct
        ? await updateProductRequest(editingProduct.id, productForm, editingProduct.stockId)
        : await createProduct(productForm)
      for (const [index, image] of images.entries()) {
        await uploadProductImage(product.id, image, index)
      }
      const persistedProduct: Producto = {
        ...product,
        stock: Number(productForm.stock),
        stockId: product.stockId ?? editingProduct?.stockId,
        imagenUrl: product.imagenUrl ?? editingProduct?.imagenUrl,
      }
      setStatus('')
      setCompletedProduct({ action: wasEditing ? 'updated' : 'created', product: persistedProduct })
      setProductForm({ ...emptyProduct, catalogoId: productForm.catalogoId, categoriaId: productForm.categoriaId })
      setImages([])
      setImageInputKey((current) => current + 1)
      setEditingProduct(null)
      await onCreated()
    } catch (requestError) {
      setStatus(requestError instanceof Error ? requestError.message : 'No se pudo guardar el producto.')
    } finally {
      setSaving(false)
    }
  }

  async function submitCatalog(event: { preventDefault: () => void }) {
    event.preventDefault()
    const validName = /^\p{L}+(?: \p{L}+)*$/u
    if (!validName.test(catalogForm.nombre.trim())) {
      setStatus('El nombre del catalogo solo puede contener letras, espacios y guiones.')
      return
    }
    setSaving(true)
    setStatus('')

    try {
      await createCatalog(catalogForm.nombre, catalogForm.descripcion)
      setCatalogForm({ nombre: '', descripcion: '' })
      setStatus('Catalogo creado correctamente.')
      await onCreated()
    } catch (requestError) {
      setStatus(requestError instanceof Error ? requestError.message : 'No se pudo crear el catalogo.')
    } finally {
      setSaving(false)
    }
  }

  function updateProduct<K extends keyof AdminProductForm>(field: K, value: AdminProductForm[K]) {
    setProductForm((current) => ({ ...current, [field]: value }))
  }

  function startEditing(producto: Producto) {
    setEditingProduct(producto)
    setProductForm({
      codigo: producto.codigo,
      nombre: producto.nombre,
      stock: String(producto.stock ?? 0),
      tipoProducto: producto.tipoProducto ?? 'PUERTA',
      estado: producto.estado ?? 'DISPONIBLE',
      color: producto.color ?? '',
      terminacion: producto.terminacion ?? '',
      ancho: producto.ancho?.toString() ?? '',
      altura: producto.altura?.toString() ?? '',
      catalogoId: producto.catalogoId?.toString() ?? '',
      categoriaId: producto.categoriaId?.toString() ?? '',
      descripcion: producto.descripcion ?? '',
    })
    setImages([])
    setImageInputKey((current) => current + 1)
    setStatus('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function requestProductAction(action: 'edit' | 'delete', producto: Producto) {
    setPendingAction(action)
    setPendingProduct(producto)
  }

  async function confirmProductAction() {
    if (!pendingProduct || !pendingAction) return
    const product = pendingProduct
    const action = pendingAction
    setPendingAction(null)
    setPendingProduct(null)
    if (action === 'edit') {
      startEditing(product)
      return
    }

    setSaving(true)
    try {
      await deleteProduct(product.id)
      if (editingProduct?.id === product.id) cancelEditing()
      setStatus('Producto eliminado correctamente.')
      await onCreated()
    } catch (requestError) {
      setStatus(requestError instanceof Error ? requestError.message : 'No se pudo eliminar el producto.')
    } finally {
      setSaving(false)
    }
  }

  function cancelEditing() {
    setEditingProduct(null)
    setProductForm({ ...emptyProduct, catalogoId: catalogos[0]?.id.toString() ?? '', categoriaId: categorias[0]?.id.toString() ?? '' })
    setImages([])
    setImageInputKey((current) => current + 1)
  }

  let imageHint = 'La imagen es obligatoria. La primera sera la principal.'
  if (images.length > 0) imageHint = `${images.length} imagenes seleccionadas: ${images.map((image) => image.name).join(', ')}`
  else if (editingProduct?.imagenUrl) imageHint = 'Conserva la imagen actual o selecciona nuevas imagenes.'
  let productButtonLabel = 'Guardar producto'
  if (saving) productButtonLabel = 'Guardando...'
  else if (editingProduct) productButtonLabel = 'Modificar producto'

  return (
    <section className="admin-page container-fluid px-4 px-lg-5">
      <header className="admin-header">
        <div>
          <span className="eyebrow">Area de gestion</span>
          <h1>Tu coleccion,<br /><em>bajo control.</em></h1>
          <span className="admin-product-count">{productos.length} productos publicados</span>
        </div>
      </header>

      <div className="row g-5">
        <div className="col-12 col-lg-7">
          <div className="admin-form-wrap">
            <span className="eyebrow">{editingProduct ? 'Editar pieza' : 'Nueva pieza'}</span>
            <h2>{editingProduct ? 'Modificar producto' : 'Agregar producto'}</h2>
            <form onSubmit={submitProduct} className="admin-form row g-3">
              <div className="col-md-6"><label>Codigo<input required minLength={3} placeholder="P-PUERTA-001" value={productForm.codigo} onChange={(event) => updateProduct('codigo', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Nombre<input required placeholder="Puerta principal moderna" value={productForm.nombre} onChange={(event) => updateProduct('nombre', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Tipo<select required value={productForm.tipoProducto} onChange={(event) => updateProduct('tipoProducto', event.target.value as AdminProductForm['tipoProducto'])}><option value="PUERTA">Puerta</option><option value="VENTANA">Ventana</option></select></label></div>
              <div className="col-md-6"><label>Estado<select required value={productForm.estado} onChange={(event) => updateProduct('estado', event.target.value as AdminProductForm['estado'])}><option value="DISPONIBLE">Disponible</option><option value="SIN_STOCK">Sin stock</option><option value="OCULTO">Oculto</option></select></label></div>
              <div className="col-md-6"><label>Disponibilidad<input required type="number" min="0" step="1" placeholder="15" value={productForm.stock} onChange={(event) => updateProduct('stock', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Categoria<select required disabled={categorias.length === 0} value={productForm.categoriaId} onChange={(event) => updateProduct('categoriaId', event.target.value)}><option value="">Selecciona una categoria</option>{categorias.map((categoria) => <option key={categoria.id} value={categoria.id}>{categoria.nombre}</option>)}</select></label></div>
              <div className="col-12"><label>Catalogo<select required disabled={catalogos.length === 0} value={productForm.catalogoId} onChange={(event) => updateProduct('catalogoId', event.target.value)}><option value="">Selecciona un catalogo</option>{catalogos.map((catalogo) => <option key={catalogo.id} value={catalogo.id}>{catalogo.nombre}</option>)}</select></label></div>
              <div className="col-md-6"><label>Color<input placeholder="Negro" value={productForm.color} onChange={(event) => updateProduct('color', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Terminacion<input placeholder="Mate" value={productForm.terminacion} onChange={(event) => updateProduct('terminacion', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Ancho (m)<input type="number" min="0" step="0.01" placeholder="0.90" value={productForm.ancho} onChange={(event) => updateProduct('ancho', event.target.value)} /></label></div>
              <div className="col-md-6"><label>Altura (m)<input type="number" min="0" step="0.01" placeholder="2.10" value={productForm.altura} onChange={(event) => updateProduct('altura', event.target.value)} /></label></div>
              <div className="col-12"><label>Descripcion<textarea rows={3} placeholder="Describe materiales, uso y caracteristicas del producto." value={productForm.descripcion} onChange={(event) => updateProduct('descripcion', event.target.value)} /></label></div>
              <div className="col-12">
                <label className="image-upload">
                  Imagenes del producto <span className="required-mark">*</span>
                  <input
                    key={imageInputKey}
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    multiple
                    onChange={(event) => setImages(Array.from(event.target.files ?? []))}
                  />
                  <small>{imageHint}</small>
                </label>
              </div>
              <div className="col-12 admin-actions"><button type="submit" disabled={saving || catalogos.length === 0 || categorias.length === 0} className="btn btn-dark rounded-0 px-4 py-3">{productButtonLabel}</button>{editingProduct && <button type="button" onClick={cancelEditing} className="btn btn-outline-dark rounded-0 px-4 py-3">Cancelar</button>}</div>
            </form>
          </div>
        </div>

        <div className="col-12 col-lg-5">
          <div className="admin-form-wrap">
            <span className="eyebrow">Nueva coleccion</span>
            <h2>Agregar catalogo</h2>
            <form onSubmit={submitCatalog} className="admin-form">
              <label>Nombre<input required minLength={3} placeholder="Catalogo Principal" value={catalogForm.nombre} onChange={(event) => setCatalogForm({ ...catalogForm, nombre: event.target.value })} /></label>
              <label>Descripcion<textarea rows={3} placeholder="Catalogo de puertas y ventanas" value={catalogForm.descripcion} onChange={(event) => setCatalogForm({ ...catalogForm, descripcion: event.target.value })} /></label>
              <button type="submit" disabled={saving} className="btn btn-dark rounded-0 px-4 py-3">Guardar catalogo</button>
            </form>
          </div>
        </div>
      </div>

      <section className="admin-products">
        <div className="admin-products-heading"><div><span className="eyebrow">Inventario</span><h2>Productos publicados</h2></div><span>{productos.length} piezas</span></div>
        <div className="row g-3">
          {productos.map((producto) => <article className="col-12 col-md-6 col-xl-4" key={producto.id}><div className="admin-product-card">{producto.imagenUrl ? <img src={producto.imagenUrl} alt={producto.nombre} /> : <div className="admin-product-placeholder">Sin imagen</div>}<div className="admin-product-info"><span>{producto.codigo}</span><h3>{producto.nombre}</h3><small>{producto.stock ?? 0} disponibles</small><div className="admin-card-actions"><button type="button" className="modify-button" onClick={() => requestProductAction('edit', producto)}>Modificar <span>↗</span></button><button type="button" className="delete-button" onClick={() => requestProductAction('delete', producto)}>Eliminar</button></div></div></div></article>)}
        </div>
      </section>

      {status && <p className="form-status admin-status">{status}</p>}

      {pendingAction && pendingProduct && <AdminProductDialog title={pendingAction === 'edit' ? '¿Deseas modificar este producto?' : '¿Deseas eliminar este producto?'} producto={pendingProduct} confirmLabel={pendingAction === 'edit' ? 'Modificar producto' : 'Eliminar producto'} danger={pendingAction === 'delete'} onClose={() => { setPendingAction(null); setPendingProduct(null) }} onConfirm={() => void confirmProductAction()} />}
      {completedProduct && <AdminProductDialog title={completedProduct.action === 'created' ? 'Producto creado exitosamente' : 'Producto modificado exitosamente'} producto={completedProduct.product} confirmLabel="Cerrar" onClose={() => setCompletedProduct(null)} onConfirm={() => setCompletedProduct(null)} />}
    </section>
  )
}

type AdminProductDialogProps = {
  title: string
  producto: Producto
  confirmLabel: string
  danger?: boolean
  onClose: () => void
  onConfirm: () => void
}

function AdminProductDialog({ title, producto, confirmLabel, danger = false, onClose, onConfirm }: Readonly<AdminProductDialogProps>) {
  return (
    <dialog open className="admin-dialog-overlay">
      <div className="admin-dialog">
        <button type="button" className="admin-dialog-close" onClick={onClose} aria-label="Cerrar">×</button>
        <span className="eyebrow">Detalle del producto</span>
        <h2>{title}</h2>
        <div className="admin-dialog-product">
          {producto.imagenUrl ? <img src={producto.imagenUrl} alt={producto.nombre} /> : <div className="admin-product-placeholder">Sin imagen</div>}
          <div className="admin-dialog-details">
            <strong>{producto.nombre}</strong>
            <span><b>Código:</b> {producto.codigo}</span>
            <span><b>Tipo:</b> {producto.tipoProducto ?? 'Sin especificar'}</span>
            <span><b>Estado:</b> {producto.estado ?? 'Sin especificar'}</span>
            <span><b>Disponibilidad:</b> {producto.stock ?? 0} unidades</span>
            <span><b>Color:</b> {producto.color || 'Sin especificar'}</span>
            <span><b>Terminación:</b> {producto.terminacion || 'Sin especificar'}</span>
            <span><b>Medidas:</b> {producto.ancho ?? '-'} m x {producto.altura ?? '-'} m</span>
            <span><b>Descripción:</b> {producto.descripcion || 'Sin descripción'}</span>
          </div>
        </div>
        <div className="admin-dialog-actions"><button type="button" className={danger ? 'btn btn-danger rounded-0 px-4 py-3' : 'btn btn-dark rounded-0 px-4 py-3'} onClick={onConfirm}>{confirmLabel}</button><button type="button" className="btn btn-outline-dark rounded-0 px-4 py-3" onClick={onClose}>Cancelar</button></div>
      </div>
    </dialog>
  )
}
