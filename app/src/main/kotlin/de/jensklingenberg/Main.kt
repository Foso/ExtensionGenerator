package de.jensklingenberg

import de.jensklingenberg.extensiongenerator.annotation.Extension

data class OtherUser(
    val name: String,
    val email: String
    )


data class SapUser(
    val name: String,
    val email: String,
    val test: Int

)

@Extension(to = [SapUser::class,OtherUser::class])
interface Help{
    val name: String
    val email: String
    val test: Int
}

@Extension(to = [SapUser::class])
data class User(
    val name: String,
    val email: String,
    val test: Int
)

fun main(args: Array<String>) {

    val user = User(
        name = "Test",
        email = "test@email.com",
        test = 1
    )

   print(user.toSapUser().name)

}

