# Блок 1: Документация и спецификация

## Обзор

Создана полная документация для Step 1, включая спецификацию, план реализации, задачи и обновления конституции проекта.

## Изменения

### 1. Спецификация Step 1 (`specs/001-step1/spec.md`)

**Структура документа**:
- Цель и Non-Goals
- Constraints (правила конституции)
- Technical decisions (SDK, UI, DI, Coroutines)
- Module set (минимальный набор модулей)
- Dependency rules (правила зависимостей)
- Scope details (детали реализации)
- Testing strategy
- CI/CD требования
- Definition of Done

**Ключевые решения**:
- minSdk = 24, target/compileSdk = 35
- JUnit5 для тестов
- Feature:home как эталон (3 модуля: domain, data, presentation)
- Core:async включен в Step 1

### 2. План реализации (`specs/001-step1/plan.md`)

**Этапы**:
1. Bootstrap Gradle & Catalog
2. Modules & Plugins
3. Core foundation
4. Feature:home skeleton
5. Navigation
6. Tests (smoke)
7. CI
8. Documentation

**Принципы**:
- Минимум инфраструктуры, максимум простоты
- Соблюдение конституции
- Каждая итерация держит билд зелёным

### 3. Задачи (`specs/001-step1/tasks.md`)

**Формат**: `[ID] [P?] Description — Done when <критерий>`

**Всего задач**: 16 (T1-T16)

**Примеры задач**:
- T1: Заполнить `gradle/libs.versions.toml`
- T8: Domain: интерфейс репозитория + use case
- T13: Unit: JUnit5 + MockK/Turbine на ViewModel
- T16: README: структура, правила, инструкции

### 4. Обновление конституции (`.specify/memory/constitution.md`)

**Добавлено**:

#### Секция 12: Constant simplification principle
```markdown
## 12 Constant simplification principle

При добавлении фичи или при исправлении, каждый раз, проверяй есть ли возможность 
решить более коротким путем. Нужно стремится, не дописывать код, а стараться каждый 
раз делать его логичнее и понятнее. Если есть что то, что можно упростить - предлагать, 
объяснять, потом упрощать. Если есть возможноть не писать длинный код, а использовать 
готовую библиотеку, расширение, пакет - предлагай, объясняй, используй.
```

#### Секция 13: Assistant behavior
```markdown
## 13 Assistant behavior

### Core principle
The assistant must not behave as a passive helper. It must behave as a rigorous 
intellectual opponent and a multi-perspective reviewer whose goal is to improve the 
user's thinking and decision quality.

### Non-disclosure of internal deliberation
- The assistant must **never reveal, quote, or describe** any internal deliberation, 
  internal debate, hidden reasoning steps, scoring, or "panel discussion".
- The assistant must output **only the final integrated response**.

### Self-reflection (internal-only quality process)
Before responding, the assistant must:
1. Create a strict internal rubric with **5–7 categories** to judge answer quality 
   from the perspective of a **multi-expert panel**.
2. Use this rubric to **iterate internally** until the response would score **≥98/100** 
   against the rubric.
3. Actively challenge the user's statements and framing:
   - test the **logic** and identify weak links,
   - evaluate **assumptions** and hidden premises,
   - check **facts** when relevant,
   - surface **missing alternatives** and neglected constraints.
4. Keep all internal debate and iteration private; output only the final conclusion.

### Answering rules (external behavior)
- **Language:** Use the user's language for chat replies (Russian by default).
- **Panel mode:** Respond as a **coordinated group of real-world professionals**, 
  with roles chosen dynamically by the assistant based on the topic.
- **Skeptical stance:** Do **not** treat the user's statements as truth by default. 
  Verify and question them.
- **Intellectual opponent behavior:** Whenever the user presents an idea, the assistant 
  must decompose it, test consistency, validate claims, propose alternatives, identify 
  blind spots, and offer stronger reformulations.
- **Tone:** Maintain a constructive tone: firm, precise, and helpful—**not agreeable 
  by default**.
```

### 5. Обновление README (`README.md`)

**Добавлено**:
- Обзор Step 1
- Структура модулей
- Правила зависимостей
- Инструкция "Как добавить новую фичу"
- Команды для сборки и тестирования

**Пример секции "Как добавить новую фичу"**:
```markdown
## Как добавить новую фичу

### 1. Создать структуру модулей

Создайте 3 модуля в `feature/<feature-name>/`:
- `domain/` — интерфейсы и use cases
- `data/` — реализации и Hilt модули
- `presentation/` — UI, ViewModel, навигация

### 2. Зарегистрировать в settings.gradle.kts

```kotlin
include(":feature:<feature-name>:domain")
include(":feature:<feature-name>:data")
include(":feature:<feature-name>:presentation")
```

### 3. Настроить зависимости

**domain/build.gradle.kts**:
```kotlin
dependencies {
    implementation(project(":core:common"))
    // Опционально: core:async для Clock/DispatcherProvider
}
```

**data/build.gradle.kts**:
```kotlin
dependencies {
    implementation(project(":feature:<feature-name>:domain"))
    implementation(project(":core:common"))
    // НЕ добавлять core:network на Step 1
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
```

**presentation/build.gradle.kts**:
```kotlin
dependencies {
    implementation(project(":feature:<feature-name>:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:async"))
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.navigation.compose)
}
```

### 4. Создать domain слой

**Repository интерфейс**:
```kotlin
interface FeatureRepository {
    suspend fun getData(): Result<Data>
}
```

**Use case**:
```kotlin
class GetDataUseCase(
    private val repository: FeatureRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(): Result<Data> {
        return withContext(dispatcherProvider.io) {
            repository.getData()
        }
    }
}
```

### 5. Создать data слой

**Repository реализация**:
```kotlin
@Singleton
class FeatureRepositoryImpl @Inject constructor() : FeatureRepository {
    override suspend fun getData(): Result<Data> {
        // Stub реализация
        return Result.Success(Data("stub"))
    }
}
```

**Hilt модуль**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureDataModule {
    @Binds
    abstract fun bindFeatureRepository(
        impl: FeatureRepositoryImpl
    ): FeatureRepository
}
```

### 6. Создать presentation слой

**UiState, UiEvent, UiEffect**:
```kotlin
data class FeatureUiState(
    val data: Data? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)

sealed class FeatureUiEvent {
    object LoadData : FeatureUiEvent()
}

sealed class FeatureUiEffect : UiEffect {
    data class Navigate(val route: String) : FeatureUiEffect()
}
```

**ViewModel**:
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeatureUiState())
    val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()
    
    private val _uiEffect = MutableSharedFlow<FeatureUiEffect>()
    val uiEffect: SharedFlow<FeatureUiEffect> = _uiEffect.asSharedFlow()
    
    fun handleEvent(event: FeatureUiEvent) {
        when (event) {
            is FeatureUiEvent.LoadData -> loadData()
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getDataUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, data = result.data) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.error) 
                    }
                }
            }
        }
    }
}
```

**Screen**:
```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect = remember { viewModel.uiEffect }
    
    LaunchedEffect(Unit) {
        viewModel.handleEvent(FeatureUiEvent.LoadData)
    }
    
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is FeatureUiEffect.Navigate -> onNavigate(effect.route)
            }
        }
    }
    
    // UI implementation
}
```

**Навигация**:
```kotlin
object FeatureRoutes {
    const val FEATURE = "feature/main"
}

fun NavGraphBuilder.featureGraph(
    navController: NavHostController
) {
    composable(FeatureRoutes.FEATURE) {
        FeatureScreen(onNavigate = { route ->
            navController.navigate(route)
        })
    }
}
```

### 7. Подключить в app

**app/build.gradle.kts**:
```kotlin
dependencies {
    implementation(project(":feature:<feature-name>:presentation"))
}
```

**app/navigation/RootNavGraph.kt**:
```kotlin
NavHost(...) {
    featureGraph(navController = navController)
}
```

## Коммит

**ID**: `98f7f59`  
**Сообщение**: `docs: add Step1 spec, plan, tasks and update constitution/README`  
**Файлов**: 5  
**Изменений**: +232 / -1

## Проверка

✅ Спецификация полная и соответствует требованиям  
✅ План реализуем и разбит на инкременты  
✅ Задачи конкретны и проверяемы  
✅ Конституция обновлена с новыми принципами  
✅ README содержит инструкции для разработчиков

