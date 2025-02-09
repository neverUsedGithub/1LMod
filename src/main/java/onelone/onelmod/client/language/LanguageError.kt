package onelone.onelmod.client.language

class LanguageError(message: String, val span: Token.Span): Throwable(message) {}
