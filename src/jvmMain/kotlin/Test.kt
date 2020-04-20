import br.com.devsrsouza.kotlinx.serialization.sql.AutoIncrement
import br.com.devsrsouza.kotlinx.serialization.sql.PrimaryKey
import br.com.devsrsouza.kotlinx.serialization.sql.Unique
import br.com.devsrsouza.kotlinx.serialization.sql.statements.createTable
import br.com.devsrsouza.kotlinx.serialization.sql.statements.insertQuery
import br.com.devsrsouza.kotlinx.serialization.sql.statements.updateKeyQuery
import br.com.devsrsouza.kotlinx.serialization.sql.statements.updateQuery
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @PrimaryKey @AutoIncrement val id: Int,
    @Unique val uuid: String,
    @Unique val nickname: String,
    val group: String? = null
)

fun main() {
    println(createTable(Player.serializer()))

    val player = Player(
        5, "0000-00-0000-00-0000", "SrSouza"
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
}