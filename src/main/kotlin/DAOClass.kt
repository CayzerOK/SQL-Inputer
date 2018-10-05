
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
    override fun Login(email:String, password:String):SessionData { return SQLLogin(email, password)}
}

object PuppetClass:DAOInterface {
    override fun GetFullUserData(userID: Int): UserFullData {
        if (userID !in 1..5) {throw CallException(404, "Wrong UserID")}
            return UserFullData(
                    userID,
                    when(userID) {
                        1 -> "user@email.ru"
                        2 -> "moder@email.ru"
                        3 -> "admin@email.ru"
                        4 -> "banned@email.ru"
                        5 -> "deleted@email.ru"
                        else-> throw CallException(404, "Wrong UserID") },
                    TestingPuppet.fullData.userName,
                    TestingPuppet.fullData.avatarURL,
                    when(userID) {
                        1 -> "User"
                        2 -> "Moder"
                        3 -> "Admin"
                        4 -> "BANNED"
                        5 -> "DELETED"
                        else-> throw CallException(404, "Wrong UserID") },
                    TestingPuppet.fullData.mute)

    }

    override fun GetUserData(userID: Int): UserPublicData {
        if (userID !in 1..5) {throw CallException(404, "Wrong UserID")}
        return UserPublicData(
                when(userID) {
                    1 -> "user@email.ru"
                    2 -> "moder@email.ru"
                    3 -> "admin@email.ru"
                    4 -> throw CallException(400, "Banned")
                    5 -> "deleted@email.ru"
                    else-> throw CallException(404, "Wrong UserID") },
                TestingPuppet.fullData.userName,
                TestingPuppet.fullData.avatarURL,
                when(userID) {
                    1 -> "User"
                    2 -> "Moder"
                    3 -> "Admin"
                    else-> throw CallException(404, "Wrong UserID") },
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
            if (it=="email" && !isEmailValid(newData[index])) {throw CallException(400, "Email Is Not Valid")}
        }
        when {
            userID !in 1..5 -> throw CallException(404, "Wrong UserID")
            dataType.lastIndex != newData.lastIndex -> throw CallException(400, "Lists Are Not Equal")
            else -> return HttpStatusCode.OK
        }
    }

    override fun Delete(userID: Int): HttpStatusCode {
        if (userID in 1..5)
            return HttpStatusCode.OK
        else throw CallException(404, "Wrong UserID")
    }

    override fun CheckPass(userID: Int, password: String): Boolean {
        if(password == TestingPuppet.pass) {return true}
        else return false
    }

    override fun GetUserID(email: String): Int {
        when(email) {
            "user@email.ru" -> return 1
            "moder@email.ru" -> return 2
            "admin@email.ru" -> return 3
            "banned@email.ru" -> return 4
            "deleted@email.ru" -> return 5
            else -> throw CallException(404, "User Not Found")
        }
    }

    override fun Login(email: String, password: String): SessionData {
        val userData = SQL.GetFullUserData(SQL.GetUserID(email))
        if (!SQL.CheckPass(userData.userID!!, password)) {
            throw CallException(400, "Wrong Password")
        }
        when (userData.role) {
            "BANNED" -> throw CallException(410, "Banned")
            "DELETED" -> throw CallException(410, "Deleted")
            else -> return SessionData(userData.userID, userData.role!!)
        }
    }
}
