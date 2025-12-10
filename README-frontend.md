# Frontend - Diario Ciudadano

Instrucciones rápidas para desarrollo y despliegue del frontend (Angular).

- Desarrollo local (usa el proxy para enviar /api a backend en `http://localhost:8085`):

```powershell
npm install
ng serve --proxy-config proxy.conf.json --port 4200
```

- Build producción y ejecutar en Docker (asegúrate que `angular.json` genera los artefactos en `dist/`):

```powershell
docker build -t diario-ciudadano-fe:latest .
docker run --rm -p 4200:80 diario-ciudadano-fe:latest
```

- Con `docker-compose` (accede al backend en Windows host desde contenedor):

```powershell
docker compose -f docker-compose-fe.yml up --build
```

Notas importantes:
- El proxy `proxy.conf.json` redirige llamadas `/api` a `http://localhost:8085` en desarrollo.
- El backend que me compartiste expone APIs bajo `/api/corresponsales` y `/api/noticias`.
- Registra `HttpClientModule` en `AppModule` y agrega los servicios creados (`ApiService`, `CorresponsalService`, `NoticiaService`).
- Para subida de archivos desde Angular usa `FormData` y envía `fotos` (array) y `video` como campos multipart.
