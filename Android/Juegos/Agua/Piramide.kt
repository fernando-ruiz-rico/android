/**
 * =========================================================================================
 * ARCHIVO: Piramide.kt
 * =========================================================================================
 * PROPÓSITO:
 * Simulador de apilamiento vertical táctico en plataformas jerárquicas predispuestas.
 *
 * QUÉ HACE EL CÓDIGO:
 * Configura una base inamovible de "Andamios" e inyecta elementos constructivos irregulares
 * (Triángulos isósceles dibujados en Path puro). Evalúa la sostenibilidad y estabilidad
 * del polígono comprobando matemáticamente que su centro de masa fundamental repose por
 * completo dentro del andamio. Si hay desviación o desborde marginal, promueve mecánicamente
 * el resbalón lateral y castiga precipitando la figura al abismo.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Instanciación Eficiente en el Loop: Instanciamos un solo Path en toda la clase
 * en lugar de generar memoria basura creándolo 60 veces por segundo. Usamos `reset()`
 * para limpiarlo y redibujarlo. Excelente para la memoria y vida de la batería.
 * 2. Lógica de Apoyo Horizontal (Gravedad Condicional): Algoritmo de plataformas en
 * videojuegos de vista lateral. El objeto frena en seco cuando su "suelo lógico"
 * coincide con el Y de la línea pintada.
 * 3. Deriva por desequilibrio (Edge-Sliding): Código preventivo sofisticado. Si el
 * centro de gravedad (X) del objeto se sitúa fuera del perímetro de la plataforma
 * pero su borde logra tocarla tangencialmente, forzamos un deslizamiento (`Vx +=`)
 * para que resbale y caiga de forma visualmente lógica y realista, evitando flotabilidad ilusoria.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * Minijuego de arquitectura, exactitud geométrica y castigo inercial.
 *
 * @param context Enlace subyacente de comunicación de eventos al ecosistema Android.
 */
class JuegoPiramideTriangulosView(context: Context) : JuegoAguaBase(context) {

    /** Pincel dinámico camaleónico reservado para ilustrar los macizos vértices de construcción. */
    private val paintTriangulo = Paint()

    /** Pincel arquitectónico oscuro encargado de estructurar cimientos recios de soporte y bordes agradables estéticamente. */
    private val paintPlataforma = Paint().apply {
        color = Color.parseColor("#8D6E63")
        strokeWidth = 15f
        strokeCap = Paint.Cap.ROUND // Los finales cortantes abruptos quedan sellados mediante cilindros redondeados orgánicos.
    }

    /** * VECTOR ESTATAL ÓPTIMO DE HARDWARE. Al instanciarlo fuera del Loop de fotogramas,
     * mitigamos miles de accesos de recolección de memoria residual por el recolector de basura JVM,
     * consiguiendo unos 60FPS intachables.
     */
    private val pathTriangulo = Path()

    /**
     * Estructura de encapsulación de datos espaciales precalculados. Un andamio de madera flotante y plano.
     * @property xIzq Eje vertical izquierdo donde se origina la franja sólida perimetral de la tabla.
     * @property xDer Eje vertical oriental final del travesaño sólido de contención.
     * @property y Límite superior transversal horizontal donde es válido edificar componentes pesados.
     */
    data class Plataforma(val xIzq: Float, val xDer: Float, val y: Float)

    /** Colección privada dinámica guardián de todas las coordenadas vigentes del entorno topológico de andamios. */
    private val plataformas = mutableListOf<Plataforma>()

    /** Estructura de Listado que provee de una riquísima cromatografía variada. */
    private val coloresTriangulos = listOf(
        Color.parseColor("#F44336"), Color.parseColor("#E91E63"), Color.parseColor("#9C27B0"),
        Color.parseColor("#673AB7"), Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
        Color.parseColor("#00BCD4"), Color.parseColor("#009688"), Color.parseColor("#4CAF50"),
        Color.parseColor("#8BC34A"), Color.parseColor("#CDDC39"), Color.parseColor("#FFEB3B"),
        Color.parseColor("#FFC107"), Color.parseColor("#FF9800"), Color.parseColor("#FF5722")
    )

    init {
        // Redefinimos un tono inferior amarillo-ámbar cálido en sustitución de la matriz abstracta general.
        paintBase.color = Color.parseColor("#FFC107")

        // Intervenimos los registros pulsadores cromáticos generales por defecto dotándolos de un vibrante verdor arbóreo.
        colorBotonIzquierdoNormal = Color.parseColor("#4CAF50")
        colorBotonDerechoNormal = Color.parseColor("#4CAF50")

        // Nombre del archivo a reproducir como música de fondo
        idMusicaFondo = R.raw.musica_piramide
    }

    /**
     * Gestor paramétrico distribuidor. Reparte plataformas estáticas basadas rígidamente en la matriz visual X e Y absolutas calculadas.
     *
     * @param ancho Resolución métrica lateral total asimilada del móvil.
     * @param alto Resolución métrica descendiente asimilada por hardware.
     */
    override fun inicializarNivel(ancho: Int, alto: Int) {
        plataformas.clear()
        // Andamio líder de la pirámide central estratosférica
        plataformas.add(Plataforma(ancho * 0.3f, ancho * 0.7f, alto * 0.25f))
        // Basamentos medianos a babor y estribor
        plataformas.add(Plataforma(ancho * 0.15f, ancho * 0.45f, alto * 0.45f))
        plataformas.add(Plataforma(ancho * 0.55f, ancho * 0.85f, alto * 0.45f))

        objetosFlotantes.clear()
        puntuacion = 0

        // Invocamos un lote maestro de 12 bloques isósceles macizos para desafiar al constructor.
        for (i in 0 until 12) {
            generarNuevoObjeto()
        }
    }

    /**
     * Forjador estático de un nuevo prisma poligonal activo al circuito gravitatorio principal incesante de caídas.
     */
    override fun generarNuevoObjeto() {
        val posX = (Math.random() * (width - 100) + 50).toFloat()
        val posY = (Math.random() * (height * 0.3f) + height * 0.5f).toFloat()

        objetosFlotantes.add(
            ObjetoFlotante(x = posX, y = posY, radio = 45f, color = coloresTriangulos.random())
        )
    }

    /**
     * Entramado visual percutivo primario. Ejecuta las directrices de hardware de dibujo purista, renderizando un sinfín ininterrumpido a 60 hz.
     *
     * @param canvas Recipiente final del pintado fotogramétrico.
     */
    override fun dibujarJuego(canvas: Canvas) {
        // Renderizado inicial purista y simplista del esqueleto pardo rígido en suspensión flotante geométrica horizontal paralela a la base.
        for (plat in plataformas) {
            canvas.drawLine(plat.xIzq, plat.y, plat.xDer, plat.y, paintPlataforma)
        }

        for (p in objetosFlotantes) {
            paintTriangulo.color = p.color

            // BORRADO VITAL y RESIDUAL. Vaciamos las memorias de puntos de cruce (coordenadas antiguas trazadas preteritamente) y renombramos de cero la hoja.
            pathTriangulo.reset()
            // Inyección topológica progresiva desde la aguja hasta sus raíces estabilizadoras de suelo
            pathTriangulo.moveTo(p.x, p.y - p.radio) // Aguja Vértice Cima Norte
            pathTriangulo.lineTo(p.x - p.radio, p.y + p.radio) // Zapata Sur Occidental Izquierda
            pathTriangulo.lineTo(p.x + p.radio, p.y + p.radio) // Zapata Sur Oriental Derecha
            // Cierre coercitivo forzoso para encapsular pintura sin goteo gráfico pixelar de fuga
            pathTriangulo.close()

            canvas.drawPath(pathTriangulo, paintTriangulo)
        }
    }

    /**
     * Cerebro de colisión arquitectónico avanzado. Emite juicios sumarios para asentar sólidas bases de edificación penalizando severamente bordes inexactos.
     */
    override fun comprobarLogicaEspecifica() {
        for (p in objetosFlotantes) {
            for (plat in plataformas) {
                // EXAMEN PRIMARIO ESPACIAL VERTICAL Y DE VELOCIDADES DESCENDENTES Y GRAVITACIONALES
                // Si la figura desciende inexorable y su zócalo base roza levemente la franja etérea permitida por el marco perimetral...
                if (p.vy > 0 && p.y + p.radio > plat.y - 15f && p.y + p.radio < plat.y + 15f) {

                    // EXAMEN SECUNDARIO ESTRICTO DE ESTABILIZACIÓN Y PLOMADA ESTRUCTURAL IDEAL
                    // Contrastamos si el vientre medio gravitatorio del prisma ha superado al completo holgadamente los lindes del tablón.
                    if (p.x > plat.xIzq && p.x < plat.xDer) {

                        // Sentencia aprobatoria de asiento fijo sólido paralizando dinámicas y rotulándolo como triunfante.
                        p.y = plat.y - p.radio
                        p.vy = 0f
                        p.vx *= 0.5f // Amortiguación inmediata al golpear sólido inamovible (fricción en seco)
                        p.atrapado = true

                    } else if (p.x > plat.xIzq - p.radio && p.x <= plat.xIzq) {
                        // Veredicto Deficiente: Centro plomada yaciendo fuera, pero talón percutiendo inestable esquina izquierda...
                        // Castigo físico propulsado empujando y desalojando virulentamente masa al vacío precipitado por gravedad negativa acumulada inercial.
                        p.vx -= 4f

                    } else if (p.x >= plat.xDer && p.x < plat.xDer + p.radio) {
                        // Veredicto Deficiente: Plomada yaciendo extramuros orientales apoyado de refilón...
                        // Castigo equivalente en dirección diestra propulsiva y caída terminal llovida.
                        p.vx += 4f
                    }
                }
            }
        }
    }
}