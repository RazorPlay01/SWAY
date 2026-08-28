# 📚 Documentación de la API de SWAY

**SWAY** es un mod de Minecraft que hace que la vegetación (hierba, flores, helechos, etc.) se deforme e interactúe físicamente cuando las entidades (jugadores, mobs) pasan a través de ellas. Este documento explica en detalle cómo utilizar la API pública del mod para que otros desarrolladores puedan hacer que sus plantas personalizadas sean compatibles con SWAY.

---

## 📋 Tabla de Contenidos

1. [Introducción](#-introducción)
2. [Configuración del Proyecto](#-configuración-del-proyecto)
3. [Registro Básico de Bloques](#-registro-básico-de-bloques)
4. [Sistema de Comportamientos (Behaviors)](#-sistema-de-comportamientos-behaviors)
5. [Tipos de Contribuidores](#-tipos-de-contribuidores)
6. [Pipeline de Comportamientos](#-pipeline-de-comportamientos)
7. [Comportamientos Integrados](#-comportamientos-integrados)
8. [Contexto y Acumulador de Fuerzas](#-contexto-y-acumulador-de-fuerzas)
9. [Deformación de Vértices](#-deformación-de-vértices)
10. [Configuración del Mod](#-configuración-del-mod)
11. [Ejemplos Completos](#-ejemplos-completos)
12. [Referencia Rápida de Métodos](#-referencia-rápida-de-métodos)

---

## 🚀 Introducción

La API de SWAY está diseñada para ser **simple y extensible**. Permite a los desarrolladores:

- **Registrar bloques personalizados** para que se deformen con el viento y las entidades.
- **Crear comportamientos personalizados** (behaviors) que controlen cómo interactúan los bloques.
- **Controlar la deformación** a nivel de vértices para efectos visuales avanzados.
- **Gestionar estructuras multibloque** (como plantas de dos bloques de alto).

### Paquete Principal

Toda la API pública se encuentra en el paquete:

```
com.github.razorplay01.sway.api
```

---

## ⚙️ Configuración del Proyecto

Para usar la API de SWAY en tu mod, necesitas:

1. **Añadir SWAY como dependencia** en tu archivo de build (Fabric, Forge o NeoForge).
2. **Importar las clases necesarias** en tu código:

```java
import com.github.razorplay01.sway.api.SwayAPI;
import com.github.razorplay01.sway.api.behavior.BehaviorKey;
import com.github.razorplay01.sway.api.behavior.SwayBehavior;
import com.github.razorplay01.sway.api.behavior.BehaviorPipeline;
import com.github.razorplay01.sway.api.behavior.contributors.*;
import com.github.razorplay01.sway.api.behavior.context.*;
```

---

## 🌿 Registro Básico de Bloques

La forma más sencilla de hacer que un bloque personalizado sea compatible con SWAY es usar el método `register()`:

```java
// Registra tu bloque personalizado con un multiplicador de intensidad de 1.2x
SwayAPI.register(MyBlocks.CUSTOM_PLANT, 1.2f);
```

### Métodos de Registro

| Método | Descripción |
|--------|-------------|
| `SwayAPI.register(Block block, float multiplier)` | Registra un bloque con un multiplicador de intensidad. Este método configura automáticamente un pipeline completo con los comportamientos integrados. |
| `SwayAPI.isInteractive(Block block)` | Verifica si un bloque es interactivo (tiene un pipeline registrado). |
| `SwayAPI.getMultiplier(BlockState state)` | Obtiene el multiplicador de intensidad de un bloque. |
| `SwayAPI.getRegistry()` | Devuelve el mapa de registros heredados (bloque → multiplicador). |

### Ejemplo de Registro

```java
public class MyModInitializer {
    public static void onInitialize() {
        // Registro simple con multiplicador
        SwayAPI.register(MyBlocks.CUSTOM_FLOWER, 1.0f);
        SwayAPI.register(MyBlocks.CUSTOM_TALL_GRASS, 1.5f);
        SwayAPI.register(MyBlocks.CUSTOM_BUSH, 0.8f);
        
        // Verificar si un bloque es interactivo
        boolean isInteractive = SwayAPI.isInteractive(MyBlocks.CUSTOM_FLOWER);
    }
}
```

---

## 🧩 Sistema de Comportamientos (Behaviors)

El sistema de comportamientos es el núcleo de la API de SWAY. Un **behavior** es una interfaz que define cómo un bloque interactúa con las entidades.

### BehaviorKey

Cada comportamiento se identifica mediante una `BehaviorKey`, que es un identificador único con namespace y path:

```java
// Crear una key personalizada
BehaviorKey myKey = BehaviorKey.create("mymod", "my_behavior");

// Crear una key en el namespace de SWAY
BehaviorKey vanillaKey = BehaviorKey.fromVanilla("my_behavior");

// Obtener información de la key
String namespace = myKey.getNamespace(); // "mymod"
String path = myKey.getPath();           // "my_behavior"
```

### SwayBehavior

La interfaz base `SwayBehavior` define métodos opcionales que todos los comportamientos pueden implementar:

```java
public interface SwayBehavior {
    // Determina si el comportamiento aplica a un estado de bloque específico
    default boolean appliesTo(BlockState state) {
        return true;
    }

    // Sobrescribe la tasa de decaimiento (null = usar valor por defecto)
    default Float getDecayRateOverride() {
        return null;
    }

    // Sobrescribe la suavidad de interpolación (null = usar valor por defecto)
    default Float getSmoothnessOverride() {
        return null;
    }
}
```

### Registro de Comportamientos

```java
// Registrar un comportamiento personalizado
SwayAPI.registerBehavior(myKey, new MyCustomBehavior());
```

> ⚠️ **Importante**: Si intentas registrar un comportamiento con una key que ya existe, se lanzará una `IllegalArgumentException`.

---

## 🎯 Tipos de Contribuidores

Los comportamientos se clasifican en **cuatro tipos de contribuidores**, cada uno con una función específica. Un comportamiento puede implementar múltiples interfaces.

### 1. CollisionContributor (Colisión)

Controla el área de búsqueda y qué bloques se ven afectados por una entidad.

```java
public interface CollisionContributor extends SwayBehavior {
    // Define el área de búsqueda alrededor de la entidad
    default AABB contributeSearchArea(Entity entity, SwayBehaviorContext ctx) {
        return entity.getBoundingBox().inflate(1);
    }

    // Determina si un bloque específico debe verse afectado
    default boolean shouldAffectBlock(BlockPos pos, BlockState state, Entity entity, SwayBehaviorContext ctx) {
        return true;
    }
}
```

**Ejemplo:**

```java
public class MyCollisionBehavior implements CollisionContributor {
    @Override
    public AABB contributeSearchArea(Entity entity, SwayBehaviorContext ctx) {
        // Ampliar el área de búsqueda a 3 bloques de radio
        return entity.getBoundingBox().inflate(3, 1, 3);
    }

    @Override
    public boolean shouldAffectBlock(BlockPos pos, BlockState state, Entity entity, SwayBehaviorContext ctx) {
        // Solo afectar bloques que no estén en agua
        return !state.getFluidState().is(Fluids.WATER);
    }
}
```

### 2. ForceContributor (Fuerza)

Contribuye fuerza al acumulador, determinando la dirección e intensidad del movimiento.

```java
public interface ForceContributor extends SwayBehavior {
    // Contribuye fuerza al acumulador
    default void contributeForce(BlockPos pos, BlockState state, Entity entity,
                                  SwayBehaviorContext ctx, ForceAccumulator accumulator) {
    }

    // Limita la intensidad máxima propuesta
    default float clampMaxIntensity(float proposedIntensity, SwayBehaviorContext ctx) {
        return proposedIntensity;
    }
}
```

**Ejemplo:**

```java
public class MyForceBehavior implements ForceContributor {
    @Override
    public void contributeForce(BlockPos pos, BlockState state, Entity entity,
                                 SwayBehaviorContext ctx, ForceAccumulator accumulator) {
        // Calcular dirección desde la entidad al bloque
        Vec3 entityPos = entity.position();
        double dx = (pos.getX() + 0.5) - entityPos.x;
        double dz = (pos.getZ() + 0.5) - entityPos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        if (dist < 2.0) {
            float force = (float) (1.0 - dist / 2.0) * 2.0f;
            float nx = (float) (dx / dist);
            float nz = (float) (dz / dist);
            accumulator.contribute(nx, nz, force, ForceAccumulator.CombineStrategy.ADD);
        }
    }
}
```

### 3. MultiBlockContributor (Multibloque)

Gestiona estructuras de múltiples bloques (como plantas de dos bloques de alto).

```java
public interface MultiBlockContributor extends SwayBehavior {
    // Obtiene la posición ancla del bloque (el bloque base de la estructura)
    default BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
        return currentPos;
    }

    // Obtiene los bloques vinculados al ancla
    default Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
        return Collections.emptyList();
    }

    // Determina si la fuerza debe propagarse a los bloques vinculados
    default boolean shouldPropagateForceToLinked() {
        return true;
    }
}
```

**Ejemplo (planta de dos bloques):**

```java
public class MyDoublePlantBehavior implements MultiBlockContributor {
    @Override
    public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
        // Si es la mitad superior, el ancla es el bloque inferior
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
            state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            return currentPos.below();
        }
        return currentPos;
    }

    @Override
    public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
        // Vincular el bloque superior con el inferior
        return List.of(anchorPos.above());
    }
}
```

### 4. DeformationContributor (Deformación)

Controla cómo se deforman los vértices del modelo del bloque.

```java
public interface DeformationContributor extends SwayBehavior {
    // Peso de deformación para un vértice según su altura Y
    default float getVertexWeight(float vertexY, BlockState state, BlockPos pos) {
        return vertexY > 0.05F ? vertexY * vertexY : 0.0F;
    }

    // Escala de deformación
    default float getDeformationScale(BlockState state, BlockPos pos) {
        return 0.45F;
    }

    // Transforma los vértices del quad
    default void transformQuad(VertexMutator v, SwayData data, BlockState state, BlockPos pos) {
        float scale = getDeformationScale(state, pos);
        float dx = data.nx * data.intensity * scale;
        float dz = data.nz * data.intensity * scale;
        if (dx == 0 && dz == 0) return;

        for (int i = 0; i < v.vertexCount(); i++) {
            float y = v.y(i);
            float weight = getVertexWeight(y, state, pos);
            if (weight > 0) {
                v.addOffset(i, dx * weight, 0, dz * weight);
            }
        }
    }
}
```

> **Importante:** `pos` puede ser `null` en ciertas rutas de renderizado (por ejemplo, cuando un
> renderizador consulta los quads sin contexto de bloque, como ocurre con Sodium en NeoForge 1.21.x
> antes de la corrección, donde la sobrecarga de `getQuads` no recibe `BlockPos`). Si tu comportamiento
> necesita la posición (p. ej. para recorrer un multibloque), comprueba `pos == null` y devuelve un
> peso de respaldo (cuadrático) en ese caso. Nunca lances excepciones desde `getVertexWeight`.

**Ejemplo (deformación cuadrática personalizada):**

```java
public class MyDeformationBehavior implements DeformationContributor {
    @Override
    public float getVertexWeight(float vertexY, BlockState state, BlockPos pos) {
        // Deformación cúbica: la punta se dobla más
        return vertexY > 0.05F ? vertexY * vertexY * vertexY : 0.0F;
    }

    @Override
    public float getDeformationScale(BlockState state, BlockPos pos) {
        // Deformación más pronunciada
        return 0.7F;
    }
}
```

---

## 🔗 Pipeline de Comportamientos

El **BehaviorPipeline** agrupa todos los comportamientos registrados para un bloque, clasificándolos por tipo de contribuidor.

### Obtener el Pipeline de un Bloque

```java
BehaviorPipeline pipeline = SwayAPI.getBehaviorPipeline(MyBlocks.CUSTOM_PLANT);

// Acceder a los contribuidores por tipo
List<SwayBehavior> behaviors = pipeline.getBehaviors();
List<CollisionContributor> collisions = pipeline.getCollisionContributors();
List<ForceContributor> forces = pipeline.getForceContributors();
List<MultiBlockContributor> multiblocks = pipeline.getMultiBlockContributors();
List<DeformationContributor> deformations = pipeline.getDeformationContributors();

// Verificar si el pipeline está vacío
boolean isEmpty = pipeline.isEmpty();
```

### Configurar el Pipeline de un Bloque

Puedes reemplazar completamente el pipeline de un bloque:

```java
// Definir el pipeline completo de un bloque
SwayAPI.setBlockPipeline(MyBlocks.CUSTOM_PLANT, List.of(
    BuiltinBehaviors.ENTITY_COLLISION_KEY,
    BuiltinBehaviors.PROXIMITY_FORCE_KEY,
    BuiltinBehaviors.DOUBLE_PLANT_MULTIBLOCK_KEY,
    BuiltinBehaviors.STANDARD_DEFORMATION_KEY,
    BuiltinBehaviors.multiplierKey(1.5f)
));
```

### Añadir/Eliminar Comportamientos de un Bloque

```java
// Añadir un comportamiento a un bloque con prioridad
SwayAPI.addBehaviorToBlock(MyBlocks.CUSTOM_PLANT, myBehaviorKey, 500);

// Eliminar un comportamiento de un bloque
SwayAPI.removeBehaviorFromBlock(MyBlocks.CUSTOM_PLANT, myBehaviorKey);
```

### Comportamientos Globales

Puedes registrar comportamientos que se apliquen a múltiples bloques mediante un predicado:

```java
// Aplicar un comportamiento a todos los bloques que sean plantas
SwayAPI.registerGlobalBehavior(
    myBehaviorKey,
    300,
    block -> block instanceof net.minecraft.world.level.block.FlowerBlock
);
```

---

## ⭐ Comportamientos Integrados

SWAY incluye comportamientos integrados que puedes reutilizar. Se acceden a través de `BuiltinBehaviors`:

```java
import com.github.razorplay01.sway.client.behavior.BuiltinBehaviors;
```

| Key | ID | Tipo | Descripción |
|-----|-----|------|-------------|
| `ENTITY_COLLISION_KEY` | `sway:entity_collision` | Collision | Define el área de búsqueda basada en el radio de influencia de la configuración. |
| `PROXIMITY_FORCE_KEY` | `sway:proximity_force` | Force | Calcula la fuerza basada en la proximidad de la entidad al bloque. |
| `DOUBLE_PLANT_MULTIBLOCK_KEY` | `sway:double_plant_multiblock` | MultiBlock | Gestiona plantas de dos bloques (mitad superior/inferior). |
| `STANDARD_DEFORMATION_KEY` | `sway:standard_quadratic_deformation` | Deformation | Deformación cuadrática estándar con soporte para plantas dobles. |
| `SUGAR_CANE_MULTIBLOCK_KEY` | `sway:sugar_cane_multiblock` | MultiBlock | Gestiona tallos de caña de azúcar de altura variable (2+ bloques). |
| `SUGAR_CANE_DEFORMATION_KEY` | `sway:sugar_cane_deformation` | Deformation | Deformación cuadrática continua a lo largo de todo el tallo de caña. |
| `VINE_MULTIBLOCK_KEY` | `sway:vine_multiblock` | MultiBlock | Gestiona enredaderas colgantes (VINE, WEEPING_VINES, TWISTING_VINES). |
| `VINE_DEFORMATION_KEY` | `sway:vine_deformation` | Deformation | Deformación tipo cuerda: anclada al techo, oscila libremente en la punta. |
| `VINE_CLIMB_TENSION_KEY` | `sway:vine_climb_tension` | Force | Tensa la parte superior de la vid cuando el jugador la trepa. |
| `multiplierKey(float)` | `sway:multiplier_X_X` | Force | Multiplica la intensidad de la fuerza por un factor. |

### Uso de Comportamientos Integrados

```java
// Registrar un bloque con el pipeline estándar completo
SwayAPI.register(MyBlocks.CUSTOM_PLANT, 1.0f);

// O configurar manualmente con los comportamientos integrados
SwayAPI.setBlockPipeline(MyBlocks.CUSTOM_PLANT, List.of(
    BuiltinBehaviors.ENTITY_COLLISION_KEY,
    BuiltinBehaviors.PROXIMITY_FORCE_KEY,
    BuiltinBehaviors.STANDARD_DEFORMATION_KEY,
    BuiltinBehaviors.multiplierKey(1.2f)
));
```

---

## 📦 Contexto y Acumulador de Fuerzas

### SwayBehaviorContext

El contexto proporciona información sobre el entorno durante el procesamiento:

```java
public record SwayBehaviorContext(
    ClientLevel level,        // El nivel del cliente
    Entity triggeringEntity,  // La entidad que causa la interacción
    SwayConfig config,        // La configuración actual del mod
    float partialTick         // El tick parcial para interpolación
) {}
```

### ForceAccumulator

El acumulador de fuerzas combina múltiples contribuciones de fuerza:

```java
ForceAccumulator acc = new ForceAccumulator();

// Estrategias de combinación
enum CombineStrategy {
    ADD,        // Promedio ponderado por intensidad
    VECTOR_SUM, // Suma vectorial
    MAX,        // Mantiene la mayor intensidad
    REPLACE     // Reemplaza completamente
}

// Contribuir una fuerza
acc.contribute(dirX, dirZ, intensity, ForceAccumulator.CombineStrategy.ADD);

// Multiplicar la escala (usado por los multiplicadores)
acc.multiplyScale(1.5f);

// Verificar si hay contribuciones
boolean hasForce = acc.hasAnyContribution();

// Obtener resultados
float nx = acc.getNx();          // Dirección X normalizada
float nz = acc.getNz();          // Dirección Z normalizada
float intensity = acc.getIntensity(); // Intensidad total (con escala)

// Convertir a SwayData
SwayData data = acc.toSwayData();

// Reiniciar para reutilizar
acc.reset();
```

### SwayData

Representa el estado de deformación de un bloque:

```java
public class SwayData {
    public float nx;         // Dirección X normalizada
    public float nz;         // Dirección Z normalizada
    public float intensity;  // Intensidad de la deformación

    // Obtener datos interpolados para suavizado
    SwayData interpolated = data.getInterpolated(smoothness);
}
```

---

## 🎨 Deformación de Vértices

### VertexMutator

La interfaz `VertexMutator` permite modificar las posiciones de los vértices de un quad:

```java
public interface VertexMutator {
    int vertexCount();              // Número de vértices
    float x(int idx);               // Posición X del vértice
    float y(int idx);               // Posición Y del vértice
    float z(int idx);               // Posición Z del vértice
    void setX(int idx, float value); // Establecer X
    void setY(int idx, float value); // Establecer Y
    void setZ(int idx, float value); // Establecer Z
    void pos(int idx, float x, float y, float z); // Establecer posición completa
    void addOffset(int idx, float dx, float dy, float dz); // Añadir desplazamiento
}
```

### SwayBehaviorDeformer

La clase `SwayBehaviorDeformer` aplica la deformación a un mutator:

```java
SwayBehaviorDeformer.deform(mutator, interpolatedData, state, pos, pipeline);
```

---

## ⚙️ Configuración del Mod

La configuración se guarda en `config/sway.json` y se puede modificar en tiempo de ejecución:

```json
{
  "enabled": true,
  "intensity": 1.0,
  "maxDistance": 8.0,
  "influenceRadius": 1.2
}
```

| Campo | Tipo | Valor por Defecto | Rango | Descripción |
|-------|------|-------------------|-------|-------------|
| `enabled` | boolean | `true` | - | Activa/desactiva el mod. |
| `intensity` | float | `1.0` | `0.0 - 5.0` | Intensidad base de la deformación. |
| `maxDistance` | float | `8.0` | `2.0 - 32.0` | Distancia máxima de búsqueda de entidades. |
| `influenceRadius` | float | `1.2` | `0.1 - 3.0` | Radio de influencia alrededor de las entidades. |

### Acceso a la Configuración

```java
import com.github.razorplay01.sway.config.SwayConfig;

// Acceder a la configuración actual
SwayConfig config = SwayConfig.INSTANCE;
boolean enabled = config.enabled;
float intensity = config.intensity;
float maxDistance = config.maxDistance;
float influenceRadius = config.influenceRadius;

// Guardar cambios
SwayConfig.save();
```

---

## 💡 Ejemplos Completos

### Ejemplo 1: Registro Simple de un Bloque

```java
public class MyMod {
    public static void onInitialize() {
        // Registro simple con multiplicador
        SwayAPI.register(MyBlocks.CUSTOM_FLOWER, 1.0f);
        SwayAPI.register(MyBlocks.CUSTOM_TALL_GRASS, 1.5f);
    }
}
```

### Ejemplo 2: Comportamiento Personalizado Completo

```java
public class MyCustomBehavior implements ForceContributor, DeformationContributor {
    private final float strength;

    public MyCustomBehavior(float strength) {
        this.strength = strength;
    }

    @Override
    public void contributeForce(BlockPos pos, BlockState state, Entity entity,
                                 SwayBehaviorContext ctx, ForceAccumulator accumulator) {
        Vec3 entityPos = entity.position();
        double dx = (pos.getX() + 0.5) - entityPos.x;
        double dz = (pos.getZ() + 0.5) - entityPos.z;
        double distSq = dx * dx + dz * dz;
        float radius = ctx.config().influenceRadius;

        if (distSq < radius * radius) {
            double d = Math.sqrt(distSq);
            float force = (float) (1.0 - d / radius) * strength;
            if (force > 0.01F) {
                float nx = d > 0.001 ? (float) (dx / d) : 1.0F;
                float nz = d > 0.001 ? (float) (dz / d) : 0.0F;
                accumulator.contribute(nx, nz, force, ForceAccumulator.CombineStrategy.ADD);
            }
        }
    }

    @Override
    public float getDeformationScale(BlockState state, BlockPos pos) {
        return 0.6F;
    }

    @Override
    public float getVertexWeight(float vertexY, BlockState state, BlockPos pos) {
        return vertexY > 0.05F ? vertexY * vertexY : 0.0F;
    }
}
```

### Ejemplo 3: Registro con Pipeline Personalizado

```java
public class MyMod {
    public static void onInitialize() {
        // Crear una key para el comportamiento personalizado
        BehaviorKey myBehaviorKey = BehaviorKey.create("mymod", "custom_sway");

        // Registrar el comportamiento
        SwayAPI.registerBehavior(myBehaviorKey, new MyCustomBehavior(1.5f));

        // Configurar el pipeline del bloque
        SwayAPI.setBlockPipeline(MyBlocks.CUSTOM_PLANT, List.of(
            BuiltinBehaviors.ENTITY_COLLISION_KEY,
            BuiltinBehaviors.PROXIMITY_FORCE_KEY,
            myBehaviorKey,  // Comportamiento personalizado
            BuiltinBehaviors.STANDARD_DEFORMATION_KEY
        ));
    }
}
```

### Ejemplo 4: Comportamiento Global para Múltiples Bloques

```java
public class MyMod {
    public static void onInitialize() {
        BehaviorKey myBehaviorKey = BehaviorKey.create("mymod", "global_sway");
        SwayAPI.registerBehavior(myBehaviorKey, new MyCustomBehavior(1.0f));

        // Aplicar a todos los bloques de tipo FlowerBlock
        SwayAPI.registerGlobalBehavior(
            myBehaviorKey,
            200,
            block -> block instanceof FlowerBlock
        );
    }
}
```

### Ejemplo 5: Planta de Dos Bloques Personalizada

```java
public class MyDoublePlantBehavior implements MultiBlockContributor {
    @Override
    public BlockPos getAnchorPosition(BlockPos currentPos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
            state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            return currentPos.below();
        }
        return currentPos;
    }

    @Override
    public Collection<BlockPos> getLinkedBlocks(BlockPos anchorPos, BlockState state, ClientLevel level) {
        if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) return List.of();
        return List.of(anchorPos.above());
    }
}

// Registro
public class MyMod {
    public static void onInitialize() {
        BehaviorKey doublePlantKey = BehaviorKey.create("mymod", "double_plant");
        SwayAPI.registerBehavior(doublePlantKey, new MyDoublePlantBehavior());

        SwayAPI.setBlockPipeline(MyBlocks.CUSTOM_DOUBLE_PLANT, List.of(
            BuiltinBehaviors.ENTITY_COLLISION_KEY,
            BuiltinBehaviors.PROXIMITY_FORCE_KEY,
            doublePlantKey,
            BuiltinBehaviors.STANDARD_DEFORMATION_KEY,
            BuiltinBehaviors.multiplierKey(1.0f)
        ));
    }
}
```

---

## 📖 Referencia Rápida de Métodos

### SwayAPI

| Método | Descripción |
|--------|-------------|
| `register(Block, float)` | Registra un bloque con multiplicador (configura pipeline automático). |
| `isInteractive(Block)` | Verifica si un bloque tiene pipeline registrado. |
| `getMultiplier(BlockState)` | Obtiene el multiplicador de un bloque. |
| `getRegistry()` | Obtiene el mapa de registros heredados. |
| `registerBehavior(BehaviorKey, SwayBehavior)` | Registra un comportamiento global. |
| `addBehaviorToBlock(Block, BehaviorKey, int)` | Añade un comportamiento a un bloque con prioridad. |
| `removeBehaviorFromBlock(Block, BehaviorKey)` | Elimina un comportamiento de un bloque. |
| `setBlockPipeline(Block, List<BehaviorKey>)` | Reemplaza el pipeline completo de un bloque. |
| `getBehaviorPipeline(Block)` | Obtiene el pipeline de un bloque. |
| `registerGlobalBehavior(BehaviorKey, int, Predicate<Block>)` | Registra un comportamiento global con predicado. |

### BehaviorKey

| Método | Descripción |
|--------|-------------|
| `create(String namespace, String path)` | Crea una key personalizada. |
| `fromVanilla(String path)` | Crea una key en el namespace `sway`. |
| `getId()` | Obtiene el identificador (Identifier/ResourceLocation). |
| `getNamespace()` | Obtiene el namespace. |
| `getPath()` | Obtiene el path. |

### ForceAccumulator

| Método | Descripción |
|--------|-------------|
| `contribute(float dirX, float dirZ, float intensity, CombineStrategy)` | Contribuye una fuerza. |
| `multiplyScale(float factor)` | Multiplica la escala de intensidad. |
| `hasAnyContribution()` | Verifica si hay contribuciones. |
| `getNx()` | Obtiene la dirección X normalizada. |
| `getNz()` | Obtiene la dirección Z normalizada. |
| `getIntensity()` | Obtiene la intensidad total. |
| `toSwayData()` | Convierte a SwayData. |
| `updateSwayData(SwayData)` | Actualiza un SwayData existente. |
| `reset()` | Reinicia el acumulador. |

### SwayEngine (Interno)

| Método | Descripción |
|--------|-------------|
| `get(BlockPos)` | Obtiene el SwayData actual de un bloque. |
| `getSmoothness()` | Obtiene el valor de suavidad. |

---

## 📝 Notas Importantes

1. **Solo Cliente**: SWAY es un mod **solo cliente**. La API solo debe usarse en el lado del cliente.

2. **Registro Temprano**: Se recomienda registrar los comportamientos y bloques durante la inicialización del mod (`onInitialize`).

3. **Thread Safety**: Los registros internos usan estructuras thread-safe (`ConcurrentHashMap`, `synchronizedMap`).

4. **Rendimiento**: El motor está optimizado para minimizar el impacto en FPS. Evita operaciones costosas en los métodos de contribución de fuerza.

5. **Compatibilidad**: SWAY soporta múltiples versiones de Minecraft (1.20.1, 1.21.1, 1.21.11, 26+) y múltiples loaders (Fabric, Forge, NeoForge).

---

## 🔗 Enlaces Útiles

- **Repositorio**: [https://github.com/RazorPlay01/SWAY](https://github.com/RazorPlay01/SWAY)
- **Código fuente de la API**: `src/main/java/com/github/razorplay01/sway/api/`
- **Comportamientos integrados**: `src/main/java/com/github/razorplay01/sway/client/behavior/`
- **Motor de deformación**: `src/main/java/com/github/razorplay01/sway/client/SwayEngine.java`
