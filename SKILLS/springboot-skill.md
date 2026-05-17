# Spring Boot Skill

## Standard Layer Structure

```txt
controller -> service -> repository -> database
```

## Rules

- Controller handles request and response
- Service handles business logic
- Repository handles database access
- DTO is used for API input/output
- Entity is used for database mapping
- Exception handler manages errors globally
