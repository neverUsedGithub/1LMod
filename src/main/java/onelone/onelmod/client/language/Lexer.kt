package onelone.onelmod.client.language

class Lexer(private val source: String) {
    private var pos = 0
    private val constTokens: Map<String, Token.Type> = mapOf(
        "(" to Token.Type.PAREN,
        ")" to Token.Type.PAREN,

        "+" to Token.Type.OPERATOR,
        "-" to Token.Type.OPERATOR,
        "*" to Token.Type.OPERATOR,
        "/" to Token.Type.OPERATOR,
        "%" to Token.Type.OPERATOR,
        "." to Token.Type.OPERATOR,
        "^" to Token.Type.OPERATOR,

        "==" to Token.Type.OPERATOR,
        "&&" to Token.Type.OPERATOR,
        "||" to Token.Type.OPERATOR
    )

    private fun isEOF(): Boolean {
        return pos >= source.length
    }

    fun next(): Token {
        while (!isEOF() && source[pos] == ' ') pos++

        if (isEOF())
            return Token(Token.Type.EOF, "", Token.Span(pos, pos))

        if (constTokens.keys.any { token -> token.startsWith(source[pos]) }) {
            val start = pos
            var curr = source[pos++].toString()

            while (!isEOF() && constTokens.keys.any { token -> token.startsWith(curr + source[pos]) })
                curr += source[pos++];

            return Token(constTokens[curr]!!, curr, Token.Span(start, start + curr.length))
        }

        if (source[pos].isJavaIdentifierStart()) {
            val start = pos
            var ident = source[pos++].toString()
            while (!isEOF() && source[pos].isJavaIdentifierPart()) ident += source[pos++]

            return Token(Token.Type.IDENTIFIER, ident, Token.Span(start, pos - 1))
        }

        if (source[pos] in "0123456789") {
            val start = pos
            var num = source[pos++].toString()
            while (!isEOF() && source[pos] in "0123456789.") num += source[pos++]

            return Token(Token.Type.NUMBER, num, Token.Span(start, pos - 1))
        }

        throw LanguageError("unexpected character '${source[pos]}'", Token.Span(pos, pos))
    }
}