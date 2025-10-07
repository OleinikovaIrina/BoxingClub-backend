# TODO / Open Questions (DTO layer)

User DTO

Пароль в маппере

Сейчас: пароль хэшируется в UserServiceImpl.

Риск: если AppUserMapper.toEntity() будет маппить пароль напрямую → может сохраниться сырой пароль.

Действие: убедиться, что маппер игнорирует password.

Email нормализация

Сейчас: проверяется уникальность email.

Риск: Test@Mail.de и test@mail.de могут считаться разными.

Действие: при сохранении приводить к lowercase + trim.

Membership DTO

IBAN

Сейчас: опциональный, только @Pattern (валидируется, если не null).

Вопрос: будет ли IBAN обязателен при создании Membership или вводится позже (например, при активации)?

consentToSepa / consentToDataPolicy

Сейчас: @AssertTrue в DTO и nullable=false в Entity.

Риск: в будущем могут потребовать хранить согласия не булевым флагом, а отдельной сущностью (например, audit consent).

Действие: уточнить стратегию хранения согласий.

Membership Entity

startDate / endDate

Сейчас: nullable=true.

Мы всегда выставляем даты в createMembership.

Действие: сделать поля обязательными (nullable=false) и проставлять в сервисе.

active

Сейчас: отдельный булевый флаг.

Риск: может дублировать логику status (APPROVED ↔ true).

Действие: для MVP оставить, позже обсудить вычисление из статуса + endDate.

Delete-policy

Сейчас: при отмене → status=CANCELLED, active=false, endDate=today.

Действие: решить, нужно ли по умолчанию фильтровать выборки по актуальным статусам.

AppUser Entity

Email нормализация

Сейчас: проверяется уникальность в БД.

Риск: Test@Mail.de и test@mail.de могут считаться разными.

Действие: приводить email к lowercase + trim перед сохранением.

Default значения для role и confirmationStatus

Сейчас: ROLE_USER и UNCONFIRMED заданы в Entity.

Вопрос: оставить дефолты в Entity или переносить установку в сервис?

Действие: зафиксировать стратегию.

Delete-policy

Вопрос: при «удалении» пользователя делать enabled=false или анонимизацию (GDPR)?

Действие: решить стратегию.

Pagination (добавить позже)

Репозитории: добавить методы с Pageable (напр., findAllByUser_Id(..., Pageable)).

Сервис/контроллер: принимать page/size (и сортировку), возвращать Page<...Dto>.

Ограничить максимальный size (например, ≤ 100).

По умолчанию сортировать по createdAt/updatedAt (добавить позже).

Индексы в БД под частые фильтры (user_id, email).

Unit-тесты

approve TRIAL: первый раз ок; второй раз → исключение.

cancel: повторный cancel не меняет прошлое, но нормализует active=false и endDate=today (если пустая или в будущем).
deleteUser

Сейчас: «жёсткое удаление» через repo.deleteById(id).

Вопрос: нужна ли soft-delete стратегия (enabled=false) для соответствия GDPR?

TODO: обсудить и зафиксировать delete-policy (hard vs soft).

Unit-тесты

createUser:

email с пробелами и в разных регистрах сохраняется в нормализованном виде.

при повторной регистрации с тем же email (любой регистр) → 409.

getUserByEmail:

вход Test@Mail.DE → находит сохранённого test@mail.de.

вход null → 400 BAD_REQUEST.

deleteUser:

удаление несуществующего id → UserNotFoundException

TODO по Security (внести в трекер)

Профили и конфиги

DevSecurityConfig: всё permitAll (для разработки).

ProdSecurityConfig: @EnableMethodSecurity + правила доступов.

JWT аутентификация

JwtTokenProvider (генерация/валидация токена).

JwtAuthenticationFilter + регистрация в SecurityFilterChain.

AuthenticationEntryPoint (корректный 401).

AccessDeniedHandler (корректный 403).

Пользователи и роли

CustomUserDetailsService (загрузка пользователя, GrantedAuthority = ROLE_* из enum).

PasswordEncoder (BCrypt).

Проверка, что в БД роли хранятся как ROLE_USER/ADMIN/TRAINER.

Правила доступов

Admin API → hasRole('ADMIN').

User API → hasAnyRole('USER','ADMIN'), при необходимости чтение для TRAINER.

Публичные (регистрация/подтверждение) → @PermitAll.

Инфраструктура

Swagger/H2 разрешить явно (в prod только Swagger).

CORS (если будет фронт на другом домене).

CSRF выключить для REST (или настроить, если будет Session).

Тесты

Интеграционные с @WithMockUser(roles="ADMIN"), @WithMockUser(roles="USER").

Проверки 401/403/200 на ключевых эндпоинтах.

## Registration → Checklist (PROD-CONFIRM)
- [ ] ConfirmationServiceProdImpl (JPA + TTL)
- [ ] Liquibase: table `confirmation_code` (user_id UNIQUE, code UNIQUE, expires_at, used)
- [ ] EmailServiceProdImpl (SMTP/provider), скрыть код в логах
- [ ] application-prod.properties: SMTP creds, ttl-mins
- [ ] GlobalExceptionHandler: map Invalid/Expired → 400/410
- [ ] Replace [DEV-CONFIRM] usages (grep by PROD-CONFIRM)
- [ ] Integration tests (profile=prod): confirm flow happy/expired