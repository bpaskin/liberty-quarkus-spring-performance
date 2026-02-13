# Hibernate to EclipseLink Migration Guide

## Migration Overview

This document details the migration from Hibernate JPA implementation to the native EclipseLink JPA provider included with WebSphere Liberty.

**Migration Date:** February 13, 2026  
**Liberty Version:** Jakarta EE 10.0 (JPA 3.1)  
**JPA Provider:** EclipseLink (Liberty's default)

---

## Changes Made

### 1. Dependencies (pom.xml)

**Status:** ✅ No changes required

The project was already using only standard Jakarta EE APIs without explicit Hibernate dependencies. The `jakarta.jakartaee-api` dependency provides all necessary JPA interfaces.

```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>
```

### 2. Persistence Configuration (persistence.xml)

**Status:** ✅ Updated

**Removed Hibernate-specific properties:**
- `hibernate.dialect` → Replaced with `eclipselink.target-database`
- `hibernate.show_sql` → Replaced with `eclipselink.logging.level.sql`
- `hibernate.format_sql` → EclipseLink formats SQL by default when logging is enabled
- `hibernate.connection.pool_size` → Connection pooling now managed by Liberty DataSource
- `hibernate.generate_statistics` → Removed (use Liberty monitoring instead)

**New EclipseLink properties:**
```xml
<property name="eclipselink.logging.level.sql" value="FINE"/>
<property name="eclipselink.logging.parameters" value="true"/>
<property name="eclipselink.target-database" value="PostgreSQL"/>
<property name="eclipselink.weaving" value="true"/>
```

**Retained standard JPA properties:**
- `jakarta.persistence.schema-generation.database.action`
- `jakarta.persistence.schema-generation.create-source`
- `jakarta.persistence.schema-generation.drop-source`
- `jakarta.persistence.sql-load-script-source`

### 3. Entity Classes

**Status:** ✅ Updated

#### Removed Hibernate-specific Annotations:

**a) @NaturalId (Fruit.java, Store.java)**
- **Removed from:** `name` field in both entities
- **Impact:** Natural ID optimization no longer available
- **Alternative:** Use standard `@Column(unique=true)` for uniqueness constraint
- **Note:** Queries by natural ID now use standard JPQL instead of Hibernate's `byNaturalId()` API

**b) @Cache and @CacheConcurrencyStrategy (StoreFruitPrice.java)**
- **Removed:** `@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)`
- **Replaced with:** `@Cacheable` (standard JPA 2.0 annotation)
- **Impact:** EclipseLink's shared cache will be used with default settings
- **Configuration:** Cache behavior can be tuned in persistence.xml if needed

**c) @Fetch and @FetchMode (StoreFruitPrice.java)**
- **Removed:** `@Fetch(FetchMode.SELECT)` on Store relationship
- **Impact:** EclipseLink will use its default fetch strategy
- **Alternative:** Use standard JPA `@EntityGraph` or JPQL fetch joins for optimization

#### Updated Entity Files:
1. **Fruit.java** - Removed `@NaturalId` import and annotation
2. **Store.java** - Removed `@NaturalId` import and annotation  
3. **StoreFruitPrice.java** - Removed all Hibernate annotations, added `@Cacheable`

### 4. Repository/DAO Classes

**Status:** ✅ No changes required

The `FruitRepository` class already uses standard JPA APIs:
- `EntityManager` (not Hibernate's `Session`)
- Standard JPQL queries
- JPA persistence methods (`persist()`, `merge()`, `remove()`)

No Hibernate-specific APIs were found in the codebase.

### 5. Server Configuration (server.xml)

**Status:** ✅ Updated

**Added clarifying comments:**
```xml
<!-- jakartaee-10.0 includes JPA 3.1 with EclipseLink as the default JPA provider -->
```

**Updated logging configuration:**
```xml
<logging traceSpecification="*=info:org.acme.*=all:eclipselink.sql=fine" 
         maxFileSize="20" 
         maxFiles="10" 
         consoleLogLevel="INFO"/>
```

**DataSource configuration:** Already properly configured with connection pooling:
```xml
<connectionManager maxPoolSize="20" minPoolSize="5"/>
```

**PostgreSQL Driver Library Path:** Updated to match Liberty Maven Plugin configuration:
```xml
<library id="postgresql-library">
    <fileset dir="${server.config.dir}/lib/postgresql" includes="*.jar"/>
</library>
```
This matches the `copyDependencies` configuration in pom.xml which copies the PostgreSQL driver to `lib/postgresql`.

### 6. Import Statements

**Status:** ✅ Updated

All entity classes now use only standard Jakarta Persistence imports:
- `jakarta.persistence.*` - Standard JPA annotations
- `jakarta.validation.constraints.*` - Bean Validation

Removed imports:
- `org.hibernate.annotations.*` - All Hibernate-specific annotations

---

## Behavioral Differences

### 1. Natural ID Queries

**Hibernate:**
```java
session.byNaturalId(Fruit.class)
    .using("name", "Apple")
    .load();
```

**EclipseLink (Standard JPA):**
```java
entityManager.createQuery("SELECT f FROM Fruit f WHERE f.name = :name", Fruit.class)
    .setParameter("name", "Apple")
    .getSingleResult();
```

**Impact:** Slightly less optimized, but functionally equivalent. The unique index on the name column ensures good performance.

### 2. Caching Strategy

**Hibernate:** Fine-grained cache concurrency strategies (READ_WRITE, NONSTRICT_READ_WRITE, etc.)

**EclipseLink:** Uses shared cache with configurable isolation levels. The `@Cacheable` annotation enables caching with default settings.

**Configuration options in persistence.xml:**
```xml
<property name="eclipselink.cache.shared.default" value="true"/>
<property name="eclipselink.cache.type.default" value="SoftWeak"/>
<property name="eclipselink.cache.size.default" value="500"/>
```

### 3. Fetch Strategies

**Hibernate:** `@Fetch(FetchMode.SELECT)`, `@Fetch(FetchMode.JOIN)`, `@Fetch(FetchMode.SUBSELECT)`

**EclipseLink:** Uses standard JPA fetch types (EAGER/LAZY) with batch fetching and join fetching configured via:
- Entity Graphs (JPA 2.1+)
- JPQL fetch joins
- Query hints

**Example using Entity Graph:**
```java
EntityGraph<StoreFruitPrice> graph = entityManager.createEntityGraph(StoreFruitPrice.class);
graph.addAttributeNodes("store", "fruit");
entityManager.find(StoreFruitPrice.class, id, 
    Collections.singletonMap("jakarta.persistence.fetchgraph", graph));
```

### 4. SQL Logging

**Hibernate:**
```properties
hibernate.show_sql=true
hibernate.format_sql=true
```

**EclipseLink:**
```properties
eclipselink.logging.level.sql=FINE
eclipselink.logging.parameters=true
```

Both approaches log SQL statements. EclipseLink's logging is more integrated with Liberty's logging framework.

### 5. Schema Generation

**No changes required.** Both Hibernate and EclipseLink support the standard JPA schema generation properties:
- `jakarta.persistence.schema-generation.database.action`
- `jakarta.persistence.schema-generation.create-source`
- `jakarta.persistence.sql-load-script-source`

---

## Testing Checklist

### ✅ Completed Tests:

1. **Compilation:** Successfully compiled with `mvn clean compile`
2. **Packaging:** Successfully packaged WAR file with `mvn package`
3. **Entity Validation:** All entities use only standard JPA annotations

### 🔄 Recommended Runtime Tests:

1. **CRUD Operations:**
   - [ ] Create new entities (Fruit, Store, StoreFruitPrice)
   - [ ] Read entities by ID
   - [ ] Update existing entities
   - [ ] Delete entities

2. **Query Operations:**
   - [ ] JPQL queries (findAll, findByName)
   - [ ] Named queries (if any)
   - [ ] Criteria API queries (if any)

3. **Relationship Operations:**
   - [ ] Lazy loading (@ManyToOne with LAZY)
   - [ ] Eager loading (@ManyToOne with EAGER)
   - [ ] Cascade operations
   - [ ] Orphan removal

4. **Transaction Management:**
   - [ ] JTA transactions
   - [ ] Rollback scenarios
   - [ ] Transaction isolation

5. **Caching:**
   - [ ] Second-level cache behavior
   - [ ] Cache invalidation
   - [ ] Query result caching

6. **Performance:**
   - [ ] N+1 query detection
   - [ ] Batch operations
   - [ ] Connection pool utilization

---

## Performance Considerations

### EclipseLink Optimizations:

1. **Weaving:** Enabled in persistence.xml for lazy loading optimization
   ```xml
   <property name="eclipselink.weaving" value="true"/>
   ```

2. **Batch Fetching:** Can be configured per query or globally
   ```xml
   <property name="eclipselink.jdbc.batch-writing" value="JDBC"/>
   <property name="eclipselink.jdbc.batch-writing.size" value="100"/>
   ```

3. **Read-Only Queries:** Use query hints for read-only operations
   ```java
   query.setHint("eclipselink.read-only", "true");
   ```

4. **Connection Pooling:** Managed by Liberty DataSource (already configured)

---

## Rollback Plan

If issues arise, to rollback to Hibernate:

1. **Add Hibernate dependencies to pom.xml:**
   ```xml
   <dependency>
       <groupId>org.hibernate.orm</groupId>
       <artifactId>hibernate-core</artifactId>
       <version>6.4.x</version>
   </dependency>
   ```

2. **Restore Hibernate properties in persistence.xml**
3. **Restore Hibernate annotations in entity classes**
4. **Update server.xml to use Hibernate as JPA provider** (if needed)

---

## Additional Resources

- [EclipseLink Documentation](https://www.eclipse.org/eclipselink/documentation/)
- [Liberty JPA Feature Documentation](https://openliberty.io/docs/latest/reference/feature/jpa-3.1.html)
- [Jakarta Persistence 3.1 Specification](https://jakarta.ee/specifications/persistence/3.1/)
- [EclipseLink Performance Tuning](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Configuration/Performance)

---

## Summary

The migration from Hibernate to EclipseLink was straightforward because:

1. **No Hibernate dependencies** were present in pom.xml
2. **Repository layer** already used standard JPA APIs
3. **Entity classes** required minimal changes (removing Hibernate-specific annotations)
4. **Liberty configuration** already included JPA 3.1 with EclipseLink

The application now uses the native JPA provider included with WebSphere Liberty, eliminating external dependencies and ensuring better integration with the Liberty runtime.

**Build Status:** ✅ SUCCESS  
**Compilation:** ✅ PASSED  
**Packaging:** ✅ PASSED  
**Runtime Testing:** 🔄 RECOMMENDED (see Testing Checklist above)