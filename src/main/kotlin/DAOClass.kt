import io.ktor.http.HttpStatusCode

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

object PuppetClass:DAOInterface
{
    override fun GetFullUserData(userID:Int): UserFullData {
        if (userID == 1)
        return TestingPuppet.fullData
        else throw Exception(CallException(404, "Wrong UserID"))
    }
    override fun GetUserData(userID:Int): UserPublicData {
        if (userID == 1)
            return TestingPuppet.publicData
        else throw Exception(CallException(404, "Wrong UserID"))
    }
    override fun GetFullUserList(page:Int, limit:Int): List<UserFullData> {
        return TestingPuppet.fullPuppetList
    }
    override fun GetUserList(page:Int, limit:Int): List<UserPublicData> {
        return TestingPuppet.puppetList
    }

    override fun Insert(email:String,username:String, password:String): HttpStatusCode {
        when {
            !isEmailValid(email) -> return HttpStatusCode(400, "Email Is Not Valid")
            username.length < 6 -> return HttpStatusCode(400, "UserName Is Not Valid")
            password.length < 6 -> return HttpStatusCode(400, "Password Is Not Valid")
            else -> return HttpStatusCode.OK
        }
    }

    override fun Update(userID:Int, dataType:List<String>, newData:List<String>): HttpStatusCode {
        dataType.forEachIndexed{index, it ->
            if (TestingPuppet.dataTypes.contains(it)==false) throw CallException(400,"Wrong DataType")
            if (it=="email"){
                if (!isEmailValid(newData[index])) throw CallException(400,"Email Is Not Valid")
            }}
        when {
            userID != 1 -> return HttpStatusCode(404, "Wrong UserID")
            dataType.lastIndex!=newData.lastIndex -> throw CallException(400, "Lists Are Not Equal")
            else -> return HttpStatusCode.OK
        }
    }

    override fun Delete(userID:Int): HttpStatusCode {
        if (userID == 1)
            return HttpStatusCode.OK
        else return HttpStatusCode(404, "Wrong UserID")
    }

    override fun CheckPass(userID: Int, password: String): Boolean {
        if(password == TestingPuppet.pass) return true
        else return false
}

    override fun GetUserID(email: String): Int {
        if (email == "example@email.pp")
            return TestingPuppet.fullData.userID!!
        else throw CallException(404, "User Not Found")
    }
}


