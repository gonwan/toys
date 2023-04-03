### Traps in Spring Data JPA
1. Insert: JPA with `@GeneratedValue(strategy = GenerationType.IDENTITY)` does not support batch insertion.
2. Update: run select before update operations, since we are using entities.
3. Update: updates all values in a row if using JPA repository, `@DynamicUpdate` can help but it also has performance issues.
4. Select: N+1 problems, if not annotate entities carefully.
5. JPA auditing(`@CreatedDate`, `@LastModifiedDate`) only supports `java.util.Date`, but not `java.sql.Date`.
6. `@Version` for optimistic locks, which is not supported by other frameworks.

### QueryDSL
1. Working with JPA.
2. Status: not actively maintained?
3. Supports `insert into... on duplicate key update...`, requires separate DTO(non-JPA) generation.
4. Join statements support via tuple return type.

### MyBatis Dynamic SQL
1. Not working with JPA.
2. Status: actively maintained, but not so mature?
3. Does not support `insert into... on duplicate key update...` and other database dependent features.
4. Join statements require XML mappers.
