package onelone.onelmod.client.language

class Token(val type: Type, val value: String, val span: Span) {
    class Span(val start: Int, val end: Int) {}

    enum class Type {
        IDENTIFIER,
        OPERATOR,
        NUMBER,
        PAREN,
        EOF,
    }
}