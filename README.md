# calculation-api

# Justificación de las decisiones técnicas tomadas:

## 1. Cálculo con porcentaje dinámico:
Se utilizó **DDD** para la implementación de la API `calculation-api`: es un patrón que permite extender de forma sencilla cualquier funcionalidad posterior que se desee agregar.  
Para complementar, se creó una API llamada `return-percentage-api` que devuelve un valor mockeado.

## 2. Caché del porcentaje:
Para resolver este punto, se utilizó un servidor **Redis** junto con la utilización de la librería **Redisson**, que ya tiene los métodos necesarios para setear claves y sus TTLs asociados. Lo que sucede es que se realiza un `put` hacia Redis con un TTL de 30 minutos y Redis se encarga de invalidar automáticamente la clave al cumplirse el tiempo estipulado. Se utilizo un servidro Redis po run tema de escalabilidad horizontal y que todas las intancias de l api puedan consultar el mismo valor. Si bien el ejercicio indica que "el porcentaje obtenido del servicio externo debe almacenarse ... etc etc" (y que asi lo desarrolle) lo mejor es que la api de calculo no se encargue de setear la Key en redis sino solo leerla y que la api externa sea la encargada de grabar el redis y controlar los TTLs. Luego la api y todas sus instancias en caso de no tener un valor en el redis disponible tendria a disposicion un EP para consultar sync un porcentage.
## 3. Reintentos ante fallos del servicio externo:
Se implementó una lógica de **Retry** usando `spring-retry` y `aspectjweaver`, cumpliendo con el objetivo solicitado en este punto. La API utiliza el valor almacenado en caché; en caso de no existir (porque superó los 30 minutos), intenta conectar con la API externa para obtener el porcentaje y setear una clave nuevamente en Redis.  
En caso de no poder conectar, se reintenta 3 veces y, finalmente, si no logra su objetivo, se lanza un error personalizado `ExternalServiceException`.

## 4. Historial de llamadas:
Se implementó un **filtro** para capturar todas las peticiones con el fin de poder grabar toda la información específica requerida para posterior consulta.

**¿Por qué un filtro y no un interceptor?**  
El problema principal con los interceptores es que no permiten manipular directamente los streams del response porque:
- El interceptor opera en un nivel más alto del ciclo de vida de la solicitud, cerca del controlador. En este punto, el cuerpo de la respuesta (`OutputStream`) no está disponible todavía o ya se ha enviado al cliente.
- Aunque se puede acceder al `HttpServletResponse` desde un interceptor, no puedo capturar o envolver el `OutputStream` de forma sencilla.

Por otro lado, los filtros son más adecuados porque:
- Actúan antes de que se escriba la respuesta: puedo envolver el `HttpServletResponse` con un `HttpServletResponseWrapper` para interceptar la escritura en el `OutputStream` o `Writer`.
- Control del flujo completo: puedo capturar, modificar o leer el contenido de la respuesta antes de que se envíe al cliente.

La implementación me permitió grabar todas las peticiones, respuestas y errores procesados.

## 5. Control de tasas (Rate Limiting):
Se utilizó 1 **interceptor** y 1 **Wrapper** para controlar el **Rate Limit** de peticiones permitidas a la aplicación. Los interceptores permiten aplicar una lógica consistente y centralizada para gestionar la limitación de tasa en todas las llamadas a la API, en lugar de tener que implementar esta lógica en cada punto individual que llama a la API. En este caso, se implementó en el `preHandle` la lógica para controlar el acceso a no más de 3 peticiones por minuto.  
Para esto, se utilizó el método `tryAcquire()` del objeto `RRateLimiter` de **RedissonAPI**, que emite un “permiso” de acuerdo a la configuración del rate limit que, como se ve en el código de la API, se configuró de la siguiente manera:

rateLimiter.trySetRate(RateType.OVERALL, 3, Duration.ofMinutes(1));

En caso de exceder el límite, se devuelve un error **429 Too Many Requests** y se guarda el evento en la base de datos para posterior consulta.

## 6. Manejo de errores HTTP:
Se controlan con errores personalizados y/o genéricos de acuerdo a cada situación. En la API se utilizó `@RestControllerAdvice` para la gestión centralizada de errores.