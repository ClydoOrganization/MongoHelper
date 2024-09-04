# Temporary README

**Important:** This code is temporary and may contain errors or unfinished elements. Please review, revise, and replace it as soon as possible.

### `UserModel` Class

```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.UUID;

/**
 * Represents a user model in the MongoDB collection for testing purposes.
 * Annotated with MongoDB-specific annotations for mapping fields.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@MongoModel("zUserTest")
public class UserModel {
    
    public static final String GAME_ID = "gameId";
    public static final String SCORE = "score";
    public static final String ENUM_TEST = "enumTest";
    public static final String PROFILE = "profile";
    public static final String DOC = "doc";

    @MongoField("_id")
    private ObjectId mongoId;

    @MongoUnique
    @MongoField(GAME_ID)
    private UUID id;

    @MongoField(SCORE)
    private int score;

    @MongoField(ENUM_TEST)
    private Test enumTest;

    @MongoField(PROFILE)
    private ProfileType userProfile;

    @MongoField(DOC)
    private Document doc;
}
```

### `ProfileType` Class

```java
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents the profile type associated with a user.
 * Annotated with MongoDB-specific annotations for mapping fields.
 */
@NoArgsConstructor
@AllArgsConstructor
@MongoType
public class ProfileType {

    @MongoField("profileId")
    private String id;

    @MongoField("username")
    private String username;

    @MongoField("email")
    private String email;
}
```

### `Test` Enum

```java
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different test types.
 * Each enum constant is mapped to a specific value in MongoDB.
 */
@RequiredArgsConstructor
@MongoEnum
public enum Test {
    @MongoMapAs("async_mapped")
    ASYNC,

    @MongoMapAs("software_mapped")
    SOFTWARE,

    @MongoMapAs("game_mapped")
    GAME;

    /**
     * Returns the next enum value in a cyclic order.
     *
     * @return The next test type.
     */
    public Test next() {
        return switch (this) {
            case ASYNC -> SOFTWARE;
            case SOFTWARE -> GAME;
            case GAME -> ASYNC;
        };
    }
}
```

### `TestStart` Class

```java
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates usage of MongoDB operations with the `UserModel`.
 * Performs CRUD operations and upserts on MongoDB using sample data.
 */
public class TestStart {
    public static void main(String[] args) {

        // MongoDB connection string
        val connectionString = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.2.14&replicaSet=rs0";

        try (val mongoHelper = MongoHelpers.create(connectionString)) {
            new Document();  // Initialization for schema creation

            // Create schema for UserModel
            mongoHelper.newSchema("test", UserModel.class);

            // Get the model for UserModel
            val model = mongoHelper.getModel(UserModel.class);

            // Example: Create and upsert a single UserModel
            val userModel = model.find().uniqueByUnique("a6eddfd2-82d9-4901-8d4e-62cc1a73aa34");

            // Modify the userModel
            userModel.setScore(ThreadLocalRandom.current().nextInt(600));
            userModel.setEnumTest(userModel.getEnumTest().next());
            userModel.setDoc(new Document("test", 114));

            // Delete the userModel and upsert it
            model.delete().one(userModel);
            model.upsert().raw(userModel);
        }
    }
}
```

### Summary of the Documentation:

1. **`UserModel` Class**:
    - Provides a representation of user data with MongoDB-specific annotations for mapping.
    - Fields include `mongoId`, `id`, `score`, `enumTest`, `userProfile`, and `doc`.

2. **`ProfileType` Class**:
    - Represents the profile information of a user.
    - Contains fields for `id`, `username`, and `email`.

3. **`Test` Enum**:
    - Enum representing different types of tests with cyclic progression.
    - Uses MongoDB mapping annotations to specify how values are stored.

4. **`TestStart` Class**:
    - Demonstrates creating, updating, and upserting `UserModel` instances in MongoDB.
    - Includes operations such as schema creation, data insertion, and modification.