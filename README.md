# Calculadora estilo iOS — Proyecto I (Programación para Móviles, UTP)

Aplicación Android nativa en Kotlin + Jetpack Compose con arquitectura MVVM.

## 1. Cómo montarlo en Android Studio

1. **New Project → Empty Activity** (la de Compose).
   - Name: `Calculator`
   - Package name: `co.edu.utp.calculator`
   - Language: Kotlin · Build configuration language: Kotlin DSL
2. Borre la carpeta `app/src/main/java/co/edu/utp/calculator/ui/theme` que crea la plantilla (Color.kt, Theme.kt, Type.kt) y el `MainActivity.kt` original.
3. Copie desde este paquete:
   - `app/src/main/java/...` → todo el código.
   - `app/src/main/res/values/strings.xml` y `colors.xml` → reemplazan los de la plantilla.
     (Deje el `themes.xml` de la plantilla tal cual.)
   - `app/src/test/...` → pruebas unitarias.
   - `.gitignore` → en la raíz del proyecto.
4. Agregue las dependencias de navegación y lifecycle-compose:

**`gradle/libs.versions.toml`**
```toml
[versions]
navigationCompose = "2.8.4"

[libraries]
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
```
(`lifecycleRuntimeKtx` ya existe en el catálogo de la plantilla.)

**`app/build.gradle.kts`** → dentro de `dependencies { }`
```kotlin
implementation(libs.androidx.navigation.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.lifecycle.runtime.compose)
```
5. *Sync Now* → Run.

Si eligió otro nombre de paquete, cambie la línea `package` y los `import co.edu.utp.calculator...` (Android Studio lo hace con *Refactor → Rename* sobre el paquete).

## 2. Estructura (separación por responsabilidad)

```
co.edu.utp.calculator
├── MainActivity.kt                  Punto de entrada; solo monta tema + navegación
├── domain/                          MODEL (Kotlin puro, sin Android)
│   ├── model/                       Estados y tipos: CalculatorState, CalculatorInput,
│   │                                CalculatorOperation, CalculatorError, Calculation, EngineResult
│   └── logic/CalculatorEngine.kt    TODAS las reglas de cálculo
├── ui/
│   ├── calculator/
│   │   ├── CalculatorViewModel.kt   VIEWMODEL: recibe eventos, llama al motor, expone StateFlow
│   │   ├── CalculatorUiState.kt     Estado que la UI pinta
│   │   ├── CalculatorScreen.kt      VIEW: pantalla de la calculadora
│   │   └── components/              Botón, teclado, display y distribución de teclas
│   ├── history/HistoryScreen.kt     VIEW: lista del historial y estado vacío
│   ├── navigation/                  NavHost con las dos rutas
│   ├── common/DomainResources.kt    Traduce operaciones/errores del dominio a R.string
│   └── theme/
```

**Flujo MVVM:** botón → `onInput(CalculatorInput)` → `CalculatorViewModel` → `CalculatorEngine.process(estado, entrada)` → nuevo estado → `StateFlow<CalculatorUiState>` → `collectAsStateWithLifecycle()` → Compose se redibuja.

- Los Composables no calculan nada: solo pintan el estado y envían eventos.
- El ViewModel no usa `stringResource`, `colorResource` ni `Context`; expone estados semánticos (`CalculatorError.DivisionByZero`) y la UI los convierte a texto en `DomainResources.kt`.
- Un solo `CalculatorViewModel` se comparte entre las dos pantallas (se crea fuera del `NavHost`, con alcance de la Activity), por eso el historial se conserva al navegar y al rotar el teléfono.

## 3. Comportamiento definido (para la sustentación)

| Caso | Comportamiento |
|---|---|
| Operaciones consecutivas | Se evalúan de izquierda a derecha al pulsar el siguiente operador: `2 + 3 × 4 =` → `5 × 4` → **20** |
| Cambiar de operador | Si se pulsa un operador sin haber escrito el segundo número, se reemplaza: `5 + × 2 =` → **10** |
| `=` repetido | Repite la última operación: `5 + 3 = = =` → 8, 11, **14** |
| `=` sin segundo número | Usa el mismo número: `5 + =` → **10** |
| Número nuevo tras `=` | Mantiene la última operación: `9 + 1 =` (10), escribo `4`, `=` → **5** |
| AC | Reinicia la calculadora (el historial se conserva) |
| +/− | Cambia el signo del número en pantalla; si aún no se ha escrito número, inicia en `-0` |
| % | Con `+` o `−` pendiente es relativo al primer número: `50 + 10 % =` → **55**. En otro caso divide entre 100 |
| Decimales | Separador único `.`; un segundo punto se ignora; cálculos con `BigDecimal` (`0.1 + 0.2 = 0.3`) |
| Límites | Máximo 15 dígitos de entrada; resultados largos se redondean o pasan a notación científica |
| División por cero | Muestra "No se puede dividir entre cero"; no se guarda en el historial; AC o un dígito reinician |
| Desbordamiento | Resultados mayores a 10¹⁰⁰ muestran "Resultado demasiado grande" |
| Historial | Cada operación binaria completada (incluidas las intermedias de una cadena) se agrega en orden cronológico |

## 4. Pruebas

`app/src/test/.../CalculatorEngineTest.kt` cubre suma, encadenamiento, `=` repetido, división por cero, recuperación tras error, decimales, porcentaje, signo y AC. Ejecutar con clic derecho → *Run 'CalculatorEngineTest'*.

## 5. Git

```bash
git init
git add .
git commit -m "Calculadora estilo iOS con Compose y MVVM"
git branch -M main
git remote add origin https://github.com/<usuario>/<repositorio>.git
git push -u origin main
```
Antes del primer commit verifique con `git status` que **no** aparezcan `build/`, `.idea/`, `.gradle/` ni `local.properties`.
