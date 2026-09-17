import { useState } from 'react'
import type { FormEvent } from 'react'
import { authenticate } from '../api'
import type { Usuario } from '../types'

type LoginPageProps = {
  onLogin: (usuario: Usuario) => void
  onBack: () => void
}

export function LoginPage({ onLogin, onBack }: LoginPageProps) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(event: FormEvent) {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      const user = await authenticate(username, password)
      onLogin(user)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'No se pudo iniciar sesion.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="login-page">
      <div className="login-panel">
        <button className="back-link" onClick={onBack}>← Volver al catalogo</button>
        <span className="eyebrow">Area privada</span>
        <h1>Bienvenido<br /><em>de nuevo.</em></h1>
        <p>Accede para administrar productos, disponibilidad e imagenes.</p>

        <form onSubmit={submit} className="login-form">
          <label>Usuario<input required value={username} onChange={(event) => setUsername(event.target.value)} /></label>
          <label>Contrasena<input required type="password" value={password} onChange={(event) => setPassword(event.target.value)} /></label>
          {error && <div className="login-error">{error}</div>}
          <button className="btn btn-dark rounded-0 w-100 py-3" disabled={loading}>{loading ? 'Verificando...' : 'Entrar al espacio'}</button>
        </form>
      </div>
    </section>
  )
}
