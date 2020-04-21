import br.com.devsrsouza.kotlinx.serialization.sql.AutoIncrement
import br.com.devsrsouza.kotlinx.serialization.sql.PrimaryKey
import br.com.devsrsouza.kotlinx.serialization.sql.Unique
import br.com.devsrsouza.kotlinx.serialization.sql.WhereOperator.eq
import br.com.devsrsouza.kotlinx.serialization.sql.WhereOperator.greater
import br.com.devsrsouza.kotlinx.serialization.sql.entity
import br.com.devsrsouza.kotlinx.serialization.sql.statements.*
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @PrimaryKey @AutoIncrement val id: Int,
    @Unique val uuid: String,
    @Unique val nickname: String,
    val group: String?
)

fun main() {
    println(createTable(Player.serializer()))

    val player = Player(
        5, "0000-00-0000-00-0000", "SrSouza", null
    )

    val player2 = player.copy(nickname = "Joao")

    println(insertQuery(Player.serializer(),player))

    println(updateQuery(
        Player.serializer(), player, listOf(Player::uuid), listOf(Player::nickname)
    ))

    println(
        updateKeyQuery(
            Player.serializer(), player2, listOf(Player::nickname)
        )
    )

    val select = selectQuery(
        Player.serializer(),
        where = listOf(Player::id.entity.where(eq, 5))
    )
    println(select.first)

    val select2 = selectQuery(
        Player.serializer(),
        where = listOf(Player::id.entity.where(greater, 5))
    )

    println(select2.first)

    val dummyResult = listOf(
        mapOf(
            "id" to 5,
            "uuid" to "0000-00-0000-00-0000",
            "nickname" to "Joao",
            "group" to null
        )
    )

    println(
        select.second(
            dummyResult
        )
    )

}