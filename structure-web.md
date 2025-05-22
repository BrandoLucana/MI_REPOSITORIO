# Estructura Básica de un Proyecto Web

## ¿Qué es una estructura web?

La **estructura web** es la organización y disposición de los archivos y carpetas que componen un sitio web. Una buena estructura web es fundamental para garantizar:

- **Navegación fluida**: Permite que los usuarios encuentren fácilmente la información.
- **Gestión eficiente del contenido**: Facilita la actualización, mantenimiento y escalabilidad del sitio.
- **Optimización para motores de búsqueda (SEO)**: Una estructura bien organizada ayuda a los motores de búsqueda a rastrear y indexar el sitio correctamente.

## Estructura de un Proyecto Web

Existen diferentes tipos de estructuras web, dependiendo del tipo de proyecto que se esté desarrollando. A continuación, se presentan algunos ejemplos de estructuras web comunes:

### 1. **Estructura Simple para un Sitio Estático**

Un sitio web estático básico suele tener una estructura muy sencilla. Es ideal para sitios con contenido que no cambia frecuentemente.

```plaintext
/ (Raíz del Proyecto)
│
├── /index.html            # Página principal
├── /about.html            # Página sobre nosotros
├── /contact.html          # Página de contacto
│
├── /assets/               # Carpeta para recursos estáticos
│   ├── /css/              # Archivos CSS
│   ├── /images/           # Imágenes del sitio
│   └── /js/               # Archivos JavaScript
│
└── /favicon.ico           # Icono de la página
