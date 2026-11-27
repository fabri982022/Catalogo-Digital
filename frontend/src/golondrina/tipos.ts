/**
 * Golondrina — tipos compartidos.
 *
 * Una "acción" es algo que el sistema sabe hacer (navegar, buscar, enviar un
 * mensaje...). El asistente las ejecuta a pedido del usuario y Golondrina las
 * observa para aprender hábitos y luego sugerirlas en el momento oportuno.
 */

export type ValorContexto = string | number | boolean | null | undefined

/** Dónde está el usuario y qué está pasando en el sistema. */
export interface Contexto {
  /** Pantalla o sección actual (ej: "catalogo", "admin", "chat-general"). */
  seccion: string
  /** Datos extra que la app quiera aportar (ej: { conversacion: 'dm' }). */
  extra?: Record<string, ValorContexto>
}

export type Parametros = Record<string, string>

export interface ParametroAccion {
  nombre: string
  /** Valores posibles; el asistente los reconoce dentro de la frase del usuario. */
  opciones?: () => string[]
  /** Si es texto libre, se toma lo que sigue a las palabras de la frase. */
  libre?: boolean
  requerido?: boolean
  /** Pregunta que hace el asistente si falta el parámetro. */
  pregunta?: string
  /**
   * Si es false, el valor no forma parte del "hábito" aprendido
   * (útil para textos largos o datos sensibles). Por defecto true.
   */
  memorizable?: boolean
}

export type ResultadoAccion = void | string | Promise<void | string>

export interface Accion {
  id: string
  /** Texto corto que se muestra en botones: "Ver puertas". Puede usar {param}. */
  titulo: string
  descripcion?: string
  icono?: string
  /**
   * Frases de ejemplo en lenguaje natural. Usá {nombreParametro} para marcar
   * dónde va un parámetro: "buscar {consulta}", "enviar {texto}".
   */
  frases?: string[]
  parametros?: ParametroAccion[]
  /** Secciones donde la acción tiene sentido. Vacío = en todas. */
  secciones?: string[]
  /** Si devuelve false la acción no se ofrece ni se ejecuta. */
  disponible?: (ctx: Contexto) => boolean
  /** Qué le falta al usuario cuando no está disponible (ej: "Primero ingresá a gestión"). */
  requisito?: string
  ejecutar: (parametros: Parametros, ctx: Contexto) => ResultadoAccion
  /** Golondrina puede presentarla al usuario aunque nunca la haya usado. */
  descubrible?: boolean
  /** Cómo hacerlo a mano (se muestra al enseñar la acción). */
  comoHacerlo?: string
}

export type Via = 'manual' | 'asistente' | 'golondrina'

export interface Evento {
  accion: string
  firma: string
  parametros: Parametros
  seccion: string
  franja: Franja
  via: Via
  t: number
}

export type Franja = 'madrugada' | 'manana' | 'tarde' | 'noche'

export type TipoSugerencia = 'siguiente' | 'habito' | 'entrada' | 'atajo' | 'descubrir'

export interface Sugerencia {
  /** Identificador estable para no repetir / silenciar. */
  clave: string
  tipo: TipoSugerencia
  accion: string
  parametros: Parametros
  /** Mensaje que muestra Golondrina. */
  mensaje: string
  /** Texto del botón principal. */
  boton: string
  /** 0..1 */
  confianza: number
}

/** Lo que el usuario elige en «Golondrina 100% configurable». Se guarda en su navegador. */
export interface ConfigGolondrina {
  /** Ya pasó por la configuración (para no volver a pedírsela). */
  configurada: boolean
  /** Aprender de lo que hace (si es false, no anota nada nuevo). */
  aprender: boolean
  /** Salir volando con sugerencias. */
  sugerir: boolean
  /** Qué tipos de sugerencia quiere ver. */
  tipos: TipoSugerencia[]
  /** Cuántas veces tiene que repetir algo para que Golondrina lo tome como costumbre. */
  minRepeticiones: number
  /** De qué lado aparece el botón. */
  posicion: 'derecha' | 'izquierda'
}

export interface OpcionesGolondrina {
  /** Identifica el sistema; separa la memoria entre apps. */
  app: string
  /** Nombre visible del asistente. */
  nombre?: string
  acciones: Accion[]
  saludo?: string
  colorPrimario?: string
  colorTexto?: string
  posicion?: 'derecha' | 'izquierda'
  /** Distancia al borde inferior en px (para no tapar otros botones flotantes). */
  abajo?: number
  /** Repeticiones mínimas para considerar algo un hábito. Default 3. */
  minRepeticiones?: number
  /** Mostrar sugerencias automáticamente. Default true. */
  sugerir?: boolean
  /** Se llama cuando el usuario le pide al asistente «configurar» (la app abre su panel). */
  alConfigurar?: () => void
  /** Contenedor donde montar la UI. Default document.body. */
  contenedor?: HTMLElement
  /** Escuchar clics en elementos con data-golondrina="idAccion". Default true. */
  capturarClics?: boolean
  /**
   * Intérprete opcional (por ejemplo un LLM) para frases que el motor local no
   * entiende. Debe devolver la acción y sus parámetros, o null.
   */
  interpretar?: (texto: string, acciones: Accion[], ctx: Contexto) => Promise<{ accion: string; parametros?: Parametros } | null>
  /** Almacenamiento; por defecto localStorage. */
  almacenamiento?: Pick<Storage, 'getItem' | 'setItem' | 'removeItem'>
  /** Reloj inyectable (tests). */
  ahora?: () => number
}
