# Frontend Mueblerix - React

## 📁 Estructura del Proyecto

```
src/
├── components/
│   ├── app/
│   │   ├── Login.jsx              # Página de inicio de sesión
│   │   ├── Login.css
│   │   ├── Dashboard.jsx          # Panel principal
│   │   ├── Dashboard.css
│   │   ├── ChangePassword.jsx     # Componente de cambio de contraseña
│   │   └── ChangePassword.css
│   └── ProtectedRoute.jsx         # HOC para proteger rutas
├── context/
│   └── AuthContext.jsx            # Contexto de autenticación
├── services/
│   ├── api/
│   │   └── apiClient.js           # Cliente axios configurado
│   ├── authService.js             # Servicio de autenticación
│   └── userService.js             # Servicio de usuarios
├── config/
│   └── constants.js               # Constantes y configuración
├── App.jsx                        # Componente principal con rutas
├── App.css
├── main.jsx
└── index.css
```

## 🚀 Características Implementadas

### ✅ Autenticación
- Login con validación de credenciales
- Almacenamiento seguro de tokens en localStorage
- Protección de rutas privadas
- Interceptores de axios para agregar tokens automáticamente

### ✅ Gestión de Usuarios
- Cambio de contraseña con validaciones:
  - Mínimo 8 caracteres
  - Al menos una mayúscula
  - Al menos una minúscula
  - Al menos un número
  - Al menos un carácter especial (@#$%^&+=!)
- Modal de bienvenida en el primer login

### ✅ Dashboard
- Panel principal con tarjetas de módulos
- Información del usuario actual
- Botón de cerrar sesión
- Diseño responsive y moderno

### ✅ Diseño
- Interfaz moderna y atractiva
- Animaciones suaves
- Diseño responsive para móviles y tablets
- Gradientes y efectos visuales

## 🛠️ Tecnologías Utilizadas

- **React 19** - Framework principal
- **React Router DOM** - Enrutamiento
- **Axios** - Cliente HTTP
- **Context API** - Gestión de estado
- **CSS3** - Estilos y animaciones
- **Vite** - Build tool

## 📦 Instalación y Uso

### 1. Instalar dependencias
```bash
cd frontend
npm install
```

### 2. Configurar la URL del backend
Edita `src/config/constants.js` si tu backend no está en `http://localhost:8080`:

```javascript
export const API_BASE_URL = 'http://localhost:8080/api/v1';
```

### 3. Iniciar el servidor de desarrollo
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

### 4. Construir para producción
```bash
npm run build
```

## 🔐 Credenciales de Prueba

Usuario creado automáticamente por el backend:
- **Usuario**: `1234567890`
- **Contraseña**: `Admin123!`

## 🎯 Principios SOLID Aplicados

### Single Responsibility Principle (SRP)
- Cada servicio tiene una única responsabilidad
- `authService.js` solo maneja autenticación
- `userService.js` solo maneja operaciones de usuario

### Open/Closed Principle (OCP)
- Componentes reutilizables y extensibles
- `ProtectedRoute` puede extenderse para diferentes niveles de acceso

### Liskov Substitution Principle (LSP)
- Componentes intercambiables con misma interfaz

### Interface Segregation Principle (ISP)
- Hooks específicos para cada funcionalidad
- Context API con solo lo necesario

### Dependency Inversion Principle (DIP)
- Servicios independientes de componentes
- Inyección de dependencias mediante Context

## 📱 Rutas de la Aplicación

- `/login` - Página de inicio de sesión
- `/dashboard` - Panel principal (requiere autenticación)
- `/` - Redirecciona a dashboard

## 🎨 Paleta de Colores

- **Primario**: `#667eea` - `#764ba2` (gradiente)
- **Azul**: `#4A90E2`
- **Verde**: `#50C878`
- **Naranja**: `#FFB347`
- **Morado**: `#9370DB`
- **Rojo**: `#ff4757`

## 📋 Funcionalidades del Modal de Bienvenida

Al iniciar sesión, el usuario verá un modal con dos opciones:
1. **Cambiar Contraseña**: Redirige al formulario de cambio de contraseña
2. **Continuar sin cambiar**: Cierra el modal y accede al dashboard

El modal se muestra solo una vez por sesión (usa sessionStorage).

## 🔧 Manejo de Errores

- Mensajes de error claros y descriptivos
- Validación en el frontend y backend
- Manejo de errores de conexión
- Redirección automática al login si el token expira

## 📊 Estado de Autenticación

El estado de autenticación se gestiona globalmente mediante Context API:
- `user`: Información del usuario actual
- `isAuthenticated`: Boolean que indica si está autenticado
- `loading`: Boolean para estados de carga
- `login()`: Función para iniciar sesión
- `logout()`: Función para cerrar sesión

## 🚧 Próximas Mejoras

- [ ] Implementar módulos de Productos, Proformas, Clientes y Reportes
- [ ] Agregar paginación en listados
- [ ] Implementar búsqueda y filtros
- [ ] Agregar notificaciones toast
- [ ] Modo oscuro
- [ ] Internacionalización (i18n)
- [ ] Tests unitarios y de integración

## 📝 Notas Importantes

- Asegúrate de que el backend esté corriendo en `http://localhost:8080`
- Los tokens se almacenan en localStorage
- La sesión persiste hasta que el usuario cierre sesión o el token expire
- El modal de bienvenida usa sessionStorage para mostrarse solo una vez por sesión

## 🐛 Solución de Problemas

### Error de CORS
Si encuentras errores de CORS, verifica que el backend tenga configurado CORS para permitir solicitudes desde `http://localhost:5173`.

### Error de conexión
Verifica que:
1. El backend esté corriendo
2. La URL en `constants.js` sea correcta
3. El puerto del backend sea el correcto

### Token expirado
Si el token expira, serás redirigido automáticamente al login.

## 👥 Soporte

Para cualquier pregunta o problema, contacta al equipo de desarrollo.
