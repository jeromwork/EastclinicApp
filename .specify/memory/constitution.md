# [EastclinicApp] Constitution
<!-- Example: Spec Constitution, TaskFlow Constitution, etc. -->

## Core Principles
# EastclinicApp — Constitution

## 0 Prime directive (source of truth)
These rules have higher priority than any suggestion, framework example, or “best practice”.
If there is a conflict: follow this document.
If something is unclear: prefer the smallest change that preserves architecture and keeps the build green.

## 1 Architecture is non-negotiable
- Each feature is split into layers: presentation / domain / data.
- UI never calls network/storage directly and never depends on DTOs/entities.
- Domain is the purest layer:
  - no Android types
  - no Retrofit/OkHttp/Room types
  - no DTO/entity classes
  - only Kotlin models, interfaces, and business rules
- Dependencies only inward:
  - presentation → domain
  - data → domain
  - never the opposite
- Feature isolation:
  - feature:* must NOT import other feature:* directly
  - shared code goes through core:* only

## 2 Risk zones behind interfaces (no leaked implementations)
All risky/replaceable subsystems MUST be hidden behind stable domain interfaces:
- auth: AuthRepository / SessionStore / TokenRefresher
- push: PushTokenProvider / PushRegistrar / NotificationHandler / NotificationRouter
- chat: ChatRepository (+ ChatTransport if needed)

Rules:
- No SDK/implementation types in ViewModel/use-cases/UiState.
- External SDKs (FCM, payments, chat, etc.) live in data/* or core/*-impl only.
- Avoid global mutable singletons; use DI + interfaces.
- ViewModels and use-cases must not change when implementations swap.

## 3 Roles (future-proof)
- UserRole exists from day 1 and is used for role-based routing/navigation decisions.
- MVP implements patient UI only, but the code must not assume “only one role forever”.
- Any role branching must be explicit and testable.

## 3.1 
- All generated specs/plans/tasks must be written in Russian.

## 4 UI contract standard (UDF)
Each ViewModel:
- exposes UiState via StateFlow
- accepts UiEvent
- emits UiEffect for one-off events (navigation, toasts, open-url, etc.)

Rules:
- UiState/UiModel MUST NOT contain DTO/entity/sdk types.
- Mapping: domain model → UiModel via dedicated mapper (e.g., *UiMapper).

## 5 Single responsibility & layering rules
- One responsibility per class.
- Use-case = one scenario.
- Repository hides sources (remote/local/cache) and exposes domain models.
- Mappers are separate:
  - remote DTO ↔ domain model
  - DB entity ↔ domain model
  - domain model ↔ UiModel

## 6 Errors & UX states
- Normalize all failures to AppError (typed, stable).
- Mapping rules:
  - data translates network/db/serialization exceptions → domain-level AppError
  - presentation sees AppError only (or AppErrorUiModel), never raw Throwable
- Screen states are consistent:
  - loading / error / empty / content
- Error messages must be user-safe (no PII/PHI, no tokens, no raw stack traces).

## 7 MVP constraints (no premature complexity)
MVP MUST NOT introduce:
- offline-first complexity (conflict resolution, background sync engine)
- KMM
- WebSockets (unless only a thin contract is added, no infra)
- complex deep links (only minimal hooks/contracts allowed)

Allowed in MVP:
- minimal contracts + extension hooks
- simple in-memory caching if it does not leak into domain/presentation
- clean interfaces to allow future growth

## 8 Security defaults (healthcare-grade)
- Tokens only in encrypted storage (no plaintext SharedPreferences).
- Never log tokens or PII/PHI.
- If logging is needed: mask identifiers and keep logs purely technical.
- No secrets in repo; no copying real patient data into tests.

## 9 Naming & style stability
Stable suffixes:
- *UseCase, *Repository, *DataSource, *ViewModel, *UiModel, *Mapper
Do not rename public contracts unless strongly justified and fully updated.

## 10 Working agreement: keep build green
- Every step must keep the app buildable. Do not “accumulate unfinished work”.
- If behavior changes: add/update at least a minimal unit test for use-case/mapper.

Honesty rule:
- Do NOT claim you executed build/tests unless you actually did.
- If you cannot run commands, state what should be run and what success looks like.

## 11 Default commands (unless repo overrides)
- Build: ./gradlew assembleDebug
- Tests: ./gradlew test
- Lint:  ./gradlew lint

If a command fails:
- explain the root cause
- propose the smallest fix
- avoid large refactors

**Version**: [1] | **Ratified**: [2025-12-14] | **Last Amended**: [2025-12-14]
