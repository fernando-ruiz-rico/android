# Paso seguidos para crear la aplicación

1. **Añadir las dependencias necesarias**
  * Iconos extendidos
  * Navigation 3 libraries
  * material3-adaptive-navigation-suite
  * Room
  * Koin
  * Plugins de Kotlin Serizalization y Google ksp
2. **Crear las entidades de la base de datos** 
   * Data class Equipo
   * Data class Jugador
   * Data class EquipoConJugadores (para la relación equipo - jugador)
3. **Crear las interfaces DAO**
   * interface EquipoDao
   * interface JugadorDao
4. **Crear las clases Repository**
   * class EquipoRepository
   * class JugadorRepository
5. **Crear la clase AppDatabse (conexión a la base de datos)**
6. **Crear el Módulo AppModule**
7. **Crear la clase de Aplicación e inicializar Koin y el módulo**

No nos debemos olvidar de registrar la aplicación en AndroidManifest.xml
```xml
<application
  android:name=".EquiposApplication"
  ...>
  ...
</application>
```
8. 
