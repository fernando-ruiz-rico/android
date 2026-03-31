/**
 * =========================================================================================
 * ARCHIVO: Base.kt
 * =========================================================================================
 * PROPÓSITO:
 * Servir como motor físico y base gráfica compartida (clase padre) para todos los minijuegos.
 *
 * QUÉ HACE EL CÓDIGO:
 * Define un `SurfaceView` que arranca un hilo secundario (`Thread`) dedicado a ejecutar un bucle
 * ininterrumpido a gran velocidad. Este bucle aplica leyes físicas (gravedad, agua, inercia)
 * a todos los objetos del juego y dibuja la interfaz común como el plástico de colores,
 * la zona del agua, el marcador y los botones multitáctiles.
 * Se integra `SensorEventListener` para captar la inclinación física del teléfono y aplicar
 * fuerzas laterales y verticales a los objetos, permitiendo al jugador mover el dispositivo
 * para ayudar a las piezas a llegar a su objetivo sin salirse una vez atrapadas.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Herencia y Clases Abstractas: Construimos un motor físico genérico (`JuegoAguaBase`)
 * para no tener que repetir las mecánicas del agua y botones en cada minijuego.
 * 2. El Game Loop (Bucle de Juego) y Threads: Cómo evitar congelar la interfaz gráfica del
 * móvil usando un hilo de ejecución secundario (Thread) a 60 FPS ininterrumpidos.
 * 3. Físicas mediante Velocidad (Vx, Vy): Simulamos la gravedad, la flotabilidad del agua
 * y la fricción multiplicando variables matemáticas fotograma a fotograma.
 * 4. Detección Multitáctil (Multitouch): Leer las pulsaciones de la pantalla interceptando
 * el evento `onTouchEvent` para permitir pulsar los dos chorros a la vez.
 * 5. Manipulación de Color Dinámica: Algoritmo HSV para oscurecer el color del botón al
 * pulsarlo fotograma a fotograma, logrando retroalimentación visual.
 * 6. Integración de Audio: Uso de `MediaPlayer` para BGM y `SoundPool` para SFX sin latencia.
 * 7. Sensores de Hardware (Acelerómetro): Integración con `SensorManager` para leer los
 * ejes espaciales del teléfono en tiempo real y transformarlos en vectores de fuerza.
 * 8. Dinámica de Fluidos y Efectos Visuales: Uso de "Velocidad Terminal", cámara de aire,
 * amortiguación de colisiones, superficie del agua inclinable y oleaje interactivo.
 * 9. Shaders y Degradados: Implementación de `LinearGradient` para dar una sensación
 * volumétrica real al agua, oscureciendo el fondo para generar profundidad sin gastar memoria.
 * 10. Saneamiento Post-Colisión (Resting Contact): Sistema anti-vibración que mata inercias
 * residuales para evitar el "jittering" e impedir que los objetos queden atascados.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Representa matemáticamente cualquier pieza jugable y movible (Aro, Pelota, Pez...).
 * Agrupa su posición espacial estricta, su inercia temporal y su estado lógico en el juego.
 *
 * @property x Coordenada horizontal actual respecto al lienzo.
 * @property y Coordenada vertical actual respecto al lienzo.
 * @property vx Velocidad inercial lateral aplicable en cada ciclo (Vector X).
 * @property vy Velocidad inercial vertical. Si es positiva cae hacia abajo, si es negativa es impulsada arriba (Vector Y).
 * @property radio Tamaño físico circular del objeto que usamos para detectar choques y rebotes.
 * @property color Color hexadecimal específico en el que será pintado el objeto.
 * @property atrapado Booleano. Si se vuelve `true`, indicamos que el objeto logró su meta y dejamos de aplicarle chorros.
 * @property contabilizado Booleano crítico. Evita que la app sume repetidos puntos en el marcador por un mismo objeto.
 */
data class ObjetoFlotante(
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var radio: Float = 30f,
    var color: Int = Color.RED,
    var atrapado: Boolean = false,
    var contabilizado: Boolean = false
)

/**
 * Plantilla base abstracta de nuestro motor de juego 2D customizado.
 * Heredar de `SurfaceView` y `SurfaceHolder.Callback` proporciona una superficie de hardware optimizada.
 * Implementar `Runnable` garantiza que esta clase posee el código pesado que ejecutaremos en paralelo.
 * Implementar `SensorEventListener` permite a esta vista escuchar directamente la placa base del móvil.
 *
 * @param context El puente hacia el sistema principal de la App Android.
 */
abstract class JuegoAguaBase(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable, SensorEventListener {

    /**
     * Colección en la que cada minijuego hijo guardará sus piezas instanciadas.
     * Su ámbito `protected` asegura que solo este motor y sus minijuegos derivados pueden leer o tocar esto.
     */
    protected val objetosFlotantes = mutableListOf<ObjetoFlotante>()

    /** Marcador general dinámico que cuenta de manera unificada cuántas piezas ha asegurado el usuario. */
    protected var puntuacion = 0

    // ================================= PINCELES (PAINT) =================================
    // La instanciación de objetos de pintura es pesada computacionalmente. Aquí se crean
    // y afinan una sola vez al inicio en lugar de en cada uno de los 60 fotogramas por segundo.

    /** Pincel para el cuerpo principal del agua estancada. Recibirá su degradado 3D en surfaceCreated(). */
    protected val paintAgua = Paint().apply {
        // El Dithering (Entramado) es crucial en los degradados Android para evitar el "Banding" (líneas tipo escalón).
        // Obliga al motor gráfico a mezclar los píxeles adyacentes de forma mucho más suave.
        isDither = true
    }

    /** Pincel estático que pinta toda la sección baja (el motor y chasis) del juguete. Color menta pastel: relajante y distintivo. */
    protected val paintBase = Paint().apply { color = Color.parseColor("#4DB6AC") }
    /** Pincel dinámico reservado para dibujar los círculos interactivos que ejercen de pulsadores/botones. */
    protected val paintBoton = Paint()
    /** Pincel configurado a gran escala gráfica para dibujar los Emojis interpretándolos como letras de texto. */
    protected val paintTextoBoton = Paint().apply { textSize = 100f }

    /** Pincel con un 70% de opacidad. Se usa para las cartelas del panel inferior que mejoran la lectura. Azul claro a petición. */
    private val paintFondoUI = Paint().apply { color = Color.parseColor("#B3B3E5FC") }

    /** Pincel meticulosamente alineado en modo 'CENTER' para facilitar el pintado y auto-centrado del marcador de puntos. */
    protected val paintMarcador = Paint().apply {
        color = Color.BLACK
        textSize = 70f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    /** Estado de color base para el botón izquierdo. Es configurable por los hijos derivados. */
    protected var colorBotonIzquierdoNormal: Int = Color.parseColor("#FF9800")
    /** Estado de color base para el botón derecho. */
    protected var colorBotonDerechoNormal: Int = Color.parseColor("#FF9800")

    // ================================= OPTIMIZACIONES DE MEMORIA (0 ALLOCATIONS) =================================
    // Pre-reservamos espacios de memoria que antes se destruían y creaban 60 veces por segundo, ahogando al móvil.
    private val rectFondoMarcador = RectF()
    private val arrayHsvTemporal = FloatArray(3)
    private var textoPuntuacionCache = "0"
    private var puntuacionAnterior = -1
    private var coloresBotonesCalculados = false
    private var colorIzqOscuro = 0
    private var colorDerOscuro = 0

    // Matriz de transformación para rotar el degradado sin recrear objetos
    private val matrixDegradado = Matrix()

    // Elementos geométricos cacheados para renderizar el mar dinámico
    private val pathCuerpoAgua = Path()

    // ================================= CONSTRAINTS FÍSICAS DE FLUIDOS =================================
    /** Fuerza continua aplicada que arrastra inexorablemente los cuerpos hacia abajo cada fotograma. */
    private val gravedad = 0.8f
    /** Fuerza contraria fija que imita la densidad del medio acuoso (flotar suavemente). Resultado neto: 0.3f hacia abajo */
    private val flotabilidad = -0.5f

    /** VISCOSIDAD: Variable de freno multiplicativo más severo. El agua frena rápidamente los objetos. */
    private val friccionAgua = 0.92f

    /** VELOCIDAD TERMINAL: Por mucho que agites el móvil, el agua impide que las piezas vayan a velocidades balísticas. */
    private val velocidadMaxima = 14f

    /** Nivel de altura predeterminado del agua (más lleno, dejando menos aire arriba) */
    private val nivelAguaCentroBase = 80f

    // Variables interactivas del agua visual
    private var tiempoOlas = 0f
    private var energiaAgua = 0f // Almacena cuánta "violencia" u oleaje hay activo

    /** Registro booleano en memoria temporal sobre el estado físico del dedo en el cuadrante de botón izquierdo. */
    private var botonIzqPulsado = false
    /** Registro booleano en memoria temporal sobre el estado físico del dedo en el cuadrante de botón derecho. */
    private var botonDerPulsado = false

    /**
     * `Volatile` comunica a la máquina virtual (JVM) que esta variable debe guardarse en
     * la memoria RAM principal, de modo que cualquier alteración se registre instantáneamente
     * impidiendo choques o parálisis entre los hilos de trabajo.
     */
    @Volatile private var jugando = false

    /** El subproceso obrero (worker thread) encargado de la carga bruta gráfica continua del Game Loop. */
    private var hiloJuego: Thread? = null

    // ================================= MOTORES DE AUDIO =================================
    /** Reproductor para la música de fondo (BGM). Ideal para audios largos. */
    private var reproductorMusica: MediaPlayer? = null

    /** Piscina de sonidos (SFX) para efectos instantáneos como las burbujas o botones. */
    private var motorEfectos: SoundPool? = null

    /** ID numérico asignado por Android al sonido del botón una vez cargado en RAM. */
    private var idSonidoBurbuja: Int = 0

    /** Variable que define qué canción sonará. Por defecto es 0 (sin música), cada minijuego la configurará. */
    protected var idMusicaFondo: Int = 0

    // ================================= ACELERÓMETRO =================================
    /** Puerta de enlace principal con los servicios de hardware del dispositivo. */
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /** Representación en código del hardware físico del sensor de aceleración. */
    private val acelerometro: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /** Fuerza lateral calculada generada al volcar el móvil hacia la izquierda o derecha. */
    private var inclinacionX = 0f

    /** Fuerza vertical calculada generada al inclinar el móvil hacia nosotros (levantarlo) o alejarlo (tumbarlo). */
    private var inclinacionY = 0f
    // ====================================================================================

    init {
        // Vinculación obligatoria. Instruimos a la superficie gráfica que esta misma clase se hará cargo de eventos vitales.
        holder.addCallback(this)

        // === CONFIGURACIÓN DE SOUNDPOOL PARA LOS BOTONES ===
        val atributosAudio = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        motorEfectos = SoundPool.Builder()
            .setMaxStreams(5) // Permite reproducir hasta 5 sonidos simultáneos si pulsas rápido
            .setAudioAttributes(atributosAudio)
            .build()

        // Cargamos el efecto de sonido en memoria (espera encontrar res/raw/sonido_boton.mp3)
        try {
            idSonidoBurbuja = motorEfectos?.load(context, R.raw.sonido_boton, 1) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * MÉTODO OBLIGATORIO. El hijo (minijuego) lo sobrescribirá para declarar sus canastas y crear sus objetos.
     * @param ancho Ancho real medido de la pantalla.
     * @param alto Alto real medido de la pantalla.
     */
    abstract fun inicializarNivel(ancho: Int, alto: Int)

    /** MÉTODO OBLIGATORIO. Aquí el hijo estampará en el lienzo todos los componentes especiales de su nivel. */
    abstract fun dibujarJuego(canvas: Canvas)

    /** MÉTODO OBLIGATORIO. El hijo debe revisar fotograma a fotograma cuándo el jugador cumple las metas de juego. */
    abstract fun comprobarLogicaEspecifica()

    /** MÉTODO OBLIGATORIO. El hijo decidirá en qué coordenadas y con qué parámetros nace una pieza de repuesto. */
    abstract fun generarNuevoObjeto()

    /**
     * Responde a la señal del sistema operativo que indica que el rectángulo gráfico físico está montado
     * y listo para operar. Detona el motor arrancando el bucle paralelo.
     *
     * @param holder Representación interna de la pantalla.
     */
    override fun surfaceCreated(holder: SurfaceHolder) {

        // === CONFIGURAR DEGRADADO DE AGUA ===
        // Colores mucho más claros para dar una sensación de agua limpia, cristalina y luminosa.
        paintAgua.shader = LinearGradient(
            0f, 0f, 0f, height * 0.8f,
            Color.parseColor("#80B3E5FC"), // Superficie: Azul casi blanco, muy transparente y luminoso
            Color.parseColor("#E64FC3F7"), // Profundidad: Celeste brillante, alegre y relajante sin ser oscuro
            Shader.TileMode.CLAMP
        )

        inicializarNivel(width, height)
        jugando = true

        // === INICIAR LA MÚSICA DE FONDO DINÁMICA ===
        if (idMusicaFondo != 0) {
            try {
                reproductorMusica = MediaPlayer.create(context, idMusicaFondo)
                reproductorMusica?.isLooping = true // Que se repita infinitamente
                reproductorMusica?.setVolume(0.9f, 0.9f) // Volumen un poco más bajo
                reproductorMusica?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // === ACTIVAR ACELERÓMETRO ===
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        hiloJuego = Thread(this) // Se le asigna explícitamente el contexto de ejecución.
        hiloJuego?.start()
    }

    /**
     * Responde a la orden de destrucción temporal o minimización de la ventana. Detiene inmediatamente
     * todo esfuerzo del hilo para liberar memoria vital del dispositivo.
     *
     * @param holder Representación interna de la pantalla.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        jugando = false
        hiloJuego?.join() // Función de acoplamiento ordenado, previene cierres forzosos abruptos (crashes).

        // === LIBERAR MEMORIA DE LA MÚSICA ===
        reproductorMusica?.stop()
        reproductorMusica?.release()
        reproductorMusica = null

        // === DESACTIVAR ACELERÓMETRO ===
        sensorManager.unregisterListener(this)
    }

    /** Método complementario exigido por la interfaz `Callback`. Sin uso funcional en nuestro contexto específico. */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    /**
     * Cuando la vista gráfica es destruida por completo,
     * aprovechamos para liberar los efectos de sonido de la memoria RAM.
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        motorEfectos?.release()
        motorEfectos = null
    }

    /**
     * MÉTODO DE SENSOR: Se ejecuta de forma asíncrona docenas de veces por segundo
     * cada vez que la mano del usuario genera un micro-movimiento o cambia el ángulo del dispositivo móvil.
     *
     * @param event Contiene la información cruda devuelta por el hardware físico.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // event.values[0] mide el eje X del móvil: inclinar a la izquierda es positivo, a la derecha es negativo.
            // Como en nuestro Canvas la derecha aumenta la X, lo invertimos con un menos (-)
            // para que la inercia visual coincida exactamente con la gravedad física del mundo real.
            inclinacionX = -event.values[0]

            // event.values[1] mide el eje Y del móvil: ponerlo de pie es positivo, tumbarlo plano es negativo.
            // Lo usamos tal cual para que al levantar el móvil, los objetos tiendan a "caer" más rápido.
            inclinacionY = event.values[1]
        }
    }

    /** Método requerido por la interfaz de sensores, no necesitamos gestionar los cambios de precisión aquí. */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * LA ARQUITECTURA CRÍTICA: EL GAME LOOP CONTINUO.
     * Este entorno gira sobre sí mismo múltiples veces por segundo calculando los valores abstractos,
     * plasmándolos en el lienzo temporal bloqueado y escupiéndolos de golpe hacia la pantalla LCD.
     */
    override fun run() {
        while (jugando) {
            // Protección defensiva que aborta el fotograma si ocurre una desincronización de hardware o cierres veloces.
            if (!holder.surface.isValid) continue

            // 1. Fase de Lógica Matemática
            actualizarFisicas()

            // 2. Fase de Adquisición de Pintura. Capturamos el lienzo obligando a Android a no alterarlo.
            val canvas = holder.lockCanvas()
            if (canvas != null) {
                // Limpiamos el fondo con un blanco puro para que parezca un juguete muy iluminado
                canvas.drawColor(Color.parseColor("#FFFFFF"))

                // A. Renderizado Base - Agua Dinámica
                dibujarAguaDinamica(canvas)

                // B. Renderizado Detallado - Cedemos el control para que el hijo pinte el entorno y las bolas.
                dibujarJuego(canvas)

                // C. Renderizado Frontera (UI) - Dibujamos siempre el frontal plástico al final, por encima de todo.
                dibujarBotonesYBase(canvas)

                // 3. Fase de Presentación. Soltamos el lienzo para que Android proyecte los píxeles a la matriz de pantalla.
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    /**
     * Dibuja la superficie poligonal del agua. Reacciona al acelerómetro inclinándose
     * como agua real dentro de una pecera y genera ondas si hay energía en el tanque.
     */
    private fun dibujarAguaDinamica(canvas: Canvas) {
        tiempoOlas += 0.05f + (energiaAgua * 0.005f) // El agua fluye y ondula
        energiaAgua *= 0.96f // Fricción: La "violencia" del oleaje se calma sola progresivamente

        // Calcular el desnivel de inclinación del agua
        val diferenciaAlturaTilt = inclinacionX * -12f
        val limiteSuelo = height * 0.8f

        // ===== ROTACIÓN DEL DEGRADADO (SHADERS) =====
        // Al rotar el dispositivo, calculamos el ángulo trigonométrico de la superficie del agua
        // y aplicamos una transformación Matrix al shader para que la luz y sombra giren acorde al fluido.
        val anguloRad = kotlin.math.atan2((diferenciaAlturaTilt * 2f).toDouble(), width.toDouble())
        val anguloGrados = Math.toDegrees(anguloRad).toFloat()

        matrixDegradado.reset()
        matrixDegradado.setRotate(anguloGrados, width / 2f, nivelAguaCentroBase)
        paintAgua.shader.setLocalMatrix(matrixDegradado)

        // ===== MASA PRINCIPAL DE AGUA (SUPERFICIE INCLINADA Y DEGRADADA) =====
        pathCuerpoAgua.reset()

        var x = 0f
        val pasoX = width / 10f // Trazamos 10 puntos a lo ancho para no matar el procesador
        val yIzquierda = nivelAguaCentroBase - diferenciaAlturaTilt

        pathCuerpoAgua.moveTo(0f, yIzquierda)

        while (x <= width + pasoX) {
            val ratio = x / width
            val tiltLocal = -diferenciaAlturaTilt + (ratio * diferenciaAlturaTilt * 2f)

            // Ondulación
            val anguloBase = (x * 0.015f) + tiempoOlas
            val amplitud = 5f + (energiaAgua * 0.4f)
            val yOnda = sin(anguloBase) * amplitud

            val y = nivelAguaCentroBase + tiltLocal + yOnda
            pathCuerpoAgua.lineTo(x, y)
            x += pasoX
        }

        // Cerrar el polígono de agua por debajo hasta el motor plástico
        pathCuerpoAgua.lineTo(width.toFloat(), limiteSuelo)
        pathCuerpoAgua.lineTo(0f, limiteSuelo)
        pathCuerpoAgua.close()

        canvas.drawPath(pathCuerpoAgua, paintAgua)
    }

    /**
     * Corazón central de colisiones y vectores físicos numéricos compartidos.
     * Modifica las coordenadas (`x`, `y`) basándose en una amalgama de fuerzas concurrentes.
     */
    private fun actualizarFisicas() {
        // Detección directa de los surtidores de empuje generados táctilmente.
        if (botonIzqPulsado) aplicarChorro(width * 0.25f, height * 0.8f)
        if (botonDerPulsado) aplicarChorro(width * 0.75f, height * 0.8f)

        // OPTIMIZACIÓN: Cacheamos las variables matemáticas para no ejecutar multiplicaciones 60 veces por bola.
        val limiteSuelo = height * 0.8f
        val limiteParedDer = width.toFloat()
        val diferenciaAlturaTilt = inclinacionX * -12f

        // OPTIMIZACIÓN GC: Usar un bucle 'for indexado' en Kotlin evita crear un objeto `Iterator` invisible en cada frame.
        for (i in 0 until objetosFlotantes.size) {
            val p = objetosFlotantes[i]

            // Calculamos exactamente a qué altura matemática está el agua *debajo* de esta ficha concreta.
            val ratioX = p.x / width
            val tiltLocal = -diferenciaAlturaTilt + (ratioX * diferenciaAlturaTilt * 2f)
            val nivelAguaLocal = nivelAguaCentroBase + tiltLocal

            // ============================================================================
            // DUALIDAD FÍSICA: CÁMARA DE AIRE VS LÍQUIDO SUMERGIDO
            // ============================================================================
            if (p.y > nivelAguaLocal) {
                // --> ESTÁ SUMERGIDO EN EL AGUA
                p.vy += (gravedad + flotabilidad)

                // INYECCIÓN DE FUERZAS DEL ACELERÓMETRO
                if (!p.atrapado) {
                    p.vx += inclinacionX * 0.3f
                    p.vy += inclinacionY * 0.3f
                }

                // Atenuación obligatoria de inercia (Viscosidad del agua incrementada)
                p.vx *= friccionAgua
                p.vy *= friccionAgua

            } else {
                // --> ESTÁ VOLANDO EN LA CÁMARA DE AIRE
                p.vy += gravedad // En el aire la gravedad empuja mucho más fuerte y hacia abajo (sin flotabilidad)

                if (!p.atrapado) {
                    p.vx += inclinacionX * 0.15f // El aire no "arrastra" tanta masa lateral como el agua
                }

                // Menos fricción: Las piezas vuelan con poca resistencia en el aire
                p.vx *= 0.99f
                p.vy *= 0.99f

                // Efecto 'Chapuzón': Si en el frame anterior venía rapidísimo del aire y acaba de chocar
                // con el agua, frena en seco y genera energía al agua (salpica).
                if (p.y + p.vy > nivelAguaLocal && p.vy > 5f) {
                    energiaAgua += p.vy * 0.5f // Generar ola por impacto
                    p.vy *= 0.4f // Se frena brusco al entrar al agua densa
                }
            }

            // ============================================================================
            // RESISTENCIA DE FLUIDOS: VELOCIDAD TERMINAL
            // Matemática estricta para asegurar que ninguna pieza supera jamás la velocidad
            // balística por mucha gravedad, inclinación o empuje que reciba junta.
            // ============================================================================
            if (p.vx > velocidadMaxima) p.vx = velocidadMaxima else if (p.vx < -velocidadMaxima) p.vx = -velocidadMaxima
            if (p.vy > velocidadMaxima) p.vy = velocidadMaxima else if (p.vy < -velocidadMaxima) p.vy = -velocidadMaxima

            // Impacto del cálculo inercial final directamente hacia las variables de localización cruda.
            p.x += p.vx
            p.y += p.vy

            // ============================================================================
            // REBOTES (Absorción de impactos)
            // ============================================================================
            // Comprobación de Colisiones Laterales
            if (p.x - p.radio < 0) {
                p.x = p.radio
                p.vx *= -0.4f
            }
            if (p.x + p.radio > limiteParedDer) {
                p.x = limiteParedDer - p.radio
                p.vx *= -0.4f
            }

            // Comprobación de Colisión Superior contra el techo real del móvil (el aire)
            if (p.y - p.radio < 0) {
                p.y = p.radio
                p.vy *= -0.5f // Rebota contra la pared de arriba
            }

            // Comprobación de Gravedad Muerta contra el plástico inferior.
            if (p.y + p.radio > limiteSuelo && !p.atrapado) {
                p.y = limiteSuelo - p.radio
                p.vy *= -0.4f

                // LÓGICA ANTI-ATASCO (Optimización para esquinas en móviles lentos)
                if (Math.abs(p.vx) < 0.5f && Math.abs(p.vy) < 0.5f) {
                    p.vx += (Math.random().toFloat() - 0.5f) * 3f
                    p.vy -= Math.random().toFloat() * 2f // Leve saltito hacia arriba
                }
            }
        }

        // Momento de intercesión. Cedemos la ejecución para validar colisiones complejas con postas o cestas hijas.
        comprobarLogicaEspecifica()

        // ============================================================================
        // SANEAMIENTO POST-COLISIÓN (Filtro Anti-Vibración / Resting Contact)
        // Resuelve el problema de las fichas atascadas intermitentemente en los aros.
        // Si la lógica hija (ej: Baloncesto) genera un rebote vertical microscópico
        // que solo provoca "temblores" y permite al acelerómetro contrarrestarlo,
        // lo anulamos. Esto obliga a la ficha a "resbalar" lateralmente sin interrupción.
        // ============================================================================
        for (i in 0 until objetosFlotantes.size) {
            val p = objetosFlotantes[i]
            if (!p.atrapado) {
                // Si el rebote hacia arriba es muy débil (menor a 2.0 de fuerza)
                if (p.vy < 0f && p.vy > -2.0f) {
                    p.vy = 0f // Matamos el rebote para forzar el deslizamiento
                }
            }
        }

        var nuevosAGenerar = 0
        val tamanoActual = objetosFlotantes.size

        // Auditoría final del ciclo: Recontar trofeos.
        for (i in 0 until tamanoActual) {
            val p = objetosFlotantes[i]
            // Verificamos estado y prevenimos conteo duplicado.
            if (p.atrapado && !p.contabilizado) {
                p.contabilizado = true // Lo precintamos a nivel contable para la eternidad
                puntuacion++
                nuevosAGenerar++
            }
        }

        // Generador en bucle de reposición. Mantener constante la fluidez de interacción y abundancia de objetos de juego.
        for (i in 0 until nuevosAGenerar) {
            generarNuevoObjeto()
        }
    }

    /**
     * Aplica un vector artificial de impulso negativo vertical para replicar burbujas y columnas de presión subacuáticas.
     *
     * @param xChorro Punto de origen X donde nace el geiser.
     * @param yChorro Punto de origen Y del geiser (el lecho del mecanismo base).
     */
    private fun aplicarChorro(xChorro: Float, yChorro: Float) {
        val radioChorro = width * 0.4f // Cacheado

        // Generamos energía (olas visuales) al apretar el botón del chorro
        energiaAgua += 3f
        if (energiaAgua > 40f) energiaAgua = 40f // Limitamos la violencia máxima del oleaje

        for (i in 0 until objetosFlotantes.size) {
            val p = objetosFlotantes[i]
            // Un objeto anclado al poste deja de sentir las tempestades del agua.
            if (p.atrapado) continue

            val dx = Math.abs(p.x - xChorro)

            // Efecto Límite: Exclusivamente los elementos adyacentes a la columna fluida son interpelados.
            if (dx < radioChorro) {
                // Algoritmo de Decadencia (Atenuación Linear). En el centro=1.0 pura fuerza, en bordes=0.0 de fuerza.
                val atenuacion = 1f - (dx / radioChorro)

                // Asignamos una fuerza irregular randomizada (5 a 13) y la modulamos por la decadencia geográfica.
                val fuerzaVertical = (5f + Math.random().toFloat() * 8f) * atenuacion
                // En el mundo canvas, restar "Y" propulsa al objeto a elevarse en los aires.
                p.vy -= fuerzaVertical

                // Emulamos corrientes transversales imprecisas al añadir inestabilidad fortuita lateral.
                p.vx += (Math.random().toFloat() - 0.5f) * 12f * atenuacion
            }
        }
    }

    /**
     * Motor gráfico de Interface Superior (UI). Pinta los pulsadores y el muro exterior bloqueante.
     *
     * @param canvas Recipiente de pintura del ciclo de cuadro actual.
     */
    private fun dibujarBotonesYBase(canvas: Canvas) {
        // Bloque de cerramiento base opaco (Cubre y oculta las partes sumergidas que toquen fondo)
        canvas.drawRect(0f, height * 0.8f, width.toFloat(), height.toFloat(), paintBase)

        // OPTIMIZACIÓN GC: Calculamos el color oscuro SÓLO una vez y no en los 60 fotogramas.
        if (!coloresBotonesCalculados) {
            colorIzqOscuro = oscurecerColor(colorBotonIzquierdoNormal)
            colorDerOscuro = oscurecerColor(colorBotonDerechoNormal)
            coloresBotonesCalculados = true
        }

        // PANEL DE BOTONES
        paintBoton.color = if (botonIzqPulsado) colorIzqOscuro else colorBotonIzquierdoNormal
        canvas.drawCircle(width * 0.25f, height * 0.9f, width * 0.1f, paintBoton)

        paintBoton.color = if (botonDerPulsado) colorDerOscuro else colorBotonDerechoNormal
        canvas.drawCircle(width * 0.75f, height * 0.9f, width * 0.1f, paintBoton)

        // ================== Cajas Ópticas de Legibilidad Translucida ==================

        // 1. Panel de Retorno de Flujo (Menú de salida) - Emoji desplazado para no solapar el reloj/batería del sistema.
        canvas.drawText("🔙", 45f, 180f, paintTextoBoton)

        // 2. Sistema de Centralización del Score (Optimizado para evitar el Garbage Collector)
        if (puntuacion != puntuacionAnterior) {
            textoPuntuacionCache = "$puntuacion"
            puntuacionAnterior = puntuacion
        }

        val anchoTexto = paintMarcador.measureText(textoPuntuacionCache)

        // Reciclamos la estructura del rectángulo global, alterando solo sus dimensiones numéricas.
        rectFondoMarcador.set(
            width / 2f - anchoTexto / 2f - 40f,
            30f,
            width / 2f + anchoTexto / 2f + 40f,
            150f
        )

        canvas.drawRoundRect(rectFondoMarcador, 25f, 25f, paintFondoUI)
        canvas.drawText(textoPuntuacionCache, width / 2f, 110f, paintMarcador)
    }

    /**
     * Utilería magistral de colorimetría para mutar el tinte base de botones genéricos reduciendo
     * su luminosidad en base al estándar Hue, Saturation, Value (HSV) de Android.
     *
     * @param colorBase Código crudo de color original que se va a alterar.
     * @return Código numérico final manipulado que resulta en una tonalidad empobrecida de brillo en un 40%.
     */
    protected fun oscurecerColor(colorBase: Int): Int {
        // OPTIMIZACIÓN GC: Usamos el array pre-instanciado 'arrayHsvTemporal' de la clase base.
        Color.colorToHSV(colorBase, arrayHsvTemporal)
        // Reducimos el tercer índice (Luminosidad / Value) drásticamente al 60% puro de su total.
        arrayHsvTemporal[2] *= 0.6f
        // Empaquetamos y rearmamos el resultado en modo `Int`
        return Color.HSVToColor(arrayHsvTemporal)
    }

    /**
     * Detección Multitáctil de Alto Nivel.
     * Procesa asíncronamente las interrupciones del hardware cuando varios dedos pisan al mismo instante.
     *
     * @param event Encapsulamiento de los datos de presión, ubicación y vida del toque.
     * @return `Boolean` obligatorio con valor `true` para advertir al entorno que se tramitó su orden adecuadamente.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var pulsandoIzq = false
        var pulsandoDer = false

        // === DETECCIÓN DEL SONIDO AL TOCAR ===
        // actionMasked nos ayuda a detectar si es el inicio de un toque nuevo
        val accion = event.actionMasked
        if (accion == MotionEvent.ACTION_DOWN || accion == MotionEvent.ACTION_POINTER_DOWN) {
            val indice = event.actionIndex
            val toqueY = event.getY(indice)

            // Si el toque ocurrió en el chasis inferior (zona de botones/chorros)
            if (toqueY > height * 0.8f) {
                motorEfectos?.play(idSonidoBurbuja, 0.8f, 0.8f, 0, 0, 1f)
            }
        }

        // Repasamos el mapeo de todas las coordenadas concurrentes en la pantalla usando un bucle vitalicio.
        for (i in 0 until event.pointerCount) {
            val x = event.getX(i)
            val y = event.getY(i)

            // ZONA VIRTUAL CRÍTICA: Área táctil reservada equivalente al botón de retorno pintado en interfaz gráfica.
            if (event.actionMasked == MotionEvent.ACTION_DOWN && y < 250f && x < 250f) {
                // Comunicación Externa. Cast de contexto pidiendo orden expresa a MainActivity de interrumpir progreso de juego.
                (context as MainActivity).crearMenuPrincipal()
                return true
            }

            // Segmentación inferior. Reparto hemisférico simple que distingue qué empuje se va a activar en función de la coordenada X leída.
            if (y > height * 0.8f) {
                if (x < width / 2f) pulsandoIzq = true
                else pulsandoDer = true
            }
        }

        // Si la orden consiste explícitamente en el despegue físico del contacto digital con el hardware capacitivo, se desactivan flujos.
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            botonIzqPulsado = false
            botonDerPulsado = false
        } else {
            // De mantenerse activo un toque o presionar extra fuertemente, se validan los pulsadores designados por la segmentación.
            botonIzqPulsado = pulsandoIzq
            botonDerPulsado = pulsandoDer
        }

        return true
    }
}