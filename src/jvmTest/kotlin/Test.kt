import br.com.devsrsouza.kotlinx.serialization.sql.AutoIncrement
import br.com.devsrsouza.kotlinx.serialization.sql.PrimaryKey
import br.com.devsrsouza.kotlinx.serialization.sql.Unique
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

    val id = Where(Player::id, 5)
    val select = selectQuery(
        Player.serializer(),
        where = listOf(id)
    )
    println(
        select.first
    )

    val dummyResult = mapOf(
        "id" to 5,
        "uuid" to "0000-00-0000-00-0000",
        "nickname" to "Joao",
        "group" to null
    )

    println(
        select.second(
            listOf(dummyResult)
        )
    )

}