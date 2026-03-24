/**
 * =========================================================================================
 * ARCHIVO: Pacman.kt
 * =========================================================================================
 * PROPÓSITO:
 * Minijuego arcade re-conceptualizado al estilo de juguetes mecánicos de agua clásico.
 *
 * QUÉ HACE EL CÓDIGO:
 * Configura una figura circular a la que "le falta un trozo" matemáticamente calculado.
 * Comprueba las interacciones vectoriales usando el cálculo riguroso del Teorema de Pitágoras
 * para la interacción física entre las esferas alimenticias y el borde esférico general.
 * Incluye mecánicas de ingesta y rebotes geométricos polarizados.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Trigonometría en Videojuegos: Usamos el Teorema de Pitágoras clásico matemático
 * (A² + B² = C²) para hallar la distancia euclidiana entre la bola flotante y el
 * centro geométrico del Pacman y saber si colisionan con una perfección circular.
 * 2. Normalización de Vectores: Convertir la dirección pura del choque en valores (Nx, Ny)
 * entre 0 y 1 para aplicar rebotes esféricos precisos e hiper-realistas.
 * 3. drawArc avanzado: Cómo barrer un círculo desde un grado de inicio para simular
 * una figura tipo "tarta a la que le falta una porción", dando la apariencia de "Boca".
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.sqrt

/**
 * Módulo especializado que implementa la ingesta de esferas mediante algoritmos radiales pitagóricos.
 *
 * @param context Vínculo del framework Android.
 */
class JuegoPacmanView(context: Context) : JuegoAguaBase(context) {

    /** Configuración del Pincel macizo que da forma corpórea al ente central (Pacman) en púrpura puro. */
    private val paintPacman = Paint().apply {
        color = Color.parseColor("#9C27B0") // Morado intenso
        style = Paint.Style.FILL
    }

    /** Pincel reciclable asignado temporalmente a cada minúscula porción alimenticia visual. */
    private val paintComida = Paint()

    /** Centralización X del devorador mecánico principal. */
    private var pacmanX = 0f
    /** Centralización Y del devorador mecánico principal. */
    private var pacmanY = 0f
    /** Módulo radial de la criatura para ejecutar barridos y áreas de contención. */
    private val pacmanRadio = 220f

    /** Estructura generadora de paleta multicolor para diversificar ópticamente la zona inferior del nivel. */
    private val coloresComida = listOf(
        Color.parseColor("#FF0000"), Color.parseColor("#FF5252"), Color.parseColor("#FF4081"),
        Color.parseColor("#E040FB"), Color.parseColor("#AA00FF"), Color.parseColor("#651FFF"),
        Color.parseColor("#3D5AFE"), Color.parseColor("#2979FF"), Color.parseColor("#00B0FF"),
        Color.parseColor("#00E5FF"), Color.parseColor("#1DE9B6"), Color.parseColor("#00E676"),
        Color.parseColor("#76FF03"), Color.parseColor("#C6FF00"), Color.parseColor("#FFEA00"),
        Color.parseColor("#FFC400"), Color.parseColor("#FF9100"), Color.parseColor("#FF3D00")
    )

    init {
        // Redefinimos un tono inferior que haga armonía naturalista (Verde clorofila).
        paintBase.color = Color.parseColor("#4CAF50")

        // Nombre del archivo a reproducir como música de fondo
        idMusicaFondo = R.raw.musica_pacman
    }

    /**
     * Motor de escalado para sembrar estratégicamente la entidad comilón a gran altitud.
     *
     * @param ancho Resolución del margen derecho.
     * @param alto Resolución del margen subterráneo.
     */
    override fun inicializarNivel(ancho: Int, alto: Int) {
        pacmanX = ancho / 2f
        pacmanY = alto * 0.4f

        objetosFlotantes.clear()
        puntuacion = 0

        // Invocamos un gran enjambre nutrido por 22 esferas independientes para elevar la densidad lúdica.
        for (i in 0 until 22) {
            generarNuevoObjeto()
        }
    }

    /**
     * Módulo responsable de sembrar componentes orgánicos básicos a diversas latitudes subterráneas.
     */
    override fun generarNuevoObjeto() {
        val posX = (Math.random() * (width - 100) + 50).toFloat()
        val posY = (Math.random() * (height * 0.3f) + height * 0.5f).toFloat()

        objetosFlotantes.add(
            ObjetoFlotante(x = posX, y = posY, radio = 30f, color = coloresComida.random())
        )
    }

    /**
     * Componente central del trazado y simulación de porciones angulares.
     *
     * @param canvas Recipiente final del pintado fotogramétrico.
     */
    override fun dibujarJuego(canvas: Canvas) {
        // Instrucción mágica gráfica: Partiendo del meridiano 315º, dibujamos y barremos una estela
        // hasta conformar 270 grados en total. Esto obvia y excluye automáticamente el fragmento restante
        // de 90 grados apuntando al noroeste superior conformando un diseño clásico de "Boca abierta".
        canvas.drawArc(
            pacmanX - pacmanRadio, pacmanY - pacmanRadio,
            pacmanX + pacmanRadio, pacmanY + pacmanRadio,
            315f, 270f, true, paintPacman
        )

        for (p in objetosFlotantes) {
            paintComida.color = p.color
            canvas.drawCircle(p.x, p.y, p.radio, paintComida)
        }
    }

    /**
     * Cerebro matemático. Mapea, escanea y detecta intrusiones corporales de las masas menores usando
     * fórmulas de distancia clásica y un vector normalizador que reorienta y distribuye fuerzas elásticas.
     */
    override fun comprobarLogicaEspecifica() {
        for (p in objetosFlotantes) {

            // SI MURIÓ: Se internalizó en las entrañas de la gran bola.
            if (p.atrapado) {
                p.vx = 0f
                p.vy = 0f
                // Aplicamos Lerp (Interpolación Lineal del 80%). Crea el efecto subconsciente de ser
                // digerido arrastrándose progresivamente y sin brusquedad perfecta hacia el ombligo del devorador.
                p.x = pacmanX + (p.x - pacmanX) * 0.8f
                p.y = pacmanY + (p.y - pacmanY) * 0.8f
                continue
            }

            // =========================================================================
            // LÓGICA DE PITÁGORAS ESTRUCTURAL
            // La suma de catetos cuadrados en raíz calcula el vector de hipotenusa.
            // =========================================================================
            val dx = p.x - pacmanX
            val dy = p.y - pacmanY
            // `sqrt` (Square Root) resuelve de forma absoluta la distancia pura libre de obstáculos y cuadrantes.
            val distancia = sqrt(dx * dx + dy * dy)

            // Activación condicional: Comienza la etapa de colisión de envolturas tangentes.
            if (distancia < pacmanRadio + p.radio) {

                // Inspección geométrica del área de ingreso orgánico ("Boca") basado en la condición previa dibujada.
                val dentroDeLaBoca = dy < 0 && Math.abs(dx) < -dy * 1.2f

                if (dentroDeLaBoca) {
                    p.vx *= 0.5f // Amortiguación inmediata al pisar la garganta.
                    p.vy *= 0.5f

                    // Adición micro-magnética estomacal, ejerciendo una atracción paulatina y engañosa.
                    p.vx += -dx * 0.1f
                    p.vy += -dy * 0.1f

                    // Módulo de sanción irremediable. Si su incursión en la geometría alcanza el 60% interior.
                    if (distancia < pacmanRadio * 0.6f) {
                        p.atrapado = true
                    }
                } else if (!p.atrapado) {
                    // =========================================================================
                    // EL FENÓMENO DE NORMALIZACIÓN (ALGEBRA LINEAL FÍSICA)
                    // =========================================================================
                    // Transformar valores desequilibrados largos en direcciones limpias base (0 a 1).
                    val nx = dx / distancia
                    val ny = dy / distancia

                    // Escupimos o apartamos al elemento invasor exactamente hasta el punto visual de no-tangencia.
                    p.x = pacmanX + nx * (pacmanRadio + p.radio)
                    // Transferencia energética por empuje repulsivo y naturalizado.
                    p.vx += nx * 2f
                    p.vy += ny * 2f
                }
            }
        }
    }
}