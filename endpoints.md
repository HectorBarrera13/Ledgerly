2# API de Grupos, Deudas, Contactos y Notificaciones

Una propuesta de endpoints REST para cubrir las historias de usuario descritas. Incluye tablas resumen, ejemplos de request/response y convenciones para lograr una implementación consistente y agradable a consumir.

-   Base URL (prod): https://ledgerly-app.com/v1
-   Base URL (sandbox): https://sandbox.api.ledgerly.com/v1
-   Auth: Bearer JWT en `Authorization: Bearer <token>`

Tabla de contenido

-   Convenciones
-   Autenticación y Cuenta
-   Contactos
-   Grupos
-   Deudas grupales
-   Deudas rápidas
-   Balance de pagos
-   Pagos por transferencia
-   Notificaciones
-   Errores estándar
-   Paginación y filtros
-   Ejemplos rápidos
-   Mapeo a Historias de Usuario

---

## Convenciones

-   IDs: UUID v4
-   Fechas:
    -   ISO-8601, UTC (ej. `2025-10-30T02:44:59Z`)
    -   Para fechas sin hora (ej. de gasto), usar `YYYY-MM-DD`
-   Dinero: usar minor units (ej. centavos) como enteros
-   Seguridad:
    -   Todas las rutas (excepto registro/login) requieren JWT
    -   Idempotency-Key recomendado en operaciones POST/PUT sensibles a duplicados
-   Respuestas:
    -   JSON
    -   Errores con formato consistente (ver sección Errores estándar)
-   Paginación:
    -   Cursor-based: `?limit=50&cursor=<opaque>`

---

## Autenticación

Resumen
| Método | Path | Descripción |
|---|---|---|
| POST | /auth/register | Registrarse |
| POST | /auth/login | Iniciar sesión |
| POST | /auth/refresh | Renovar token |
| POST | /auth/logout | Cerrar sesión |

POST `/auth/register`

-   Body:

```json
{
    "first_name": "Juan",
    "last_name": "Pérez",
    "email": "jperez@example.com",
    "password": "S3gura!",
    "phoneCountryCode": "+52",
    "phoneNumber": "1555555555"
}
```

-   201:

```json
{
    "account": {
        "id": "uuid",
        "email": "jperez@example.com"
    },
    "user": {
        "id": "uuid",
        "first_name": "Juan",
        "last_name": "Pérez",
        "email": "jperez@example.com",
        "phone": "+52155..."
    },
    "tokens": {
        "access_token": "...",
        "session_expires_at": "2025-11-14T00:00:00Z"
    }
}
```

POST `/auth/login`

-   Body:

```json
{
    "email": "jperez@example.com",
    "password": "S3gura!"
}
```

-   200:

```json
{
    "access_token": "...",
    "session_expires_at": "2025-11-14T00:00:00Z"
}
```

POST `/auth/refresh`

-   Body:

```json
{
    "refresh_token": "..."
}
```

-   200:

```json
{
    "access_token": "...",
    "session_expires_at": "2025-11-14T00:00:00Z"
}
```

POST `/auth/logout`

-   204

## Perfil de usuario

Resumen
| GET | /profile | Ver perfil |
| PATCH | /profile | Editar nombre y apellido |
| DELETE | /profile | Eliminar cuenta |

GET `/profile`

-   200:

```json
{
    "id": "uuid",
    "first_name": "Juan",
    "last_name": "Pérez",
    "phone": "+52-155..."
}
```

PATCH `/profile`

-   Body:

```json
{
    "first_name": "Juan",
    "last_name": "Pérez"
}
```

-   200:

```json
{
    "id": "uuid",
    "first_name": "Juan",
    "last_name": "Pérez",
    "phone": "+52-155..."
}
```

## Amigos

Resumen
| Método | Path | Descripción |
|---|---|---|
| GET | /friends/qr | Generar QR |
| POST | /friends/{userId} | Añadir amigo |
| GET | /friends | Listar amigos |
| GET | /friends/{friendId} | Ver detalle de amigo |
| PATCH | /friends/{friendId} | Editar alias |
| DELETE | /friends/{friendId} | Eliminar amigo |

GET `/friends/qr`

-   200:

```json
{
    "qr": "uuid",
    "expires_at": "2025-11-01T12:00:00Z"
}
```

POST `/friends/{userId}`

-   201:

```json
{
    "id": "uuid",
    "first_name": "María",
    "last_name": "López",
    "added_at": "2025-10-20T10:00:00Z"
}
```

GET `/friends?limit=50&cursor=uuid`

-   200:

```json
{
    "items": [
        {
            "id": "uuid",
            "first_name": "María",
            "last_name": "López",
            "created_at": "2025-10-20T10:00:00Z"
        },
        {
            "id": "uuid",
            "first_name": "Aldar",
            "last_name": "Faltó",
            "created_at": "2025-10-20T10:00:00Z"
        }
    ],
    "next_cursor": "uuid"
}
```

GET `/friends/{friendId}`

-   200:

```json
{
    "id": "uuid",
    "first_name": "María",
    "last_name": "López",
    "added_at": "2025-10-20T10:00:00Z"
}
```

DELETE `/friends/{friendId}`

-   204

---

## Deudas en general

| Método | Path                  | Descripción                               |
|--------|-----------------------|-------------------------------------------|
| PATCH | /debt/{DebtId}        | Editar deuda                              |
| DELETE | /debt/{DebtId}        | Eliminar deuda                            |
| POST | /debt/{DebtId}/settle | Marcar pago realizado                     |

PATCH `/debt/{DebtId}`
-   Body (ready):

```json
{
    "actor_id": "UUID",
    "debt_id": "UUID",
    "new_purpose": "Nuevo propósito",
    "new_description": "Nueva descripción",
    "new_currency": "MXN",
    "new_amount": "15000L"
}
```

## Deudas rápidas


| Método | Path                    | Descripción |
|--------|-------------------------|-------------|
| POST | /quick-debt             | Registrar deuda rápida |
| GET | /quick-debt/debtor      | Listar por pagar |
| GET | /quick-debt/creditor    | Listar por cobrar |
| PATCH | /quick-debt/{DebtId}    | Editar deuda rápida |

POST `/quick-debts`

-   Body: (Correct)

```json
{
    "purpose": "Café",
    "description" : "", 
    "currency" : "MXN",
    "amount" : "5000L" ,
    "user_id": "me",
    "role": "CREDITOR",
    "target_user": "targerName"
}
```

-   201: (pendiente de confirmar los campos)

```json
{
    "quick_debt_id": "qd1",
    "creditor_user": {
        "id": "me|uuidCreditor",
        "first_name": "Juan",
        "last_name": "Pérez"
    },
    "debtor_user": {
        "id": "uuidDebtor",
        "first_name": "Ana",
        "last_name": "García"
    },
    "amount_minor_units": 5000,
    "status": "PENDING",
    "created_at": "2025-10-29"
}
```

GET `/quick-debts?limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
            "quick_debt_id": "qd1",
            "concept": "Café",
            "debtor_user": {
                "id": "uuidDebtor",
                "first_name": "Ana",
                "last_name": "García"
            },
            "creditor_user": {
                "id": "me",
                "first_name": "Juan",
                "last_name": "Pérez"
            },
            "amount_minor_units": 6500,
            "status": "PENDING",
            "created_at": "2025-10-30"
        },
        {
            "quick_debt_id": "qd2",
            "concept": "Cena",
            "debtor_user": {
                "id": "me",
                "first_name": "Juan",
                "last_name": "Pérez"
            },
            "creditor_user": {
                "id": "uuidCreditor",
                "first_name": "Luis",
                "last_name": "Martínez"
            },
            "amount_minor_units": 12000,
            "status": "ACCEPTED",
            "created_at": "2025-10-30"
        }
    ]
}
```

GET `/quick-debts/debtor/?status=PENDING|ACCEPTED|REJECTED|SETTLED&limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
            "quick_debt_id": "qd1",
            "concept": "Café",
            "creditor_user": {
                "id": "uuidCreditor",
                "first_name": "Luis",
                "last_name": "Martínez"
            },
            "amount_minor_units": 5000,
            "created_at": "2025-10-29"
        },
        {
            "quick_debt_id": "qd1",
            "concept": "Cena",
            "creditor_user": {
                "id": "uuidCreditor",
                "first_name": "Luis",
                "last_name": "Martínez"
            },
            "amount_minor_units": 5000,
            "created_at": "2025-10-29"
        }
    ]
}
```

GET `/quick-debts/creditor/?status=PENDING|ACCEPTED|REJECTED|SETTLED&limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
            "quick_debt_id": "qd1",
            "concept": "Café",
            "debtor_user": {
                "id": "uuidDebtor",
                "first_name": "Ana",
                "last_name": "García"
            },
            "amount_minor_units": 5000,
            "created_at": "2025-10-29"
        },
        {
            "quick_debt_id": "qd1",
            "concept": "Cena",
            "debtor_user": {
                "id": "uuidDebtor",
                "first_name": "Ana",
                "last_name": "García"
            },
            "amount_minor_units": 5000,
            "created_at": "2025-10-29"
        }
    ]
}
```

PATCH `/quick-debts/{DebtId}`

-   Body (parcial):

```json
{
    "amount_minor_units": 6500,
    "concept": "Café y pan"
}
```

-   200:

```json
{
    "quick_debt_id": "qd1",
    "amount_minor_units": 6500,
    "status": "PENDING",
    "date": "2025-10-30"
}
```

DELETE `/quick-debts/{DebtId}`

-   204



POST `/quick-debts/{DebtId}/settle`

```json
{
    "status": "SETTLED",
    "settled_at": "2025-10-30T01:00:00Z"
}
```

---

## Deudas entre usuarios

| Método | Path                       | Descripción                               |
|--------|----------------------------|-------------------------------------------|
| POST | /debt-between-users        | Registrar deuda rápida                    |
| GET | /debt-between-users/debtor        | Listar por pagar                          |
| GET | /debt-between-users/creditor      | Listar por cobrar                         | 
|POST | /debt/{DebtId}/verify-payment | Confirmar el pago(La deuda queda saldada) |
|POST | /debt/{DebtId}/reject-payment  | Rechazar el pago                          |
|POST | /debt/{DebtId}/settle         | Reportar deuda como saldada               |

POST `/debt-between-users`

-   Body:

```json
{
    "purpose": "Pizza",
    "description": "",
    "currency" : "MXN",
    "amount" : "10000L" ,
    "debtor_id": "targetUserId|me",
    "creditor_id": "me|targetUserId"
}
```

-   201:

```json
{
    "debt_id": "d1",
    "debtor_id": "me|uuiddebtor",
    "creditor_id" : "uuidcreditor|me",
    "purpose": "Pizza",
    "description": "",
    "currency": "MXN",
    "amount" : "10000L",
    "status": "PENDING",
    "created_at": "2025-10-29"
}
```



GET `/quick-debts/debtor/?status=PENDING|ACCEPTED|REJECTED|SETTLED&limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
          "debt_id": "d1",
          "debtor_id": "me",
          "creditor_id" : "uuidcreditor",
          "purpose": "Pizza",
          "description": "",
          "currency": "MXN",
          "amount" : "10000L",
          "status": "PENDING",
          "created_at": "2025-10-29"
        },
        {
          "debt_id": "d1",
          "debtor_id": "me",
          "creditor_id" : "uuidcreditor",
          "purpose": "Cafe",
          "description": "",
          "currency": "MXN",
          "amount" : "10000L",
          "status": "PENDING",
          "created_at": "2025-10-29"
        }
    ]
}
```

GET `/quick-debts/creditor/?status=PENDING|ACCEPTED|REJECTED|SETTLED&limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
          "debt_id": "d1",
          "debtor_id": "uuiddebtor",
          "creditor_id" : "me",
          "purpose": "Pizza",
          "description": "",
          "currency": "MXN",
          "amount" : "10000L",
          "status": "PENDING",
          "created_at": "2025-10-29"
        },
        {
          "debt_id": "d1",
          "debtor_id": "uuiddebtor",
          "creditor_id" : "me",
          "purpose": "Cafe",
          "description": "",
          "currency": "MXN",
          "amount" : "10000L",
          "status": "PENDING",
          "created_at": "2025-10-29"
        }
    ]
}
```

PATCH `/quick-debts/{DebtId}`

-   Body (parcial):

```json
{
  "debt_id": "d1",
  "purpose": "Pizza",
  "description": "",
  "currency": "MXN",
  "amount" : "10000L"
}
```

-   200:

```json
{
    "quick_debt_id": "qd1",
    "amount_minor_units": 6500,
    "status": "PENDING",
    "date": "2025-10-30"
}
```

DELETE `/quick-debts/{DebtId}`

-   204



POST `/quick-debts/{DebtId}/settle`

```json
{
    "status": "SETTLED",
    "settled_at": "2025-10-30T01:00:00Z"
}
```

---

## Balance de pagos

Resumen
| Método | Path | Descripción |
|---|---|---|
| GET | /balances/total | Neto total (me deben vs debo) |

GET `/balances/total`

-   200:

```json
{
    "net_cents": -30000,
    "total_to_pay_cents": 30000,
    "total_to_collect_cents": 0
}
```

---

## Notificaciones

Se implementara un sistema de notificaciones basado en webssockets.

WS `/notifications/debts`

-   Mensaje de nueva notificación:

```json
{
    "notification_id": "n1",
    "type": "DEBT_ASSIGNMENT",
    "message": "Tienes una nueva deuda asignada de $5000",
    "by_user": {
        "id": "relatedUserId",
        "first_name": "Luis",
        "last_name": "Martínez"
    },
    "related_debt_id": "debtId",
    "created_at": "2025-10-30T02:00:00Z"
}
```

Resumen
| Método | Path | Descripción |
|---|---|---|
| GET | /notifications | Listar notificaciones |
| PATCH | /notifications/{notificationId}/read | Marcar como leída |

GET `/notifications/debts?status=READ|UNREAD&limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
            "notification_id": "n1",
            "type": "DEBT_ASSIGNMENT",
            "message": "Tienes una nueva deuda asignada de $5000",
            "by_user": {
                "id": "relatedUserId",
                "first_name": "Luis",
                "last_name": "Martínez"
            },
            "related_debt_id": "debtId",
            "created_at": "2025-10-30T02:00:00Z"
        }
    ],
    "next_cursor": "opaque"
}
```

PATCH `/notifications/{notificationId}/read`

-   200:

```json
{
    "notification_id": "n1",
    "status": "READ",
    "read_at": "2025-10-30T03:00:00Z"
}
```

## Errores estándar

-   **400**

    ```json
    {
        "message": "VALIDATION_ERROR",
        "details": [
            {
                "field": "amount_cents",
                "message": "Must be > 0"
            }
        ],
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```

-   **401**
    ```json
    {
        "message": "UNAUTHENTICATED",
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **403**
    ```json
    {
        "message": "INSUFFICIENT_PERMISSIONS",
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **404**
    ```json
    {
        "message": "NOT_FOUND",
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **409**
    ```json
    {
        "message": "CONFLICT",
        "details": [
            {
                "field": "amount_cents",
                "message": "Must be > 0"
            }
        ],
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **423**
    ```json
    {
        "message": "ACCOUNT_SUSPENDED",
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **429**
    ```json
    {
        "message": "RATE_LIMITED",
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```
-   **500**
    ```json
    {
        "timestamp": "2025-10-30T02:00:00Z"
    }
    ```

---

## Paginación y filtros

-   Query: `?limit=50&cursor=<opaque>`
-   Respuesta incluye `next_cursor` cuando hay más resultados
-   Filtros típicos:
    -   `status` (notifs, deudas)
    -   `membership` (grupos)
    -   `role` (quick-debts)
    -   `date_from`, `date_to` (deudas) [opcional]

---

## Mapeo a Historias de Usuario

Grupos

-   Crear grupo: POST /groups
-   Ver detalles: GET /groups/{id}, GET /groups/{id}/overview, GET /groups/{id}/members, GET /groups/{id}/debts
-   Editar grupo: PATCH /groups/{id}, POST/DELETE /groups/{id}/members
-   Eliminar grupo: DELETE /groups/{id}
-   Listar grupos: GET /groups?membership=member
-   Listar mis grupos: GET /groups?membership=owner

Deudas

-   Registrar grupal: POST /groups/{id}/debts
-   Editar grupal: PATCH /groups/{id}/debts/{debtId}
-   Eliminar grupal: DELETE /groups/{id}/debts/{debtId}
-   Enlistar grupales: GET /groups/{id}/debts
-   Registrar rápida: POST /quick-debts
-   Editar rápida: PATCH /quick-debts/{id}
-   Eliminar rápida: DELETE /quick-debts/{id}
-   Enlistar rápidas: GET /quick-debts
-   Aceptar/Rechazar deuda: POST /debt-assignments/{id}/accept|reject y POST /quick-debt-assignments/{id}/accept|reject
-   Saldar deuda: POST /debt-assignments/{id}/settle y POST /quick-debts/{id}/settle
-   Transferencia: POST /debt-assignments/{id}/transfer (+ verify)

Balance

-   Consolidado: GET /balances/summary
-   Deuda total: GET /balances/total

Contactos

-   Agregar (solicitud): POST /contacts/requests
-   Agregar por QR: POST /contacts/qr/handshake
-   Editar contacto: PATCH /contacts/{id}
-   Eliminar contacto: DELETE /contacts/{id}
-   Listar contactos: GET /contacts
-   Ver detalles: GET /contacts/{id}

Notificaciones

-   Asignación de deuda: generado por POST /groups/{id}/debts o /quick-debts
-   Solicitud de ingreso a grupo: POST /groups/{id}/join-requests
-   Resolución de solicitud: POST /groups/join-requests/{id}/approve|reject
-   Invitación a grupo: POST /groups/{id}/invitations + accept/reject
-   Solicitud de contacto: POST /contacts/requests + accept/reject
-   Recordatorios periódicos: GET /notifications + preferencias

Notas finales

-   Autorizar por rol (OWNER vs MEMBER) en grupos
-   Validar pertenencia al grupo al acceder/editar
-   Notificaciones asíncronas (event-driven) al crear/editar/eliminar
-   Usar `Idempotency-Key` en POST para evitar duplicados por reintento

## No aplica ahorita

## Grupos

Resumen
| Método | Path | Descripción |
|---|---|---|
| POST | /groups | Crear grupo |
| GET | /groups?membership=memberID | Listar grupos donde soy miembro |
| GET | /groups?membership=ownerID | Listar grupos que creé |
| GET | /groups/{groupId} | Ver detalles del grupo |
| GET | /groups/{groupId}/overview | Datos para UI: totales, permisos, previews |
| GET | /groups/{groupId}/members | Listar miembros |
| POST | /groups/{groupId}/members | Agregar miembro |
| DELETE | /groups/{groupId}/members/{userId} | Eliminar miembro |
| PATCH | /groups/{groupId} | Editar grupo (nombre, descripción, miembros) |
| DELETE | /groups/{groupId} | Eliminar grupo |
| POST | /groups/{groupId}/invitations | Invitar usuario |
| POST | /group-invitations/{invitationId}/accept | Aceptar invitación |
| POST | /group-invitations/{invitationId}/reject | Rechazar invitación |

POST `/groups`

-   Body:

```json
{
    "name": "Viaje a CDMX",
    "description": "Gastos del viaje",
    "member_user_ids": ["uuid1", "uuid2"]
}
```

-   201:

```json
{
    "group_id": "uuid",
    "name": "Viaje a CDMX",
    "description": "Gastos del viaje",
    "owner_user_id": "uuidOwner",
    "members": [
        {
            "user_id": "uuidOwner",
            "role": "OWNER"
        },
        {
            "user_id": "uuid1",
            "role": "MEMBER"
        },
        {
            "user_id": "uuid2",
            "role": "MEMBER"
        }
    ],
    "created_at": "2025-10-10T12:00:00Z"
}
```

GET `/groups?membership=member`

-   body:

```json
{
    "items": [
        {
            "group_id": "...",
            "name": "Viaje a CDMX",
            "my_role": "MEMBER",
            "my_total_owed_cents": 1234
        }
    ],
    "next_cursor": "..."
}
```

-   200:

```json
{
    "items": [
        {
            "group_id": "...",
            "name": "Viaje a CDMX",
            "my_role": "MEMBER",
            "my_total_owed_cents": 1234
        }
    ]
}
```

GET `/groups?membership=owner`

-   200:

```json
{
    "items": [
        {
            "group_id": "...",
            "name": "Casa nueva",
            "my_role": "OWNER",
            "member_count": 4
        }
    ]
}
```

GET /groups/{groupId}

-   200:

```json
{
    "group_id": "...",
    "name": "Viaje a CDMX",
    "description": "Gastos del viaje",
    "owner_user_id": "...",
    "members": [
        {
            "user_id": "...",
            "first_name": "...",
            "last_name": "...",
            "role": "MEMBER"
        }
    ],
    "stats": {
        "my_total_owed_cents": 2300,
        "total_expenses_cents": 154000,
        "debts_count": 12
    },
    "actions": {
        "can_edit": true,
        "can_delete": true,
        "can_create_debt": true
    }
}
```

GET `/groups/{groupId}/overview`

-   200:

```json
{
    "my_total_owed_cents": 2300,
    "member_count": 5,
    "debts_preview": [
        {
            "debt_id": "...",
            "title": "Cena viernes",
            "date": "2025-10-28",
            "amount_cents": 120000
        }
    ],
    "members": [
        {
            "user_id": "...",
            "name": "..."
        }
    ],
    "actions": {
        "can_create_debt": true,
        "can_edit_group": true,
        "can_delete_group": true
    }
}
```

PATCH `/groups/{groupId}`

-   Body:

```json
{
    "name": "Nuevo nombre",
    "description": "Nueva descripción",
    "add_member_user_ids": ["uuidX", "uuidY"],
    "remove_member_user_ids": ["uuidZ"]
}
```

-   200:

```json
{
    "group_id": "uuid",
    "name": "Nuevo nombre",
    "description": "Nueva descripción",
    "members": [
        {
            "user_id": "uuidX"
        },
        {
            "user_id": "uuidY"
        }
    ]
}
```

DELETE `/groups/{groupId}`

-   204

POST `/groups/{groupId}/members`

-   Body:

```json
{
    "user_id": "uuid"
}
```

-   201

DELETE `/groups/{groupId}/members/{userId}`

-   204

POST `/groups/{groupId}/invitations`

-   Body:

```json
{
    "invitee_user_id": "uuid"
}
```

-   201:

```json
{
    "invitation_id": "...",
    "status": "PENDING"
}
```

POST `/group-invitations/{invitationId}/accept`

-   200:

```json
{
    "status": "ACCEPTED"
}
```

POST `/group-invitations/{invitationId}/reject`

```json
{
    "status": "REJECTED"
}
```

---

## Deudas grupales

Resumen
| Método | Path | Descripción |
|---|---|---|
| POST | /groups/{groupId}/debts | Registrar deuda grupal |
| GET | /groups/{groupId}/debts | Listar deudas del grupo |
| GET | /groups/{groupId}/debts/{debtId} | Ver detalle de deuda |
| PATCH | /groups/{groupId}/debts/{debtId} | Editar deuda grupal |
| DELETE | /groups/{groupId}/debts/{debtId} | Eliminar deuda grupal |
| POST | /debt-assignments/{assignmentId}/accept | Aceptar asignación |
| POST | /debt-assignments/{assignmentId}/reject | Rechazar asignación |
| POST | /debt-assignments/{assignmentId}/settle | Marcar asignación como saldada |
| POST | /debt-assignments/{assignmentId}/transfer | Subir evidencia de transferencia |
| POST | /debt-assignments/{assignmentId}/verify-transfer | Verificar evidencia (backoffice) |

Modelo de split:

```json
{
    "method": "equal | percentage | shares | custom",
    "participants": [
        {
            "user_id": "uuid1",
            "value": 1
        },
        {
            "user_id": "uuid2",
            "value": 1
        }
    ]
}
```

POST `/groups/{groupId}/debts`

-   Body:

```json
{
    "title": "Cena viernes",
    "description": "Restaurante",
    "amount_cents": 120000,
    "date": "2025-10-28",
    "payer_user_id": "uuidPayer",
    "split": {
        "method": "equal",
        "participants": [
            {
                "user_id": "uuidPayer",
                "value": 1
            },
            {
                "user_id": "uuid2",
                "value": 1
            }
        ]
    }
}
```

-   201:

```json
{
    "debt_id": "uuidDebt",
    "group_id": "uuidGroup",
    "title": "Cena viernes",
    "amount_cents": 120000,
    "date": "2025-10-28",
    "payer_user_id": "uuidPayer",
    "assignments": [
        {
            "assignment_id": "a1",
            "debtor_user_id": "uuidPayer",
            "amount_cents": 60000,
            "status": "PENDING"
        },
        {
            "assignment_id": "a2",
            "debtor_user_id": "uuid2",
            "amount_cents": 60000,
            "status": "PENDING"
        }
    ]
}
```

GET `/groups/{groupId}/debts?limit=50&cursor=...`

-   200:

```json
{
    "items": [
        {
            "debt_id": "uuidDebt",
            "title": "Cena viernes",
            "amount_cents": 120000,
            "date": "2025-10-28",
            "payer_user_id": "uuidPayer",
            "my_assignment_status": "PENDING"
        }
    ],
    "next_cursor": "opaque"
}
```

GET `/groups/{groupId}/debts/{debtId}`

-   200:

```json
{
    "debt_id": "uuidDebt",
    "group_id": "uuidGroup",
    "title": "Cena viernes",
    "description": "Restaurante",
    "amount_cents": 120000,
    "date": "2025-10-28",
    "payer_user_id": "uuidPayer",
    "assignments": [
        {
            "assignment_id": "a1",
            "debtor_user_id": "uuidPayer",
            "amount_cents": 60000,
            "status": "PENDING"
        },
        {
            "assignment_id": "a2",
            "debtor_user_id": "uuid2",
            "amount_cents": 60000,
            "status": "PENDING"
        }
    ],
    "actions": {
        "can_edit": true,
        "can_delete": true,
        "can_accept_or_reject": true,
        "can_settle": true
    }
}
```

PATCH `/groups/{groupId}/debts/{debtId}`

-   Body (parcial):

```json
{
    "title": "...",
    "description": "...",
    "amount_cents": 130000,
    "date": "2025-10-29",
    "split": {
        "method": "shares",
        "participants": [
            {
                "user_id": "uuid1",
                "value": 2
            },
            {
                "user_id": "uuid2",
                "value": 1
            }
        ]
    }
}
```

-   200:

```json
{}
```

DELETE `/groups/{groupId}/debts/{debtId}`

-   204

POST `/debt-assignments/{assignmentId}/accept`

-   200:

```json
{
    "status": "ACCEPTED"
}
```

POST `/debt-assignments/{assignmentId}/reject`

```json
{
    "status": "REJECTED"
}
```

POST `/debt-assignments/{assignmentId}/settle`

-   Body opcional:

```json
{
    "note": "Pagado en efectivo",
    "evidence": {
        "type": "CASH",
        "reference": "..."
    }
}
```

-   200:

```json
{
    "status": "SETTLED",
    "settled_at": "2025-10-30T01:00:00Z"
}
```

POST `/debt-assignments/{assignmentId}/transfer`

-   Body:

```json
{
    "amount_cents": 60000,
    "date": "2025-10-29T18:00:00Z",
    "bank_reference": "ABC123",
    "attachment_url": "https://..."
}
```

-   202:

```json
{
    "status": "PENDING_VERIFICATION"
}
```

POST `/debt-assignments/{assignmentId}/verify-transfer`

-   Body:

```json
{
    "approved": true,
    "note": "Comprobante válido"
}
```

-   200:

```json
{
    "status": "SETTLED"
}
```

---
