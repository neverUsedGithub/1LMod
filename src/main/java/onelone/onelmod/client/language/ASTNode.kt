package onelone.onelmod.client.language

sealed class ASTNode(val span: Token.Span) {
    class Number(val value: String, span: Token.Span) : ASTNode(span)
    class Program(val value: ASTNode, span: Token.Span) : ASTNode(span)
    class Identifier(val value: String, span: Token.Span) : ASTNode(span)
    class NegateExpression(val value: ASTNode, span: Token.Span) : ASTNode(span)
    class BinaryExpression(val left: ASTNode, val right: ASTNode, val operator: String, span: Token.Span) : ASTNode(span)
}