# MongoHelper

MongoHelper is a small ORM-like helper library built on top of the official MongoDB Java driver. It provides:

- **Central registration of models** via `OrmSchematic`
- **Metadata-driven collections and indexes**
- **Grouped CRUD operations per model** via `OperationsGroup`

The goal is to keep your MongoDB access code simple, consistent, and metadata-driven, while still letting you drop down to the raw MongoDB driver whenever you need.

---

## Features

- Define your schema in one place with `OrmSchematic`
- Annotate models and fields using `@OrmModel`, `@OrmField`, and index annotations
- Automatic creation of collections and indexes based on model metadata
- Strongly-typed grouped operations for each model (`count`, `create`, `delete`, `find`, `update`, `upsert`)
- Pluggable codec registries for custom value types and enums

---

## Requirements

- Java 17+ (adjust if your project targets another version)
- MongoDB server (local or remote)
- MongoDB Java driver (sync)

---

## Installation

Add MongoHelper and the MongoDB Java driver to your build.

### Maven

```xml
<dependencies>
    <!-- MongoDB Java driver -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>VERSION_HERE</version>
    </dependency>

    <!-- MongoHelper -->
    <dependency>
        <groupId>net.clydo</groupId>
        <artifactId>mongohelper</artifactId>
        <version>VERSION_HERE</version>
    </dependency>
</dependencies>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("org.mongodb:mongodb-driver-sync:VERSION_HERE")
    implementation("net.clydo:mongohelper:VERSION_HERE")
}
```

Replace `VERSION_HERE` with the actual versions you use or publish.

---

## Quick Start

### 1. Create a MongoClient

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

MongoClient client = MongoClients.create("mongodb://localhost:27017");
```

### 2. Create a MongoHelper Instance

```java
import net.clydo.mongo.MongoHelper;

MongoHelper helper = MongoHelper.create(client);
```

### 3. Define a Schematic

```java
import net.clydo.mongo.OrmSchematic;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.List;

public class UserSchematic implements OrmSchematic {

    @Override
    public String getDatabaseName() {
        return "app_db";
    }

    @Override
    public List<Class<?>> getModelClasses() {
        return List.of(User.class);
    }

    @Override
    public List<Class<?>> getTypeClasses() {
        return List.of();
    }

    @Override
    public List<Class<? extends Enum<?>>> getEnumClasses() {
        return List.of();
    }

    @Override
    public List<CodecRegistry> handleCodecRegistry(List<CodecRegistry> registries) {
        return registries;
    }
}
```

### 4. Annotate a Model

```java
import net.clydo.mongo.annotations.OrmModel;
import net.clydo.mongo.annotations.OrmField;
import org.bson.types.ObjectId;

@OrmModel("users")
public class User {

    @OrmField("_id")
    private ObjectId id;

    @OrmField("email")
    private String email;

    @OrmField("age")
    private int age;

    public User() {
    }

    // getters/setters ...
}
```

### 5. Register the Schematic and Use OperationsGroup

```java
import net.clydo.mongo.operations.OperationsGroup;

helper.register(UserSchematic.class);

OperationsGroup<User> users = helper.get(User.class);

// Example usage (pattern only; adjust to your operations API):

User newUser = new User();
newUser.setEmail("john@example.com");
newUser.setAge(30);

users.create()
        // .one(newUser)
        ;

users.find()
        // .where(eq("email", "john@example.com"))
        ;
```

The exact methods on `create()`, `find()`, etc. depend on the concrete operation implementations in this project; see the wiki for details.

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**, as stated in the source file headers. See the license text at:

- <http://www.gnu.org/licenses/>
