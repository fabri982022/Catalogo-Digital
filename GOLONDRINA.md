# Golondrina — asistente que aprende de cada usuario

Golondrina tiene dos partes que trabajan juntas:

1. **El asistente** (botón con la golondrina). El usuario le pide las cosas con sus palabras («buscame puertas de roble», «abrí Cubaeduca», «decile a Ana que llego tarde») y el asistente ejecuta la acción en el sistema. Ya no hace falta aprender dónde está cada botón.
2. **La automatización Golondrina**. Observa lo que el usuario hace —a mano o a través del asistente— junto con su **contexto** (pantalla, franja horaria, qué hizo antes) y, cuando algo se **repite**, lo aprende y se lo muestra en el momento justo: la golondrina llega volando con una sugerencia de un toque.

## Qué aprende

| Tipo | Cuándo aparece | Ejemplo |
|---|---|---|
| **Secuencia** | Después de X, el usuario casi siempre hace Y | «Cada vez que abrís la ficha de un producto, después seguís con *Consultar por WhatsApp*. ¿Te lo adelanto?» |
| **Entrada** | Lo primero que hace al llegar, repetido en varias sesiones | «Cada vez que llegás, lo primero que hacés es *Abrir Cubaeduca*.» |
| **Hábito** | Algo que hace en esta pantalla y a esta hora del día | «Por la mañana solés enviar *buen día gente*. ¿Lo hago por vos?» |
| **Atajo** | Algo que hace siempre a mano y podría pedírselo al asistente | «Veo que hacés *ver Puertas de interior* seguido. Escribime eso y lo hago yo.» |
| **Descubrir** | Una función útil que todavía no usó, en la pantalla donde pasa tiempo | «¿Sabías que podés pedirme qué productos se quedaron sin stock?» |

Además, cuando el asistente no está seguro, ofrece opciones; si el usuario elige una, **aprende esa frase** y la próxima vez la entiende directo.

Reglas para no molestar: un hábito necesita **3 repeticiones** (`minRepeticiones`), lo que no se repite se va olvidando (vida media de 14 días), hay al menos 90 s entre sugerencias, cada una descansa 20 min después de mostrarse, y si se rechaza 3 veces o se toca «No me muestres esto de nuevo», no vuelve.

## Privacidad y control del usuario

Todo se guarda **solo en el navegador** del usuario (`localStorage`, clave `golondrina:<app>[:usuario]`); no se envía nada a ningún servidor. El usuario puede escribirle al asistente:

- «¿qué aprendiste?» → le muestra sus hábitos y secuencias aprendidas.
- «no me sugieras» / «volvé a sugerir» → silencia o reactiva las sugerencias.
- «olvidá todo» → borra su memoria.
- «ayuda» → lista lo que puede pedir en la pantalla actual.

## Cómo está armado

```
golondrina/            ← núcleo común, idéntico en los tres sistemas, sin dependencias
  tipos.ts             ← Accion, Contexto, Sugerencia, opciones
  texto.ts             ← normalización del español, sinónimos, raíces, tolerancia a errores
  interprete.ts        ← convierte una frase en acción + parámetros (motor local, sin IA externa)
  memoria.ts           ← registra eventos, hábitos, secuencias, frases aprendidas
  aprendizaje.ts       ← decide qué sugerir según repetición + contexto
  ui.ts                ← botón, panel y "vuelo" (Shadow DOM: no choca con los estilos del sistema)
  index.ts             ← clase Golondrina y crearGolondrina()
```

### Agregar una acción nueva

```ts
{
  id: 'buscar',
  titulo: 'Buscar «{consulta}»',                    // texto de botones; {param} se reemplaza
  descripcion: 'buscar piezas por nombre o código',  // se usa al enseñar la función
  frases: ['buscar {consulta}', 'tenés {consulta}'], // ejemplos en lenguaje natural
  parametros: [{ nombre: 'consulta', libre: true, requerido: true, pregunta: '¿Qué busco?' }],
  secciones: ['catalogo'],          // opcional: dónde tiene sentido
  disponible: (ctx) => true,        // opcional: si hoy se puede hacer
  requisito: 'Primero ingresá…',    // opcional: qué falta cuando no está disponible
  descubrible: true,                // opcional: Golondrina puede enseñarla
  ejecutar: ({ consulta }) => { /* hacer la acción */ return 'Texto de respuesta' },
}
```

Parámetros: `opciones: () => string[]` (se reconocen dentro de la frase, con o sin acentos/plurales) o `libre: true` (toma el texto que sigue a la frase). `memorizable: false` evita que un valor forme parte del hábito.

### Registrar lo que el usuario hace a mano

- En código: `asistente.registrar('filtrar-categoria', { categoria: 'Puertas' })`.
- Sin código: agregar atributos al elemento → `data-golondrina="abrir-plataforma" data-golondrina-plataforma="Cubaeduca"`.
- Contexto: `asistente.contexto('admin', { producto: 'Puerta Roble' })` cada vez que cambia la pantalla.

### Conectar una IA (opcional)

El intérprete local cubre las frases de las acciones. Si se quiere entender lenguaje más libre, se puede pasar `interpretar: async (texto, acciones, ctx) => ({ accion, parametros })` al crear Golondrina (por ejemplo llamando a un modelo de lenguaje desde el backend). Solo se usa cuando el motor local no está seguro.

## En este sistema (Catálogo Digital)

- Núcleo: `frontend/src/golondrina/` · Acciones del catálogo: `frontend/src/asistente.ts`.
- `App.tsx` monta Golondrina, le pasa las acciones con el estado actual, informa el contexto (pantalla, producto abierto, categoría elegida) y separa la memoria por administrador.
- Para que el asistente pueda buscar y filtrar, la búsqueda y la categoría activa ahora viven en `App.tsx` (antes eran estado interno de `CatalogPage`).
- `AdminPage` acepta `productToEdit` para que el asistente cargue un producto en el formulario.
- Se registran: filtros de categoría, apertura de productos, búsquedas (cuando el usuario deja de escribir), consultas por WhatsApp, navegación y altas/ediciones en gestión.

| Pedido de ejemplo | Acción |
|---|---|
| «buscame algo de aluminio» | Busca y muestra los resultados |
| «mostrame las puertas de exterior» | Filtra por categoría |
| «abrí la puerta pivotante negra» | Abre la ficha del producto |
| «quiero comprarla» / «consultar por WhatsApp» | Abre WhatsApp con el mensaje armado del producto abierto |
| «¿cuántos productos hay?» | Resumen por categoría |
| «ver colecciones» / «ver todo» | Navega |
| «ingresar» | Lleva al acceso de gestión |
| «nuevo producto» / «modificar Puerta Roble» (gestión) | Abre el formulario vacío o cargado |
| «productos sin stock» (gestión) | Lista lo agotado y lo que queda con 3 o menos |

Lo que aprende acá: categorías que cada visitante mira primero, la secuencia «ver producto → consultar por WhatsApp» (y se la adelanta), y en gestión las tareas que el administrador repite.
