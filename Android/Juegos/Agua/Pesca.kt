/**
 * =========================================================================================
 * ARCHIVO: Pesca.kt
 * =========================================================================================
 * PROPÓSITO:
 * Implementar una simulación de captura y amarre de entes en base a proximidad.
 *
 * QUÉ HACE EL CÓDIGO:
 * Configura ganchos estacionarios que actúan mediante atracción pseudo-magnética.
 * Usa lógica de colisión avanzada basada en áreas y rangos de distancias (en vez de rectángulos
 * duros), de forma que un pez se considerará enganchado si pasa, a gran velocidad o lenta,
 * por un área radialmente favorable y afín a un gancho.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Generación Procedural de Vectores: Usando `Path` y uniéndolo a la variable dinámica
 * del tamaño y rotación del pez, creamos colas triangulares variables.
 * 2. Campo Magnético de Cercanía (Detección Radial): Calculamos no solo colisión simple de
 * caja a caja (AABB), sino una trampa de proximidad en un radio de acción concreto
 * usando Matemáticas Puras (Pitágoras).
 * 3. Cierre visual de figuras geométricas con `close()` para rellenar formas poligonales.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * Entorno simulado de pesca lúdica y captura automática basado en áreas de influencia radiales.
 *
 * @param context El ecosistema nativo general.
 */
class JuegoPescarView(context: Context) : JuegoAguaBase(context) {

    /** Pincel dinámico reservado íntegramente a perfilar y pigmentar los mamíferos y escamas acuáticas. */
    private val paintPez = Paint()

    /** Pincel macizo oscuro orientado a la forja geométrica visual de los palos de caña y ganchos finales en herradura. */
    private val paintGancho = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }

    /** Repositorio de coordenadas precalculadas conteniendo las ubicaciones estáticas X de la maquinaria pescadora. */
    private val ganchosX = mutableListOf<Float>()
    /** Límite Y donde terminan las cuerdas y nace la trampa herradural (Gancho final de pesca). */
    private var ganchosY = 0f

    /** Estructura de Listado que provee una inmensa gama de vivos colores coralinos. */
    private val coloresPeces = listOf(
        Color.parseColor("#D50000"), Color.parseColor("#C51162"), Color.parseColor("#AA00FF"),
        Color.parseColor("#6200EA"), Color.parseColor("#304FFE"), Color.parseColor("#2962FF"),
        Color.parseColor("#00B8D4"), Color.parseColor("#00BFA5"), Color.parseColor("#00C853"),
        Color.parseColor("#64DD17"), Color.parseColor("#AEEA00"), Color.parseColor("#FFD600"),
        Color.parseColor("#FFAB00"), Color.parseColor("#FF6D00"), Color.parseColor("#DD2C00")
    )

    init {
        // Redefinimos un tono inferior cristalino (Azul turquesa océano lúdico).
        paintBase.color = Color.parseColor("#2196F3")
    }

    /**
     * Motor de escalado y siembra posicional basándose en los anchos concretos dictados por la pantalla real del dispositivo.
     *
     * @param ancho Resolución final de lectura máxima lateral.
     * @param alto Resolución final extrema hacia el abismo.
     */
    override fun inicializarNivel(ancho: Int, alto: Int) {
        ganchosY = alto * 0.25f
        ganchosX.clear()

        // Distribuimos equitativamente los 3 anzuelos a lo largo del eje horizontal en cuotas perfectas de 25%.
        ganchosX.add(ancho * 0.25f)
        ganchosX.add(ancho * 0.5f)
        ganchosX.add(ancho * 0.75f)

        objetosFlotantes.clear()
        puntuacion = 0

        // Invocamos un cardumen activo surtido de 12 peces diversos.
        for (i in 0 until 12) {
            generarNuevoObjeto()
        }
    }

    /**
     * Fábrica de biodiversidad. Instancia aleatoriamente cada elemento con una corpulencia (radio) imprevisible y peculiar.
     */
    override fun generarNuevoObjeto() {
        val posX = (Math.random() * (width - 100) + 50).toFloat()
        val posY = (Math.random() * (height * 0.3f) + height * 0.5f).toFloat()

        objetosFlotantes.add(
            ObjetoFlotante(
                x = posX,
                y = posY,
                // Mutación genética simulada dotando al elemento de un engorde randomizado paramétrico.
                radio = (35..55).random().toFloat(),
                color = coloresPeces.random()
            )
        )
    }

    /**
     * Motor Gráfico de renders encadenados para elementos vivos mutables y maquinaria inamovible (Ganchos).
     *
     * @param canvas Recipiente final del pintado fotogramétrico.
     */
    override fun dibujarJuego(canvas: Canvas) {
        // Línea vertical que desciende como hilo grueso pescador unida a un barrido cóncavo (Arco de 180º).
        for (gx in ganchosX) {
            canvas.drawLine(gx, 0f, gx, ganchosY, paintGancho)
            canvas.drawArc(gx - 20f, ganchosY, gx + 20f, ganchosY + 40f, 0f, 180f, false, paintGancho)
        }

        for (p in objetosFlotantes) {
            paintPez.color = p.color

            // Óvalo ensanchado que compone la zona abultada vital principal del cuerpo o espina dorsal.
            canvas.drawOval(p.x - p.radio * 1.5f, p.y - p.radio, p.x + p.radio * 1.5f, p.y + p.radio, paintPez)

            // Creación instantánea por fotograma de un diseño algorítmico poligonal para aleta caudal.
            val pathCola = Path()
            pathCola.moveTo(p.x - p.radio * 1.2f, p.y) // Pilar interior base
            pathCola.lineTo(p.x - p.radio * 2.8f, p.y - p.radio * 1.2f) // Cima superior de la aleta trasera
            pathCola.lineTo(p.x - p.radio * 2.8f, p.y + p.radio * 1.2f) // Punta inferior sur de la aleta
            pathCola.close() // Orden terminante matemática para cerrar polígonos irregulares solidificándolos.

            canvas.drawPath(pathCola, paintPez)
        }
    }

    /**
     * Bucle analizador de atracción invisible y engarzado físico estricto. Audita incesantemente los umbrales esféricos de cercanía.
     */
    override fun comprobarLogicaEspecifica() {
        for (p in objetosFlotantes) {

            // SI ESTÁ CAPTURADO E ILESO (Enganchado con éxito supremo)
            if (p.atrapado) {
                p.vy = 0f
                p.vx = 0f
                // Sincronización fija vertical atándolo visualmente al tope de la curvatura del anzuelo
                p.y = ganchosY + 20f

                // Operador lógico Kotliniano avanzado `minByOrNull` para hallar la base X más óptima del gancho magnético más próximo.
                val ganchoCercano = ganchosX.minByOrNull { Math.abs(it - p.x) } ?: p.x
                p.x = ganchoCercano
                continue
            }

            // =========================================================================
            // LÓGICA DE CAPTURA EUCLIDIANA INCONDICIONAL
            // Supera a los choques básicos resolviendo encuentros fluidos sin importar p.vy
            // =========================================================================
            // Sumamos una tolerancia artificial extrema de 35px extra facilitando el nivel de frustración del jugador
            val radioCaptura = p.radio + 35f

            for (gx in ganchosX) {
                // Coordenadas absolutas del epicentro de influencia gravitacional (el gancho per se).
                val centroAnzueloX = gx
                val centroAnzueloY = ganchosY + 20f

                // Formulación Pitagórica analítica para distancia recta infalible
                val dx = p.x - centroAnzueloX
                val dy = p.y - centroAnzueloY
                val distancia = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                // Dictamen definitivo de captura inamovible
                if (distancia < radioCaptura) {
                    p.x = centroAnzueloX
                    p.y = centroAnzueloY
                    p.vx = 0f
                    p.vy = 0f
                    p.atrapado = true

                    break // Rompe la iteración sobre ganchos adicionales, el destino está sellado y el pez capturado.
                }
            }
        }
    }
}