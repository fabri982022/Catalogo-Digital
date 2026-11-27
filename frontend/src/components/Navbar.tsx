import type { Usuario, View } from '../types'
import { LogoGolondrina } from './GolondrinaConfig'
import { abrirConfigGolondrina } from '../golondrinaConfig'

type NavbarProps = {
  usuario: Usuario | null
  view: View
  onNavigate: (view: View) => void
  onLogout: () => void
}

export function Navbar({ usuario, view, onNavigate, onLogout }: NavbarProps) {
  return (
    <nav className="navbar navbar-expand-lg app-nav sticky-top">
      <div className="container-fluid px-4 px-lg-5">
        <button className="brand-mark border-0 bg-transparent" onClick={() => onNavigate('catalogo')}>
          <span className="brand-symbol">CD</span>
          <span>
            <strong>Catalogo Digital</strong>
            <small>puertas / ventanas</small>
          </span>
        </button>

        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="mainNav">
          <div className="navbar-nav mx-auto gap-lg-2">
            <NavButton active={view === 'catalogo'} golondrina="ver-todo" onClick={() => onNavigate('catalogo')}>Explorar</NavButton>
            <NavButton active={view === 'categorias'} golondrina="ver-colecciones" onClick={() => onNavigate('categorias')}>Colecciones</NavButton>
            {usuario && <NavButton active={view === 'admin'} golondrina="ir-gestion" onClick={() => onNavigate('admin')}>Gestion</NavButton>}
          </div>

          <button type="button" className="g-nav" onClick={abrirConfigGolondrina} title="Configurar Golondrina">
            <LogoGolondrina size={16} /> Golondrina
          </button>
          {usuario ? (
            <div className="user-menu">
              <span className="user-dot">{usuario.nombreUsuario.slice(0, 1).toUpperCase()}</span>
              <span>{usuario.nombreUsuario}</span>
              <button data-golondrina="cerrar-sesion" onClick={onLogout}>Salir</button>
            </div>
          ) : (
            <button className="login-link" data-golondrina="ingresar" onClick={() => onNavigate('login')}>Acceder -&gt;</button>
          )}
        </div>
      </div>
    </nav>
  )
}

function NavButton({ active, onClick, children, golondrina }: { active: boolean; onClick: () => void; children: string; golondrina?: string }) {
  return <button className={`nav-link ${active ? 'active' : ''}`} data-golondrina={golondrina} onClick={onClick}>{children}</button>
}
