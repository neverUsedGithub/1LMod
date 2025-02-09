package onelone.onelmod.client.language

class Parser(private val lexer: Lexer) {
    private var current = lexer.next()

    private val precedence: Map<String, Int> = mapOf(
        "+" to 0,
        "-" to 0,
        "*" to 1,
        "/" to 1,
        "%" to 1,
        "." to 2
    )

    private fun matches(type: Token.Type, value: String? = null): Boolean {
        if (current.type != type) return false
        if (value != null && current.value != value) return false

        return true
    }

    private fun eat(type: Token.Type, value: String? = null): Token {
        if (!matches(type, value))
            throw LanguageError("unexpected token ${current.type}, expected $type", current.span)

        val last = current
        current = lexer.next()

        return last
    }

    private fun parsePrimary(): ASTNode {
        if (this.matches(Token.Type.OPERATOR, "-")) {
            val start = this.eat(Token.Type.OPERATOR, "-").span.start
            val value = this.parsePrimary()

            return ASTNode.NegateExpression(value, Token.Span(start, value.span.end))
        }

        if (this.matches(Token.Type.NUMBER)) {
            val num = this.eat(Token.Type.NUMBER)
            return ASTNode.Number(num.value, num.span)
        }

        if (this.matches(Token.Type.IDENTIFIER)) {
            val identifier = this.eat(Token.Type.IDENTIFIER)
            return ASTNode.Identifier(identifier.value, identifier.span)
        }

        if (this.matches(Token.Type.PAREN, "(")) {
            eat(Token.Type.PAREN, "(")
            val expr = parseBinary()
            eat(Token.Type.PAREN, ")")

            return expr
        }

        throw LanguageError("expected a(n) number, identifier or parenthesis", current.span)
    }

    private fun isOperator(minPrecedence: Int): Boolean {
        return matches(Token.Type.OPERATOR) && precedence.getOrDefault(current.value, 0) >= minPrecedence
    }

    private fun parseBinaryRecursive(left: ASTNode, minPrecedence: Int): ASTNode {
        var lhs = left

        while (isOperator(minPrecedence)) {
            val currPrecedence = precedence.getOrDefault(current.value, 0)
            val operator = this.eat(Token.Type.OPERATOR)
            var rhs = parsePrimary()

            while (isOperator(currPrecedence + 1)) {
                rhs = parseBinaryRecursive(rhs, currPrecedence + 1)
            }

            lhs = ASTNode.BinaryExpression(lhs, rhs, operator.value, Token.Span(lhs.span.start, rhs.span.end))
        }

        return lhs
    }

    private fun parseBinary(): ASTNode {
        return parseBinaryRecursive(parsePrimary(), 0)
    }

    fun parse(): ASTNode {
        val value = parseBinary()
        eat(Token.Type.EOF)

        return ASTNode.Program(value, value.span)
    }
}