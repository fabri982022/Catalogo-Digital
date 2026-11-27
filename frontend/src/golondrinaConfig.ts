import { useEffect, useState } from 'react'
import { asistente } from './asistente'
import type { ConfigGolondrina } from './golondrina'

/** Evento para abrir la configuración desde cualquier lado (navbar, asistente, banner). */
export const EVENTO_CONFIGURAR = 'golondrina:configurar'
export const abrirConfigGolondrina = () => window.dispatchEvent(new CustomEvent(EVENTO_CONFIGURAR))

/** La configuración actual de Golondrina, siempre al día. */
export function useConfigGolondrina() {
  const [config, setConfig] = useState<ConfigGolondrina>(() => asistente.configuracion)
  useEffect(() => asistente.alCambiarConfig(setConfig), [])
  return config
}
