
import io.ktor.http.HttpStatusCode
import org.junit.Test

object BaseClass:DAOInterface
{
    override fun GetFullUserData(userID:Int): UserFullData { return SQLGetFullUserData(userID) }
    override fun GetUserData(userID:Int): UserPublicData { return SQLGetUserData(userID) }
    override fun GetFullUserList(page:Int, limit:Int): List<UserFullData> { return SQLGetFullUsers(page, limit)}
    override fun GetUserList(page:Int, limit:Int): List<UserPublicData> { return SQLGetUsers(page, limit)}
    override fun Insert(email:String,username:String, password:String): HttpStatusCode { return SQLInsert(email,username, password)}
    override fun Update(userID:Int, dataType:List<String>, newData:List<String>): HttpStatusCode { return SQLUpdate(userID,dataType,newData)}
    override fun Delete(userID:Int): HttpStatusCode { return SQLDelete(userID) }
    override fun CheckPass(userID: Int, password: String): Boolean { return SQLCheckPass(userID, password)}
    override fun GetUserID(email: String): Int { return SQLGetID(email) }
}

object PuppetClass:DAOInterface {
    override fun GetFullUserData(userID: Int): UserFullData {
        if (userID !in 1..5) {
            throw CallException(404, "User not found")
        }
        return UserFullData(
                userID,
                when (userID) {
                    1 -> "user@email.ru"
                    2 -> "moder@email.ru"
                    3 -> "admin@email.ru"
                    4 -> "banned@email.ru"
                    5 -> "deleted@email.ru"
                    else -> throw CallException(404, "User not found")
                },
                TestingPuppet.fullData.userName,
                TestingPuppet.fullData.avatarURL,
                when (userID) {
                    1 -> "User"
                    2 -> "Moder"
                    3 -> "Admin"
                    4 -> "BANNED"
                    5 -> "DELETED"
                    else -> throw CallException(404, "User not found")
                },
                TestingPuppet.fullData.mute)

    }

    override fun GetUserData(userID: Int): UserPublicData {
        if (userID !in 1..5) {
            throw CallException(404, "User not found")
        }
        return UserPublicData(
                when (userID) {
                    1 -> "user@email.ru"
                    2 -> "moder@email.ru"
                    3 -> "admin@email.ru"
                    4 -> "banned@email.ru"
                    5 -> "deleted@email.ru"
                    else -> throw CallException(404, "User not found")
                },
                TestingPuppet.fullData.userName,
                TestingPuppet.fullData.avatarURL,
                when (userID) {
                    1 -> "User"
                    2 -> "Moder"
                    3 -> "Admin"
                    4 -> "BANNED"
                    5 -> "DELETED"
                    else -> throw CallException(404, "User not found")
                },
                TestingPuppet.fullData.mute)

    }

    override fun GetFullUserList(page: Int, limit: Int): List<UserFullData> {
        return TestingPuppet.fullPuppetList
    }

    override fun GetUserList(page: Int, limit: Int): List<UserPublicData> {
        return TestingPuppet.puppetList
    }

    override fun Insert(email: String, username: String, password: String): HttpStatusCode {
        return HttpStatusCode.Created
    }

    override fun Update(userID: Int, dataType: List<String>, newData: List<String>): HttpStatusCode {
        dataType.forEachIndexed { index, it ->
            if (TestingPuppet.dataTypes.contains(it) == false) throw CallException(400, "Wrong DataType")
            if (it == "email" && !isEmailValid(newData[index])) {
                throw CallException(400, "Email Is Not Valid")
            }
        }
        when {
            userID !in 1..5 -> throw CallException(404, "User not found")
            else -> return HttpStatusCode.OK
        }
    }

    override fun Delete(userID: Int): HttpStatusCode {
        if (userID in 1..5)
            return HttpStatusCode.OK
        else throw CallException(404, "User not found")
    }

    override fun CheckPass(userID: Int, password: String): Boolean {
        if (password == TestingPuppet.pass) {
            return true
        } else return false
    }

    override fun GetUserID(email: String): Int {
        when (email) {
            "user@email.ru" -> return 1
            "moder@email.ru" -> return 2
            "admin@email.ru" -> return 3
            "banned@email.ru" -> return 4
            "deleted@email.ru" -> return 5
            else -> throw CallException(404, "Email Not Found")
        }
    }
}
