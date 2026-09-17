import type { Usuario, View } from '../types'

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
            <NavButton active={view === 'catalogo'} onClick={() => onNavigate('catalogo')}>Explorar</NavButton>
            <NavButton active={view === 'categorias'} onClick={() => onNavigate('categorias')}>Colecciones</NavButton>
            {usuario && <NavButton active={view === 'admin'} onClick={() => onNavigate('admin')}>Gestion</NavButton>}
          </div>

          {usuario ? (
            <div className="user-menu">
              <span className="user-dot">{usuario.nombreUsuario.slice(0, 1).toUpperCase()}</span>
              <span>{usuario.nombreUsuario}</span>
              <button onClick={onLogout}>Salir</button>
            </div>
          ) : (
            <button className="login-link" onClick={() => onNavigate('login')}>Acceder -&gt;</button>
          )}
        </div>
      </div>
    </nav>
  )
}

function NavButton({ active, onClick, children }: { active: boolean; onClick: () => void; children: string }) {
  return <button className={`nav-link ${active ? 'active' : ''}`} onClick={onClick}>{children}</button>
}
