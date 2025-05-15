# Proyecto de Navegación Universitaria

## Descripción General

Este proyecto consiste en una aplicación móvil para Android diseñada para mejorar la experiencia de navegación dentro de un campus universitario.  La aplicación busca facilitar a los estudiantes, profesores y visitantes la localización de aulas, oficinas, servicios y otros puntos de interés dentro de la universidad.  Además de la navegación básica, la aplicación ofrece funcionalidades adicionales como gestión de perfiles de usuario y un sistema de sugerencias.

La aplicación se desarrolla utilizando el lenguaje de programación Kotlin y el entorno de desarrollo Android Studio.  Se utiliza Firebase como plataforma para la gestión de la autenticación de usuarios y el almacenamiento de datos, lo que permite una mayor escalabilidad y mantenibilidad de la aplicación.

## Objetivos

Los objetivos principales de este proyecto son:

* Proporcionar una herramienta de navegación intuitiva y eficiente para el entorno universitario.
* Facilitar el acceso a la información relevante sobre la ubicación de los diferentes recursos del campus.
* Mejorar la experiencia del usuario dentro del campus, reduciendo el tiempo dedicado a la búsqueda de ubicaciones.
* Ofrecer un canal de comunicación entre los usuarios y la administración de la universidad a través del sistema de sugerencias.

## Funcionalidades en Detalle

La aplicación ofrece las siguientes funcionalidades:

* **Autenticación de Usuarios:**
    * Los usuarios pueden crear una cuenta utilizando su dirección de correo electrónico y contraseña.
    * Los usuarios pueden iniciar sesión en la aplicación utilizando sus credenciales.
    * La aplicación utiliza Firebase Authentication para gestionar de forma segura la autenticación de usuarios.
    * Se incluye una funcionalidad de recuperación de contraseña para los usuarios que hayan olvidado sus credenciales.

* **Gestión de Perfiles de Usuario:**
    * Cada usuario tiene un perfil que contiene información personal y académica.
    * Los usuarios pueden ver y editar la siguiente información en su perfil:
        * Nombre completo
        * Número de cédula
        * Departamento
        * Municipio
        * Sexo
        * Etnia
        * Correo electrónico
        * Número de teléfono
        * Correo electrónico institucional
    * La información del perfil de usuario se almacena en la base de datos de Firebase Firestore.

* **Navegación por el Campus:**
    * La aplicación muestra un mapa interactivo del campus universitario.
    * El mapa está dividido por pisos, permitiendo a los usuarios seleccionar el piso que desean visualizar.
    * Los usuarios pueden hacer zoom y desplazarse por el mapa para explorar las diferentes áreas del campus.
    * La aplicación permite a los usuarios seleccionar un punto de inicio y un punto de destino en el mapa.
    * La aplicación calcula y muestra la ruta más corta entre los dos puntos seleccionados, utilizando el algoritmo de Dijkstra.
    * Se destacan en el mapa los puntos de interés relevantes, como escaleras, baños y otros servicios.

* **Sistema de Sugerencias:**
    * Los usuarios pueden enviar sugerencias a los administradores de la aplicación.
    * Las sugerencias pueden incluir comentarios sobre la aplicación, reportes de errores o solicitudes de nuevas funcionalidades.
    * Los usuarios pueden ver un historial de las sugerencias que han enviado.
    * Las sugerencias se almacenan en la base de datos de Firebase Firestore.

## Arquitectura de la Aplicación

La aplicación sigue una arquitectura basada en componentes, donde cada funcionalidad se implementa en una o varias actividades y vistas.  Se utiliza Firebase para la gestión de la lógica del lado del servidor, incluyendo la autenticación de usuarios y el almacenamiento de datos.  El mapa interactivo se implementa utilizando una vista personalizada (`MapaCanvasView`) que gestiona la visualización y la interacción del usuario con el mapa.

## Tecnologías Utilizadas

* **Lenguaje de Programación:** Kotlin
* **Entorno de Desarrollo:** Android Studio
* **Plataforma de Desarrollo:** Android SDK
* **Base de Datos y Autenticación:** Firebase (Authentication y Firestore)
* **Algoritmo de Búsqueda de Rutas:** Algoritmo de Dijkstra

## Instalación y Configuración

Para instalar y ejecutar la aplicación en un entorno de desarrollo, siga estos pasos:

1.  **Clonar el Repositorio:** Clona el repositorio de Git en tu máquina local utilizando el siguiente comando:
    ```bash
    git clone <URL del repositorio>
    ```
2.  **Abrir el Proyecto en Android Studio:** Abre el proyecto clonado en Android Studio.
3.  **Configurar un Dispositivo Virtual o Físico:** Asegúrate de tener un emulador de Android configurado o un dispositivo Android físico conectado a tu computadora.
4.  **Configurar Firebase:**
    * Crea un proyecto en la consola de Firebase.
    * Habilita los servicios de Authentication y Firestore para tu proyecto.
    * Descarga el archivo `google-services.json` de tu proyecto de Firebase.
    * Copia el archivo `google-services.json` en el directorio `app/` de tu proyecto de Android Studio.
5.  **Ejecutar la Aplicación:** Ejecuta la aplicación en Android Studio.

## Contribuciones

Las contribuciones a este proyecto son bienvenidas. Si deseas contribuir, por favor, sigue estos pasos:

1.  Haz un fork del repositorio.
2.  Crea una rama para tu contribución (`git checkout -b mi-contribucion`).
3.  Realiza los cambios y haz commit de ellos (`git commit -m "Descripción de la contribución"`).
4.  Sube los cambios a tu fork (`git push origin mi-contribucion`).
5.  Crea un pull request.

## Licencia

[Indicar la licencia del proyecto]

## Autores
Daniel Godoy
Camilo Barragan
Diego Peña
Juan Cuastumal 
