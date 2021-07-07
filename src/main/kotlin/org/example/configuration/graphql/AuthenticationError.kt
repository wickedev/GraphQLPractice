package org.example.configuration.graphql

class AuthenticationError : Exception("UNAUTHENTICATED")

class ForbiddenError : Exception("FORBIDDEN")

class DuplicateNameException : Exception("DUPLICATE_NAME")