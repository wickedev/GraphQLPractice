package org.example.configuration.graphql

class SyntaxError : Exception("GRAPHQL_PARSE_FAILED")

class ValidationError : Exception("GRAPHQL_VALIDATION_FAILED")

class UserInputError : Exception("BAD_USER_INPUT")

class AuthenticationError : Exception("UNAUTHENTICATED")

class ForbiddenError : Exception("FORBIDDEN")

class PersistedQueryNotFoundError : Exception("PERSISTED_QUERY_NOT_FOUND")

class PersistedQueryNotSupportedError : Exception("PERSISTED_QUERY_NOT_SUPPORTED")

class TokenNotExistError : Exception("AUTHORIZATION_TOKEN_NOT_EXIST")
