import io.ktor.http.HttpStatusCode

interface DAOInterface {
    fun GetFullUserData(userID:Int):UserFullData
    fun GetUserData(userID:Int): UserPublicData
    fun GetUserList(page:Int, limit:Int) : List<UserPublicData>
    fun GetFullUserList(page:Int, limit:Int) : List<UserFullData>
    fun Update(userID:Int, dataType:List<String>, newData:List<String>) : HttpStatusCode
    fun Delete(userID:Int) : HttpStatusCode
    fun Insert(email:String,username:String, password:String) : HttpStatusCode
    fun GetUserID(email: String): Int
    fun CheckPass(userID: Int,password: String): Boolean
    fun Login(email:String, password:String): SessionData
}