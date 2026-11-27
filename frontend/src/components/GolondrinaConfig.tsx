import { useEffect, useState } from 'react'
import { asistente } from '../asistente'
import { TIPOS_SUGERENCIA, type ConfigGolondrina, type TipoSugerencia } from '../golondrina'
import '../golondrina-config.css'
import { EVENTO_CONFIGURAR, abrirConfigGolondrina, useConfigGolondrina } from '../golondrinaConfig'


const TIPOS: Record<TipoSugerencia, [string, string]> = {
  siguiente: ['Lo que sigue', 'Después de X casi siempre hacés Y: te lo adelanto.'],
  entrada: ['Al llegar', 'Lo primero que hacés cuando entrás.'],
  habito: ['Costumbres por horario', 'Lo que hacés en esta pantalla a esta hora.'],
  atajo: ['Atajos', 'Algo que hacés a mano y me podés pedir con tus palabras.'],
  descubrir: ['Funciones nuevas', 'Cosas útiles que todavía no usaste.'],
}

const RAPIDEZ: [number, string][] = [
  [2, 'Rápido (a la 2.ª vez)'],
  [3, 'Normal (a la 3.ª vez)'],
  [5, 'Tranquilo (a la 5.ª vez)'],
]

/** Logo de Golondrina: la «G» de circuito dentro del escudo. */
export function LogoGolondrina({ size = 28 }: { size?: number }) {
  return (
    <svg viewBox="0 0 100 115" width={size} height={size * 1.15} aria-hidden="true">
      <path d="M 50 6 L 89 19 C 89 62 80 92 50 110 C 20 92 11 62 11 19 Z" fill="#0B1220" stroke="#22D3EE" strokeWidth="6" strokeLinejoin="round" />
      <path d="M66 40 H42 L32 50 V70 L42 80 H58 L66 72 V62 H52" stroke="#F1F5F9" strokeWidth="8" strokeLinecap="round" strokeLinejoin="round" fill="none" />
      <circle cx="66" cy="40" r="6" fill="#22D3EE" />
      <circle cx="52" cy="62" r="6" fill="#22D3EE" />
    </svg>
  )
}


/** Cartel arriba de todo hasta que el usuario configura Golondrina: es lo primero que ve. */
export function GolondrinaBanner() {
  const config = useConfigGolondrina()
  if (config.configurada) return null
  return (
    <div className="g-banner" role="region" aria-label="Configurar Golondrina">
      <LogoGolondrina size={30} />
      <p>
        <strong>Golondrina es 100% configurable.</strong> Antes de empezar, elegí qué aprende, qué te sugiere y de qué lado aparece.
      </p>
      <button type="button" onClick={abrirConfigGolondrina}>Configurar</button>
    </div>
  )
}

/** Panel con todo lo que se puede configurar de Golondrina. */
export function GolondrinaConfig() {
  const guardada = useConfigGolondrina()
  const [abierta, setAbierta] = useState(false)
  const [c, setC] = useState<ConfigGolondrina>(guardada)
  const [borrado, setBorrado] = useState(false)

  useEffect(() => {
    const abrir = () => {
      setC(asistente.configuracion)
      setBorrado(false)
      setAbierta(true)
    }
    window.addEventListener(EVENTO_CONFIGURAR, abrir)
    return () => window.removeEventListener(EVENTO_CONFIGURAR, abrir)
  }, [])

  useEffect(() => {
    if (!abierta) return
    const tecla = (e: KeyboardEvent) => e.key === 'Escape' && setAbierta(false)
    document.addEventListener('keydown', tecla)
    return () => document.removeEventListener('keydown', tecla)
  }, [abierta])

  if (!abierta) return null

  const alternarTipo = (t: TipoSugerencia) =>
    setC((v) => ({ ...v, tipos: v.tipos.includes(t) ? v.tipos.filter((x) => x !== t) : [...v.tipos, t] }))

  const guardar = () => {
    asistente.configurar({ ...c, configurada: true })
    setAbierta(false)
  }

  return (
    <div className="product-overlay" onClick={() => setAbierta(false)}>
      <div className="g-config" role="dialog" aria-modal="true" aria-labelledby="g-config-titulo" onClick={(e) => e.stopPropagation()}>
        <button type="button" className="close-dialog" onClick={() => setAbierta(false)} aria-label="Cerrar">×</button>
        <div className="g-config-cabecera">
          <LogoGolondrina size={40} />
          <div>
            <span className="eyebrow">Golondrina · 100% configurable</span>
            <h2 id="g-config-titulo">Configurá tu asistente</h2>
          </div>
        </div>

        <section>
          <h3>1. ¿Aprendo de lo que hacés?</h3>
          <label className="g-opcion">
            <input type="checkbox" checked={c.aprender} onChange={(e) => setC({ ...c, aprender: e.target.checked })} />
            <span>Sí, anotá lo que hago para descubrir mis costumbres <small>(se guarda solo en este navegador)</small></span>
          </label>
        </section>

        <section>
          <h3>2. ¿Te sugiero cosas?</h3>
          <label className="g-opcion">
            <input type="checkbox" checked={c.sugerir} onChange={(e) => setC({ ...c, sugerir: e.target.checked })} />
            <span>Sí, aparecé volando con una sugerencia cuando veas algo útil</span>
          </label>
          <div className={`g-tipos ${c.sugerir ? '' : 'g-apagado'}`}>
            {TIPOS_SUGERENCIA.map((t) => (
              <label key={t} className="g-opcion">
                <input type="checkbox" disabled={!c.sugerir} checked={c.tipos.includes(t)} onChange={() => alternarTipo(t)} />
                <span><strong>{TIPOS[t][0]}</strong> <small>{TIPOS[t][1]}</small></span>
              </label>
            ))}
          </div>
        </section>

        <section>
          <h3>3. ¿Qué tan rápido aprendo una costumbre?</h3>
          <div className="g-chips">
            {RAPIDEZ.map(([n, t]) => (
              <button key={n} type="button" aria-pressed={c.minRepeticiones === n} className={`filter-pill ${c.minRepeticiones === n ? 'selected' : ''}`} onClick={() => setC({ ...c, minRepeticiones: n })}>{t}</button>
            ))}
          </div>
        </section>

        <section>
          <h3>4. ¿De qué lado aparezco?</h3>
          <div className="g-chips">
            {(['izquierda', 'derecha'] as const).map((lado) => (
              <button key={lado} type="button" aria-pressed={c.posicion === lado} className={`filter-pill ${c.posicion === lado ? 'selected' : ''}`} onClick={() => setC({ ...c, posicion: lado })}>
                {lado === 'izquierda' ? '← Izquierda' : 'Derecha →'}
              </button>
            ))}
          </div>
        </section>

        <section>
          <h3>5. Lo que ya aprendí</h3>
          <button
            type="button"
            className="g-secundario"
            onClick={() => {
              asistente.memoria.olvidarTodo()
              setBorrado(true)
            }}
          >
            Olvidar todo lo aprendido
          </button>
          {borrado && <span className="g-ok" role="status">Listo, empiezo de cero.</span>}
        </section>

        <div className="g-config-pie">
          <small>También podés escribirle «configurar» al asistente para volver acá.</small>
          <button type="button" className="g-principal" onClick={guardar}>
            {guardada.configurada ? 'Guardar cambios' : 'Guardar y empezar'}
          </button>
        </div>
      </div>
    </div>
  )
}
