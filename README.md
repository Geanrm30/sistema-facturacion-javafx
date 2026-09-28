# Sistema de Facturación JavaFX

Aplicación de escritorio para facturación minorista desarrollada con **JavaFX 21**, **Maven** y **Lombok**.
Programación de Aplicaciones de Escritorio — Universidad Americana (UAM).

## Semana 4 · Base profesional del proyecto

- Proyecto Maven limpio, sin las clases de demostración (`HelloApplication`, `HelloController`, `hello-view.fxml`).
- Paquete base `ni.edu.uam.facturacion` organizado por responsabilidades.
- Modelos del dominio con Lombok: `Categoria`, `Producto`, `Cargo` y `Empleado`.
- Menú principal (`MenuBar` + `ToolBar`) con navegación al módulo de productos.
- Formulario de productos con validaciones, `Alert` y listado temporal en `TableView`.

## Estructura

```
src/main/
├── java/
│   ├── module-info.java
│   └── ni/edu/uam/facturacion/
│       ├── application/FacturacionApplication.java
│       ├── controller/  MenuPrincipalController, ProductoController
│       ├── model/       Categoria, Producto, Cargo, Empleado
│       └── util/SceneManager.java
└── resources/ni/edu/uam/facturacion/
    ├── fxml/    menu-principal.fxml, producto-view.fxml
    ├── images/  logo.png
    └── icons/   agregar.png, cerrar.png
```

## Requisitos
 
- JDK 21
- Maven (se incluye el wrapper `mvnw`)
- IntelliJ IDEA con *Annotation Processing* habilitado (para Lombok)

## Ejecución

```bash
./mvnw clean javafx:run
```

## Pruebas realizadas

| # | Prueba | Resultado esperado |
|---|--------|--------------------|
| 1 | Ejecutar el proyecto | Aparece el menú principal sin clases Hello |
| 2 | Abrir Productos desde MenuBar y ToolBar | Ambos abren el mismo formulario |
| 3 | Guardar campos vacíos | Aparece una advertencia |
| 4 | Ingresar texto en precio o existencia | Aparece un error |
| 5 | Registrar precio cero o existencia negativa | El registro se rechaza |
| 6 | Registrar datos válidos | El producto aparece en `TableView` |
| 7 | Compilar el proyecto | Compila sin errores |

## Autor

- Geanfranco Rodriguez — [@Geanrm30](https://github.com/Geanrm30)
