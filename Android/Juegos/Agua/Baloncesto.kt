/**
 * =========================================================================================
 * ARCHIVO: Baloncesto.kt
 * =========================================================================================
 * PROPÓSITO:
 * Implementar una simulación de minijuego de baloncesto doble incrustado en agua.
 *
 * QUÉ HACE EL CÓDIGO:
 * Renderiza tableros, aros y mallas geométricas avanzadas usando dibujo vectorial.
 * Analiza el comportamiento de 18 balones independientes. Posee una lógica anti-bloqueo
 * especializada que fuerza artificialmente desvíos laterales (resbalones) si un balón
 * contacta con exactitud matemática el borde del anillo para evitar que quede atascado eternamente.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Dibujo avanzado por Vectores (Path): Aprender a trazar una "U" a mano dictando puntos
 * en un eje de coordenadas X e Y para hacer la red de la canasta.
 * 2. DashPathEffect: Un efecto que cambia el pincel para pintar de forma intermitente
 * (líneas discontinuas punteadas), perfecto para simular las mallas o cosidos.
 * 3. Físicas Anti-Equilibrio (Vectores condicionales): Lógica defensiva en el código
 * para evitar que dos objetos choquen perfectamente centrados y se anulen sus fuerzas,
 * causando que una pelota flote estática encima del borde de un aro de metal.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path

/**
 * Representa la pantalla interactiva del minijuego derivado "Baloncesto".
 *
 * @param context El puente vital al ecosistema base de la App Android.
 */
class JuegoBaloncestoView(context: Context) : JuegoAguaBase(context) {

    /** Pincel dinámico que tomará diferentes configuraciones y colores en cada ciclo del balón principal. */
    private val paintBalon = Paint()

    /** Pincel macizo para trazar la dureza visual del hierro que sostiene las canastas a cada extremo. */
    private val paintCanasta = Paint().apply {
        color = Color.parseColor("#0D47A1")
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    /** * Pincel en modo punteado que simula hilos en red. El parámetro fundamental es el `DashPathEffect`.
     * Alterna tramos de 10 píxeles encendidos de tinta, seguidos por huecos abstractos limpios de 10 píxeles nulos.
     */
    private val paintRed = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    /** Pincel rústico llano para rellenar los rectángulos de apoyo de cada canasta con tonalidad roja genérica. */
    private val paintTablero = Paint().apply { color = Color.RED }

    /** Eje vertical inicial precalculado donde anclará su base estructural la canasta del extremo izquierdo. */
    private var canastaIzqY = 0f
    /** Eje vertical escalonado donde anclará su diseño diferencialmente más alto la canasta del bloque derecho. */
    private var canastaDerY = 0f
    /** Dimensión de anchura transversal absoluta e inviolable que medirá siempre cada embocadura de las cestas. */
    private val anchoCanasta = 260f

    /** Estructura de Listado que provee una gama variada de colores vivos forzando contraste e intensidad máxima a las esferas. */
    private val coloresPelotas = listOf(
        Color.parseColor("#FF0000"), Color.parseColor("#FF5252"), Color.parseColor("#FF4081"),
        Color.parseColor("#E040FB"), Color.parseColor("#AA00FF"), Color.parseColor("#651FFF"),
        Color.parseColor("#3D5AFE"), Color.parseColor("#2979FF"), Color.parseColor("#00B0FF"),
        Color.parseColor("#00E5FF"), Color.parseColor("#1DE9B6"), Color.parseColor("#00E676"),
        Color.parseColor("#76FF03"), Color.parseColor("#C6FF00"), Color.parseColor("#FFEA00"),
        Color.parseColor("#FFC400"), Color.parseColor("#FF9100"), Color.parseColor("#FF3D00")
    )

    init {
        // Asentamos un tono ambarino vibrante al chasis bajo del aparato emulado de agua plástico.
        paintBase.color = Color.parseColor("#FFC107")

        // Modificamos selectivamente las tonalidades de descanso preasignadas por el motor genérico.
        // Transformamos su presentación por defecto en matices verdes llamativos y armónicos.
        colorBotonIzquierdoNormal = Color.parseColor("#4CAF50")
        colorBotonDerechoNormal = Color.parseColor("#4CAF50")

        // Nombre del archivo a reproducir como música de fondo
        idMusicaFondo = R.raw.musica_baloncesto
    }

    /**
     * Motor de escalado dinámico. Se lanza cuando poseemos los volúmenes en píxeles de nuestra pantalla.
     * Ajusta la distancia Y del objetivo y fabrica los contendientes esféricos iniciales.
     *
     * @param ancho Ancho resolutivo definitivo del LCD de usuario.
     * @param alto Altitud resolutiva definitiva.
     */
    override fun inicializarNivel(ancho: Int, alto: Int) {
        canastaIzqY = alto * 0.4f
        canastaDerY = alto * 0.3f

        objetosFlotantes.clear()
        puntuacion = 0

        // Invocamos secuencialmente un contingente de 14 pelotas activas para la dificultad deseada del puzzle.
        for (i in 0 until 14) {
            generarNuevoObjeto()
        }
    }

    /**
     * Fábrica encargada de depositar una nueva instancia pelicular al circuito acuático general.
     */
    override fun generarNuevoObjeto() {
        val posX = (Math.random() * (width - 100) + 50).toFloat()
        val posY = (Math.random() * (height * 0.4f) + height * 0.4f).toFloat()

        objetosFlotantes.add(
            ObjetoFlotante(x = posX, y = posY, radio = 55f, color = coloresPelotas.random())
        )
    }

    /**
     * Lógica gráfica encargada de trazar rectángulos, vectores continuos y formas simples componiendo una escena sofisticada.
     *
     * @param canvas Recipiente final del pintado fotogramétrico.
     */
    override fun dibujarJuego(canvas: Canvas) {
        // Tableros de cristal protector virtualizados
        canvas.drawRect(0f, canastaIzqY - 70f, 20f, canastaIzqY + 70f, paintTablero)
        canvas.drawRect(width - 20f, canastaDerY - 70f, width.toFloat(), canastaDerY + 70f, paintTablero)

        // Trazado en cadena. Configuración de Puntos Unidireccionales componiendo un dibujo en formato de "U" invertida.
        val pathRedIzq = Path().apply {
            moveTo(20f, canastaIzqY)
            lineTo(50f, canastaIzqY + 180f)
            lineTo(20f + anchoCanasta - 30f, canastaIzqY + 180f)
            lineTo(20f + anchoCanasta, canastaIzqY)
        }
        canvas.drawPath(pathRedIzq, paintRed)

        val pathRedDer = Path().apply {
            moveTo(width - 20f, canastaDerY)
            lineTo(width - 50f, canastaDerY + 180f)
            lineTo(width - 20f - anchoCanasta + 30f, canastaDerY + 180f)
            lineTo(width - 20f - anchoCanasta, canastaDerY)
        }
        canvas.drawPath(pathRedDer, paintRed)

        // Bastidores circulares endurecidos del embudo central. Trazados como línea maciza sólida.
        canvas.drawLine(20f, canastaIzqY, 20f + anchoCanasta, canastaIzqY, paintCanasta)
        canvas.drawLine(width - 20f, canastaDerY, width - 20f - anchoCanasta, canastaDerY, paintCanasta)

        // Mecanismo visual para adornar el objeto estricto "círculo" en una réplica virtual con estrías de pelota deportiva.
        for (p in objetosFlotantes) {
            // Fondo de color plano inyectado que cubre y da forma principal a la bola
            paintBalon.color = p.color
            paintBalon.style = Paint.Style.FILL
            canvas.drawCircle(p.x, p.y, p.radio, paintBalon)

            // Alteramos drásticamente el uso del Pincel a modo línea (`STROKE`) para calcar los canales y guías de un balón original.
            paintBalon.color = Color.BLACK
            paintBalon.style = Paint.Style.STROKE
            paintBalon.strokeWidth = 3f

            // Re-dibujo de la coraza, y líneas cruzadas vectorizadas transversales.
            canvas.drawCircle(p.x, p.y, p.radio, paintBalon)
            canvas.drawLine(p.x - p.radio, p.y, p.x + p.radio, p.y, paintBalon)
            canvas.drawLine(p.x, p.y - p.radio, p.x, p.y + p.radio, paintBalon)
            // Círculo central ovalado que corona y estiliza las marcas icónicas del balón cestero.
            canvas.drawOval(p.x - p.radio*0.5f, p.y - p.radio, p.x + p.radio*0.5f, p.y + p.radio, paintBalon)
        }
    }

    /**
     * Bucle analítico denso encargado de auditar la relación geográfica entre las entidades redondas
     * fluyentes y sus obstáculos geométricos, aplicando empujones o correcciones anti-estáticas.
     */
    override fun comprobarLogicaEspecifica() {
        for (p in objetosFlotantes) {

            // EXAMEN DE REclusión FÍSICA: Si la meta ya es cumplida, las normativas universales varían dramáticamente.
            if (p.atrapado) {
                p.vx *= 0.8f // Incrementamos roce severo interno emulando choque de malla

                // Distribuidor Binario en base a cuadrantes: Definimos qué embudo está atrapando.
                if (p.x < width / 2f) { // Cuadrante Occidental (Izquierdo)
                    // Restricción posicional X: Imposibilita el escape atravesando matemáticamente las líneas del Path en "U".
                    if (p.x < 50f + p.radio) p.x = 50f + p.radio
                    if (p.x > 20f + anchoCanasta - 30f - p.radio) p.x = 20f + anchoCanasta - 30f - p.radio

                    // Suelo físico duro precalculado del final del túnel.
                    val topeInferior = canastaIzqY + 180f - p.radio - 5f
                    if (p.y > topeInferior) { p.y = topeInferior; p.vy = 0f }
                } else { // Cuadrante Oriental (Derecho)
                    val inicioDer = width - 20f - anchoCanasta
                    val finDer = width - 20f
                    if (p.x < inicioDer + 30f + p.radio) p.x = inicioDer + 30f + p.radio
                    if (p.x > finDer - 30f - p.radio) p.x = finDer - 30f - p.radio

                    val topeInferior = canastaDerY + 180f - p.radio - 5f
                    if (p.y > topeInferior) { p.y = topeInferior; p.vy = 0f }
                }

                // Ruptura abrupta y limpieza de ciclo (Bypass del resto del código perimetral irrelevante para atrapados).
                continue
            }

            // MÓDULO SENSOR: Analizar entrada legítima oficial del enceste. (Viniendo de arriba a abajo y centrado elásticamente).
            val entraIzq = p.vy > 0 && p.y > canastaIzqY - p.radio && p.y < canastaIzqY + 20f && p.x > 20f && p.x < 20f + anchoCanasta
            val entraDer = p.vy > 0 && p.y > canastaDerY - p.radio && p.y < canastaDerY + 20f && p.x > width - 20f - anchoCanasta && p.x < width - 20f

            if (entraIzq || entraDer) {
                p.atrapado = true
                p.vx *= 0.5f // Desaceleración inicial y amortiguación brusca post-encestamiento.
            } else {
                // =========================================================================
                // CORRECCIONES Y RESBALONES ARTIFICIALES AVANZADOS (ANTI-ATASCO)
                // =========================================================================

                // Malla lateral Izquierda Baja: Forzar escape rebotando hacia afuera con inercia manipulada.
                if (p.y > canastaIzqY + 20f && p.y < canastaIzqY + 180f && p.x > 20f && p.x < 20f + anchoCanasta) {
                    if (p.x < 20f + anchoCanasta / 2f) p.vx -= 3f // Empuje a babor
                    else p.vx += 3f // Empuje a estribor
                }

                // Malla lateral Derecha Baja
                if (p.y > canastaDerY + 20f && p.y < canastaDerY + 180f && p.x > width - 20f - anchoCanasta && p.x < width - 20f) {
                    val inicioDer = width - 20f - anchoCanasta
                    if (p.x < inicioDer + anchoCanasta / 2f) p.vx -= 3f
                    else p.vx += 3f
                }

                // =========================================================================
                // BLOQUEO ABSOLUTO DEL EQUILIBRIO MATEMÁTICO INFINITO
                // Rompemos el punto crítico donde la esfera y el aro cancelaban fuerzas en cero.
                // =========================================================================
                // Choque directo borde Canasta Izquierda (Zonas críticas absolutas de peligro)
                if (Math.abs(p.y - canastaIzqY) < p.radio) {
                    if (Math.abs(p.x - 20f) < p.radio) {
                        p.y = canastaIzqY - p.radio
                        p.vy *= -0.5f
                        // Impulsión artificial dictatorial
                        p.vx = if (p.x < 20f) -4f else 4f
                    } else if (Math.abs(p.x - (20f + anchoCanasta)) < p.radio) {
                        p.y = canastaIzqY - p.radio
                        p.vy *= -0.5f
                        p.vx = if (p.x < 20f + anchoCanasta) -4f else 4f
                    }
                }

                // Choque directo borde Canasta Derecha
                val inicioDer = width - 20f - anchoCanasta
                val finDer = width - 20f
                if (Math.abs(p.y - canastaDerY) < p.radio) {
                    if (Math.abs(p.x - inicioDer) < p.radio) {
                        p.y = canastaDerY - p.radio; p.vy *= -0.5f
                        p.vx = if (p.x < inicioDer) -4f else 4f
                    } else if (Math.abs(p.x - finDer) < p.radio) {
                        p.y = canastaDerY - p.radio; p.vy *= -0.5f
                        p.vx = if (p.x < finDer) -4f else 4f
                    }
                }
            }
        }
    }
}