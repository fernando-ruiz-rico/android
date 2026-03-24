/**
 * =========================================================================================
 * ARCHIVO: Aros.kt
 * =========================================================================================
 * PROPÓSITO:
 * Implementar el clásico minijuego de ensartar aros de plástico en postes verticales.
 *
 * QUÉ HACE EL CÓDIGO:
 * Define un escenario con tres postes amarillos y genera aros de colores que flotan y caen.
 * Contiene el algoritmo matemático para detectar cuándo el centro de un aro pasa por
 * la punta superior de un poste. Si esto sucede, el aro queda atrapado, pierde su inercia
 * horizontal y se desliza hacia abajo hasta amontonarse físicamente en la base.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Simulación de Perspectiva Falsa (2.5D): Usando `drawOval` logramos que un círculo
 * perfecto parezca un aro acostado en profundidad una vez se encaja en el poste.
 * 2. Cajas de Colisión (AABB): Calculamos si el centro de un aro cruza el ancho estricto
 * de la cabeza de nuestro poste amarillo.
 * 3. Lógica de Apilamiento Espacial: Algoritmo para contar cuántos elementos hay
 * atrapados en una columna y fijar dinámicamente la altura final de caída
 * para que los elementos reposen unos encima de otros físicamente hasta llegar al verde.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Clase que representa el minijuego de ensartar aros.
 * Hereda de [JuegoAguaBase] para utilizar su motor físico de fluidos y gestión táctil.
 *
 * @param context El contexto de la aplicación Android, necesario para poder dibujar en pantalla.
 */
class JuegoArosView(context: Context) : JuegoAguaBase(context) {

    /** * Pincel configurado en modo `STROKE` (Solo contorno).
     * En lugar de rellenar el círculo con color, dibuja un "Donut" matemático.
     */
    private val paintAro = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    /** Pincel para pintar las bases sólidas y los palos amarillos de la máquina. */
    private val paintPoste = Paint().apply { color = Color.parseColor("#FFEB3B") }

    /** Coordenada horizontal (X) donde se plantará el poste izquierdo. */
    private var posteIzqX = 0f
    /** Coordenada horizontal (X) donde se plantará el poste central. */
    private var posteCentroX = 0f
    /** Coordenada horizontal (X) donde se plantará el poste derecho. */
    private var posteDerX = 0f
    /** Medida vertical (Y) que indica la longitud visible del poste. */
    private var altoPoste = 0f
    /** Coordenada vertical (Y) donde está la "punta" más alta de los postes para detectar entradas. */
    private var techoPosteY = 0f

    /** Lista de 18 colores hexadecimales variados para que los aros sean fácilmente distinguibles al amontonarse. */
    private val coloresAros = listOf(
        Color.parseColor("#FF0000"), Color.parseColor("#FF5252"), Color.parseColor("#FF4081"),
        Color.parseColor("#E040FB"), Color.parseColor("#AA00FF"), Color.parseColor("#651FFF"),
        Color.parseColor("#3D5AFE"), Color.parseColor("#2979FF"), Color.parseColor("#00B0FF"),
        Color.parseColor("#00E5FF"), Color.parseColor("#1DE9B6"), Color.parseColor("#00E676"),
        Color.parseColor("#76FF03"), Color.parseColor("#C6FF00"), Color.parseColor("#FFEA00"),
        Color.parseColor("#FFC400"), Color.parseColor("#FF9100"), Color.parseColor("#FF3D00")
    )

    init {
        // Redefinimos el color base del juguete heredado de la clase madre Base.kt a Verde.
        paintBase.color = Color.parseColor("#4CAF50")

        // Nombre del archivo a reproducir como música de fondo
        idMusicaFondo = R.raw.musica_aros
    }

    /**
     * Se llama cuando la pantalla del dispositivo ya tiene sus medidas finales.
     * Calcula las posiciones exactas de los elementos en base a dichas medidas.
     *
     * @param ancho Anchura disponible en píxeles.
     * @param alto Altura disponible en píxeles.
     */
    override fun inicializarNivel(ancho: Int, alto: Int) {
        // Generamos un diseño que se adapte proporcionalmente a cualquier pantalla de móvil
        posteIzqX = ancho * 0.20f
        posteCentroX = ancho * 0.5f
        posteDerX = ancho * 0.80f

        altoPoste = alto * 0.35f
        techoPosteY = (alto * 0.8f) - altoPoste

        objetosFlotantes.clear()
        puntuacion = 0

        // Queremos llenar la pantalla inicial con 18 aros
        for (i in 0 until 18) {
            generarNuevoObjeto()
        }
    }

    /**
     * Crea una nueva pieza flotante (Aro) en una posición semi-aleatoria de la pantalla
     * y la añade a la lista principal de objetos que manejan las físicas.
     */
    override fun generarNuevoObjeto() {
        val posX = (Math.random() * (width - 100) + 50).toFloat()
        val posY = (Math.random() * (height * 0.3f) + height * 0.5f).toFloat()

        objetosFlotantes.add(
            ObjetoFlotante(
                x = posX,
                y = posY,
                radio = 55f,
                color = coloresAros.random()
            )
        )
    }

    /**
     * Bucle visual. Pinta las piezas estáticas (postes) y luego itera sobre
     * cada aro flotante para dibujarlo.
     *
     * @param canvas El lienzo proporcionado por Android para dibujar gráficos.
     */
    override fun dibujarJuego(canvas: Canvas) {
        // Postes amarillos dibujados en el fondo (10 píxeles a cada lado de su centro lógico)
        canvas.drawRect(posteIzqX - 10f, techoPosteY, posteIzqX + 10f, height * 0.8f, paintPoste)
        canvas.drawRect(posteCentroX - 10f, techoPosteY, posteCentroX + 10f, height * 0.8f, paintPoste)
        canvas.drawRect(posteDerX - 10f, techoPosteY, posteDerX + 10f, height * 0.8f, paintPoste)

        for (p in objetosFlotantes) {
            paintAro.color = p.color

            if (p.atrapado) {
                // Alumno, fíjate aquí: Si está atrapado (ensartado), aplastamos su alto a solo 20 píxeles.
                // Esto engaña al ojo humano haciendo que parezca que vemos el aro desde arriba en perspectiva.
                canvas.drawOval(p.x - p.radio, p.y - 10f, p.x + p.radio, p.y + 10f, paintAro)
            } else {
                // Si flota libre en el agua, está de cara al usuario y lo vemos como un círculo perfecto.
                canvas.drawCircle(p.x, p.y, p.radio, paintAro)
            }
        }
    }

    /**
     * Algoritmo principal de este minijuego.
     * Comprueba choques contra los postes y gestiona el amontonamiento de piezas.
     */
    override fun comprobarLogicaEspecifica() {
        // Diccionario que actuará de contador para saber cuántos aros se han amontonado en cada poste.
        val apilados = mutableMapOf<Float, Int>()
        apilados[posteIzqX] = 0
        apilados[posteCentroX] = 0
        apilados[posteDerX] = 0

        // Ordenamos los aros desde el fondo de la pantalla hacia arriba.
        // Es imperativo para que el algoritmo apile el de más abajo como "0", el siguiente como "1", etc.
        val ordenados = objetosFlotantes.sortedByDescending { it.y }

        for (p in ordenados) {
            if (p.atrapado) {
                p.vx = 0f // Inmovilizado lateralmente, solo puede caer hacia abajo

                // Leemos cuántos aros YA HAN LLEGADO antes que él al suelo de ese mismo poste.
                val cantApilada = apilados[p.x] ?: 0

                // Calculamos el suelo: Base verde general (height * 0.8f)
                // menos el grosor de los aros que ya haya apilados debajo (20f por cada uno).
                val sueloObjetivo = (height * 0.8f) - 10f - (cantApilada * 20f)

                // Si ha cruzado su objetivo calculado en la caída...
                if (p.y >= sueloObjetivo) {
                    p.y = sueloObjetivo // Lo fijamos rígidamente en su lugar
                    p.vy = 0f // Matamos inercia gravitacional
                    apilados[p.x] = cantApilada + 1 // Subimos el contador para el SIGUIENTE aro que baje por aquí.
                } else {
                    // Aún está cayendo, lo aceleramos levemente para que caiga liso y natural por el palo
                    p.vy += 1f
                    // Lo contabilizamos igualmente porque, aunque siga cayendo, reserva su espacio,
                    // evitando que otro aro intente ocupar su misma altura física antes de llegar.
                    apilados[p.x] = cantApilada + 1
                }

            } else {
                // LÓGICA DE DETECCIÓN Y ENSARTADO INICIAL
                // El aro cae por el agua, debe ir hacia abajo (vy > 0) y su zona central cruzar la punta.
                if (p.vy > 0 && p.y + p.radio > techoPosteY - 15f && p.y < techoPosteY + 55f) {
                    // Comprobamos si su X se alinea con uno de los postes con margen de error del 80% de su radio.
                    if (Math.abs(p.x - posteIzqX) < p.radio * 0.8f) {
                        p.x = posteIzqX; p.atrapado = true
                    } else if (Math.abs(p.x - posteCentroX) < p.radio * 0.8f) {
                        p.x = posteCentroX; p.atrapado = true
                    } else if (Math.abs(p.x - posteDerX) < p.radio * 0.8f) {
                        p.x = posteDerX; p.atrapado = true
                    }
                } else if (p.y > techoPosteY) {
                    // REBOTE: Si no tuvo suerte de ensartarse, choca contra las paredes de los palos
                    if (Math.abs(p.x - posteIzqX) < p.radio) {
                        p.x = if (p.x < posteIzqX) posteIzqX - p.radio else posteIzqX + p.radio
                        p.vx *= -0.5f
                    } else if (Math.abs(p.x - posteCentroX) < p.radio) {
                        p.x = if (p.x < posteCentroX) posteCentroX - p.radio else posteCentroX + p.radio
                        p.vx *= -0.5f
                    } else if (Math.abs(p.x - posteDerX) < p.radio) {
                        p.x = if (p.x < posteDerX) posteDerX - p.radio else posteDerX + p.radio
                        p.vx *= -0.5f
                    }
                }
            }
        }
    }
}