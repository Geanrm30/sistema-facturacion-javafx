# Sistema de Facturación JavaFX

Aplicación de escritorio para facturación minorista desarrollada con **JavaFX 21**, **Maven**, **Lombok** y **PostgreSQL**.
Programación de Aplicaciones de Escritorio — Universidad Americana (UAM).

## Funcionalidades

- Menú principal (`MenuBar` + `ToolBar`) con navegación a los módulos de Productos y Categorías.
- Conexión a PostgreSQL mediante JDBC y capa DAO (`CategoriaDAO`, `ProductoDAO`).
- **CRUD de categorías** con validación de nombres duplicados (sin distinguir mayúsculas) y activación/desactivación.
- **CRUD de productos** con validaciones de campos, imagen del producto (botón *Examinar*, se copia a `images/productos`) y estado activo/inactivo.
- Búsqueda por código, nombre o categoría, y filtros por estado y categoría.
- Validaciones de negocio:
  - Código de producto único.
  - Aviso (con opción de continuar) si el nombre de producto ya existe, sin distinguir mayúsculas.
  - Al actualizar, el aviso de nombre repetido solo aparece si el nombre fue modificado.
  - Solo se pueden asignar categorías activas a un producto.
- Al hacer clic en una fila de la tabla se cargan sus datos en el formulario; al hacer clic de nuevo sobre la misma fila se deselecciona y se limpia el formulario.

## Estructura

```
src/main/
├── java/
│   ├── module-info.java
│   └── ni/edu/uam/facturacion/
│       ├── application/FacturacionApplication.java
│       ├── controller/  MenuPrincipalController, ProductoController, CategoriaController
│       ├── dao/         ProductoDAO, CategoriaDAO
│       ├── model/       Categoria, Producto, Cargo, Empleado
│       └── util/        SceneManager, DatabaseConnection
└── resources/ni/edu/uam/facturacion/
    ├── fxml/    menu-principal.fxml, producto-view.fxml, categoria-view.fxml
    ├── images/  logo.png, productos/
    └── icons/   agregar.png, cerrar.png
```

## Requisitos

- JDK 21
- Maven (se incluye el wrapper `mvnw`)
- PostgreSQL con la base de datos `tienda_javafx` en `localhost:5432` (usuario `postgres`)
- Variable de entorno `DB_PASSWORD` con la contraseña de la base de datos
- IntelliJ IDEA con *Annotation Processing* habilitado (para Lombok)

## Ejecución

```bash
./mvnw clean javafx:run
```

## Pruebas realizadas

### Validaciones del módulo Categoría

| Prueba | Datos | Resultado esperado |
|--------|-------|--------------------|
| Nombre vacío | `""` | Impide el registro |
| Nombre con espacios | `" "` | Impide el registro |
| Nombre duplicado | `Computadoras` | Muestra advertencia |
| Eliminar con productos | Categoría utilizada | Impide la eliminación |
| Sin selección | Presionar actualizar | Solicita seleccionar una categoría |

### Validaciones del módulo Producto

| Prueba | Valor ingresado | Resultado esperado |
|--------|-----------------|--------------------|
| Código vacío | Vacío | No guarda |
| Código duplicado | `PRD001` existente | No guarda |
| Nombre vacío | Vacío | No guarda |
| Nombre repetido | `Pan` / `PAn` | Pregunta si desea continuar (sin distinguir mayúsculas) |
| Actualizar sin cambiar el nombre | Editar otro campo | No aparece el aviso de nombre repetido |
| Sin categoría | ComboBox vacío | No guarda |
| Precio texto | `abc` | Muestra error |
| Precio cero | `0` | No guarda |
| Precio negativo | `-15.50` | No guarda |
| Existencia texto | `diez` | Muestra error |
| Existencia decimal | `10.5` | Muestra error |
| Existencia negativa | `-3` | No guarda |

### Interfaz y otras pruebas

| Prueba | Resultado esperado |
|--------|--------------------|
| Abrir Productos y Categorías desde MenuBar y ToolBar | Se abre el formulario correspondiente |
| Registrar datos válidos | El registro se guarda en la base de datos y aparece en `TableView` |
| Clic dos veces sobre la misma fila de la tabla | Primero se cargan los datos, luego se limpian |
| Simular error de conexión (sin `DB_PASSWORD` o servidor apagado) | Mensaje de error y la aplicación sigue funcionando |
| Compilar el proyecto | Compila sin errores |

## Autor

- Geanfranco Rodriguez — [@Geanrm30](https://github.com/Geanrm30)
