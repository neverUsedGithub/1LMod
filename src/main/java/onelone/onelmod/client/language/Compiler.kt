package onelone.onelmod.client.language

import kotlinx.atomicfu.atomicArrayOfNulls

class Compiler(source: String) {
    private val lexer = Lexer(source)
    private val parser = Parser(lexer)

    private val properties: Map<String, String> = mapOf(
        "x"     to "1",
        "y"     to "2",
        "z"     to "3",
        "yaw"   to "4",
        "pitch" to "5",
    )

    companion object {
        fun compileToExpression(source: String): Result<String> {
            return try {
                Result.success(Compiler(source).compile())
            } catch (error: LanguageError) {
                Result.failure(error)
            }
        }
    }

    private fun operatorDot(node: ASTNode.BinaryExpression, parent: ASTNode): String {
        if (node.left !is ASTNode.Identifier)
            throw LanguageError("expected an identifier on the left-hand side of '.' operator", node.left.span)

        var method = "index"
        val property: String

        if (node.right is ASTNode.Identifier && node.right.value in properties) property = properties[node.right.value]!!
        else if (node.right is ASTNode.Identifier) {
            property = node.right.value
            method = "entry"
        } else {
            property = compileNode(node.right, node)
        }

        return "%$method(${node.left.value}, $property)"
    }

    private fun operatorPow(node: ASTNode.BinaryExpression, parent: ASTNode): String {
        if (node.right !is ASTNode.Number)
            throw LanguageError("power must be known at compile-time", node.right.span)

        if ('.' in node.right.value)
            throw LanguageError("power mustn't have any decimal places", node.right.span)

        val single = compileNode(node.left, node)
        if (node.right.value == "0") return "1"
        if (node.right.value == "1") return single

        var expr = ""

        for (i in 1..node.right.value.toInt()) {
            if (i > 1) expr += " * "
            expr += single
        }

        return "%math($expr)"
    }

    private fun compileNode(node: ASTNode, parent: ASTNode): String {
        return when (node) {
            is ASTNode.Program -> compileNode(node.value, node)
            is ASTNode.Number -> node.value
            is ASTNode.Identifier -> "%var(${node.value})"
            is ASTNode.NegateExpression -> "%math(0 - ${compileNode(node.value, node)})"
            is ASTNode.BinaryExpression -> {
                var expr: String

                if (node.operator == ".") expr = operatorDot(node, parent)
                else if (node.operator == "^") expr = operatorPow(node, parent)
                else {
                    expr = "${compileNode(node.left, node)} ${node.operator} ${compileNode(node.right, node)}"
                    if (parent !is ASTNode.BinaryExpression || parent.left != node) expr = "%math($expr)"
                }

                return expr
            }
        }
    }

    fun compile(): String {
        val program = parser.parse()
        return compileNode(program, program)
   }
}